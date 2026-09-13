package com.finverse.ui

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.finverse.FinverseApp
import com.finverse.data.model.AIFileAttachment
import com.finverse.data.model.AIRequest
import com.finverse.data.model.FinancialGoal
import com.finverse.data.model.FinancialProfile
import com.finverse.data.repository.FinancialRepository
import com.finverse.domain.FinancialCalculationEngine
import com.finverse.domain.TwinScenario
import com.finverse.network.AIApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

sealed class UIState {
    object Idle : UIState()
    object Loading : UIState()
    data class Success(val message: String) : UIState()
    data class Error(val error: String) : UIState()
}

class FinverseViewModel(
    application: Application,
    private val repository: FinancialRepository,
    val calculationEngine: FinancialCalculationEngine,
    private val aiApiService: AIApiService
) : AndroidViewModel(application) {

    val profile: StateFlow<FinancialProfile> = repository.profile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FinancialProfile()
    )

    val goals: StateFlow<List<FinancialGoal>> = repository.goals.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _uiState = MutableStateFlow<UIState>(UIState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _aiResponse = MutableStateFlow<String?>(null)
    val aiResponse = _aiResponse.asStateFlow()
    
    private val _lastQuestion = MutableStateFlow<String?>(null)
    val lastQuestion = _lastQuestion.asStateFlow()

    private val _aiAudioBase64 = MutableStateFlow<String?>(null)
    val aiAudioBase64 = _aiAudioBase64.asStateFlow()

    private val _isPlayingAudio = MutableStateFlow(false)
    val isPlayingAudio = _isPlayingAudio.asStateFlow()

    private val _currentPlayingId = MutableStateFlow<String?>(null)
    val currentPlayingId = _currentPlayingId.asStateFlow()

    private val _twinScenario = MutableStateFlow(TwinScenario.EXPECTED)
    val twinScenario = _twinScenario.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("en", "IN")
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) { 
                        _isPlayingAudio.value = true 
                        _currentPlayingId.value = utteranceId
                    }
                    override fun onDone(utteranceId: String?) { 
                        _isPlayingAudio.value = false 
                        _currentPlayingId.value = null
                    }
                    override fun onError(utteranceId: String?) { 
                        _isPlayingAudio.value = false 
                        _currentPlayingId.value = null
                    }
                })
            }
        }
    }

    fun setTwinScenario(scenario: TwinScenario) {
        _twinScenario.value = scenario
    }

    fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        format.maximumFractionDigits = 0
        return format.format(amount)
    }

    fun saveProfile(profile: FinancialProfile) {
        viewModelScope.launch {
            repository.saveProfile(profile)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            val current = profile.value
            repository.saveProfile(current.copy(hasCompletedOnboarding = true, isLoggedIn = true))
        }
    }

    fun logout() {
        viewModelScope.launch {
            stopAiAudio()
            repository.logout()
        }
    }

    fun login(name: String, email: String) {
        viewModelScope.launch {
            val current = profile.value
            repository.saveProfile(
                current.copy(
                    userName = name.ifBlank { "User" },
                    userEmail = email.ifBlank { "user@example.com" },
                    isLoggedIn = true,
                    hasCompletedOnboarding = true
                )
            )
        }
    }

    fun loadDemoData() {
        viewModelScope.launch {
            repository.loadDemoData()
        }
    }

    fun saveGoal(goal: FinancialGoal) {
        viewModelScope.launch {
            repository.saveGoal(goal)
        }
    }

    fun deleteGoal(id: Int) {
        viewModelScope.launch {
            repository.deleteGoal(id)
        }
    }

    fun askAdvisor(question: String, imageBytes: ByteArray? = null, mimeType: String = "image/jpeg") {
        viewModelScope.launch {
            _uiState.value = UIState.Loading
            _lastQuestion.value = question
            stopAiAudio()
            _aiAudioBase64.value = null
            
            val currentProfile = profile.value
            val monthlySavings = currentProfile.monthlyIncome - currentProfile.essentialExpenses - currentProfile.discretionaryExpenses - currentProfile.emi
            val health = calculationEngine.calculateHealthScore(currentProfile)
            
            try {
                val contextPrompt = """
                    You are Tech Dev & Finverse AI, an intelligent voice and financial assistant.
                    
                    User Financial Context:
                    - Name: ${currentProfile.userName}
                    - Monthly Income: ₹${currentProfile.monthlyIncome}
                    - Essential Living Expenses: ₹${currentProfile.essentialExpenses}
                    - Discretionary Spends: ₹${currentProfile.discretionaryExpenses}
                    - Monthly EMI / Debt Obligations: ₹${currentProfile.emi}
                    - Net Monthly Savings: ₹$monthlySavings
                    - Total Liquid Savings: ₹${currentProfile.currentSavings}
                    - Emergency Fund: ₹${currentProfile.emergencyFund}
                    - Investments: ₹${currentProfile.currentInvestments}
                    - Total Outstanding Loans: ₹${currentProfile.outstandingLoanAmount}
                    - Financial Health Score: ${health.overall}/100
                    
                    Instructions for Tech Dev & Finverse AI:
                    1. Address the user directly by their name (${currentProfile.userName}).
                    2. Give a sharp, encouraging, clear, and actionable financial recommendation.
                    3. Format the response using markdown (like **bold**, ### headers).
                    
                    User Inquiry: $question
                """.trimIndent()

                val fileAttachments = if (imageBytes != null && imageBytes.isNotEmpty()) {
                    val imgBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    listOf(AIFileAttachment(mimeType = mimeType, data = imgBase64))
                } else {
                    null
                }

                val response = aiApiService.generateAdvice(
                    AIRequest(prompt = contextPrompt, files = fileAttachments)
                )

                if (response.isSuccessful && response.body()?.success == true && response.body()?.text != null) {
                    val responseBody = response.body()!!
                    _aiResponse.value = responseBody.text
                    _aiAudioBase64.value = responseBody.audioBase64
                    _uiState.value = UIState.Success("Advice generated")
                    
                    playAiAudio() // Auto play audio voice
                } else {
                    // Smart built-in financial intelligence fallback
                    _aiResponse.value = generateLocalFinancialAdvice(question, currentProfile, monthlySavings, health.overall)
                    _uiState.value = UIState.Success("Advice generated")
                    playAiAudio()
                }
            } catch (e: Exception) {
                // Graceful local financial intelligence fallback when offline
                _aiResponse.value = "Hi ${currentProfile.userName}, it looks like you are offline. Tech Dev & Finverse AI requires an internet connection to process complex queries.\n\n" + generateLocalFinancialAdvice(question, currentProfile, monthlySavings, health.overall)
                _uiState.value = UIState.Error("Network Error: Tech Dev & Finverse AI is offline.")
                playAiAudio()
            }
        }
    }

    fun playAiAudio() {
        stopAiAudio()
        val b64 = _aiAudioBase64.value
        if (!b64.isNullOrBlank()) {
            try {
                val audioBytes = Base64.decode(b64, Base64.DEFAULT)
                val tempFile = File.createTempFile("bol_ai_response_", ".wav", getApplication<Application>().cacheDir)
                tempFile.writeBytes(audioBytes)
                
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(tempFile.absolutePath)
                    prepare()
                    setOnCompletionListener {
                        _isPlayingAudio.value = false
                        _currentPlayingId.value = null
                    }
                    start()
                }
                _isPlayingAudio.value = true
                _currentPlayingId.value = "bol_ai_audio"
                return
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Fallback to TTS if no audioBase64 or if MediaPlayer failed
        val responseText = _aiResponse.value ?: return
        speakCustomText(responseText, id = "advisor_response")
    }

    fun speakCustomText(text: String, id: String = "custom_speech") {
        if (_isPlayingAudio.value && _currentPlayingId.value == id) {
            stopAiAudio()
            return
        }

        stopAiAudio()
        try {
            val cleanText = text.replace(Regex("[*#\\[\\]`_]"), " ")
                .replace("₹", "Rupees ")
                .replace("->", " to ")
            
            _isPlayingAudio.value = true
            _currentPlayingId.value = id
            tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, id)
        } catch (e: Exception) {
            e.printStackTrace()
            _isPlayingAudio.value = false
            _currentPlayingId.value = null
        }
    }

    fun stopAiAudio() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
        }

        try {
            tts?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        _isPlayingAudio.value = false
        _currentPlayingId.value = null
    }

    override fun onCleared() {
        super.onCleared()
        stopAiAudio()
        tts?.shutdown()
    }

    private fun generateLocalFinancialAdvice(question: String, p: FinancialProfile, monthlySavings: Double, healthScore: Int): String {
        val q = question.lowercase()
        return when {
            q.contains("score") || q.contains("health") || q.contains("improve") -> {
                """
                **Financial Health Assessment (Score: $healthScore/100)**
                
                1. **Savings Rate:** You are saving ${if (p.monthlyIncome > 0) String.format(Locale.US, "%.1f%%", (monthlySavings / p.monthlyIncome) * 100) else "0%"} of income. Aim for at least 20-30%.
                2. **Emergency Fund:** You currently hold ${formatCurrency(p.emergencyFund)}. We recommend at least 6 months of essential expenses (${formatCurrency(p.essentialExpenses * 6)}).
                3. **Debt Management:** Your monthly EMI is ${formatCurrency(p.emi)}. Keep your debt-to-income ratio below 35%.
                4. **Action Item:** Automate transfers to your investment accounts on salary day.
                """.trimIndent()
            }
            q.contains("car") || q.contains("afford") || q.contains("buy") || q.contains("purchase") -> {
                val liquidSavings = p.currentSavings
                """
                **Affordability Analysis**
                
                - **Monthly Free Cash Flow:** ${formatCurrency(monthlySavings)}
                - **Liquid Savings (excluding emergency):** ${formatCurrency(liquidSavings)}
                - **Rule of Thumb (20/4/10):** Put down at least 20%, finance for no more than 4 years, and total auto costs (EMI + fuel + insurance) should stay under 10% of monthly income (${formatCurrency(p.monthlyIncome * 0.10)}).
                - **Recommendation:** If purchasing via EMI, ensure your new monthly payment does not reduce your savings below ${formatCurrency(p.monthlyIncome * 0.15)}.
                """.trimIndent()
            }
            q.contains("emergency") || q.contains("fund") || q.contains("save") -> {
                val targetEmergency = p.essentialExpenses * 6
                val gap = targetEmergency - p.emergencyFund
                """
                **Emergency Fund Strategy**
                
                - **Target Fund (6 months):** ${formatCurrency(targetEmergency)}
                - **Current Reserve:** ${formatCurrency(p.emergencyFund)}
                - **Gap to Target:** ${if (gap > 0) formatCurrency(gap) else "Fully Funded!"}
                - **Recommendation:** Keep emergency money in high-yield liquid instruments or fixed deposits for instant accessibility.
                """.trimIndent()
            }
            else -> {
                """
                **Finverse Tech Dev AI Intelligence Insight**
                
                Based on your current profile:
                - **Net Worth:** ${formatCurrency(p.currentSavings + p.currentInvestments + p.emergencyFund - p.outstandingLoanAmount)}
                - **Monthly Net Cashflow:** ${formatCurrency(monthlySavings)}
                - **Health Score:** $healthScore / 100
                
                **Key Recommendation:**
                Prioritize building a 6-month safety buffer (${formatCurrency(p.essentialExpenses * 6)}) while investing ${formatCurrency((monthlySavings * 0.7).coerceAtLeast(0.0))} monthly into diversified growth assets.
                """.trimIndent()
            }
        }
    }
    
    fun resetAiState() {
        stopAiAudio()
        _aiResponse.value = null
        _lastQuestion.value = null
        _aiAudioBase64.value = null
        _uiState.value = UIState.Idle
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as FinverseApp
                return FinverseViewModel(
                    application,
                    application.container.financialRepository,
                    application.container.calculationEngine,
                    application.container.aiApiService
                ) as T
            }
        }
    }
}
