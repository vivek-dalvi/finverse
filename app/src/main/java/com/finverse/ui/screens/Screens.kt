package com.finverse.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.finverse.data.model.FinancialGoal
import com.finverse.data.model.FinancialProfile
import com.finverse.domain.AiFinancialActionPlan
import com.finverse.domain.HealthScore
import com.finverse.domain.HealthScoreItem
import com.finverse.domain.LifeEventCalculation
import com.finverse.domain.PrePurchaseComparison
import com.finverse.domain.Projection
import com.finverse.domain.PurchaseVerdict
import com.finverse.domain.TwinScenario
import com.finverse.ui.FinverseViewModel
import com.finverse.ui.UIState
import com.finverse.ui.components.*
import com.finverse.ui.theme.*
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import java.io.ByteArrayOutputStream

@Composable
fun MarkdownTextFormatter(text: String, modifier: Modifier = Modifier) {
    val annotatedString = buildAnnotatedString {
        var isBold = false
        val parts = text.split("**")
        val onSurfaceColor = MaterialTheme.colorScheme.onSurface
        
        parts.forEachIndexed { index, part ->
            if (isBold) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = onSurfaceColor)) {
                    append(part)
                }
            } else {
                // Check for headers
                if (part.startsWith("### ")) {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ElectricBlue)) {
                        append(part.removePrefix("### "))
                    }
                } else if (part.startsWith("## ")) {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp, color = ElectricBlue)) {
                        append(part.removePrefix("## "))
                    }
                } else {
                    withStyle(style = SpanStyle(color = onSurfaceColor.copy(alpha = 0.9f))) {
                        append(part)
                    }
                }
            }
            if (index < parts.size - 1) {
                isBold = !isBold
            }
        }
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyLarge,
        lineHeight = 24.sp,
        modifier = modifier
    )
}


// ==========================================
// 1. WELCOME SCREEN (3D MODERN HERO & LOGIN)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(navController: NavController, viewModel: FinverseViewModel) {
    val profile by viewModel.profile.collectAsState()
    var showLoginDialog by remember { mutableStateOf(false) }
    var loginNameInput by remember { mutableStateOf("") }

    if (showLoginDialog) {
        AlertDialog(
            onDismissRequest = { showLoginDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FinverseLogoBadge(size = 32.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Finverse Sign In", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Enter your name to access or initialize your personalized financial twin.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextSecondary
                    )
                    OutlinedTextField(
                        value = loginNameInput,
                        onValueChange = { loginNameInput = it },
                        label = { Text("Your Name") },
                        placeholder = { Text("Your Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.login(loginNameInput, "")
                        showLoginDialog = false
                        navController.navigate("dashboard") { popUpTo("welcome") { inclusive = true } }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Continue", fontWeight = FontWeight.Bold, color = PureWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF000000),
                        Color(0xFF000000)
                    )
                )
            )
            .padding(horizontal = 22.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // 3D Animated Glowing Finverse Logo with Orbital Cosmic Rings
            Finverse3DHeroBadge(size = 100.dp)

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Finverse",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = PureWhite,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = ElectricBlue.copy(alpha = 0.2f),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(listOf(ElectricBlueLight, CyanNeon))
                )
            ) {
                Text(
                    text = "3D Financial Intelligence & Twin",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanNeonLight,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Simulate your 20-year wealth decisions, audit spending reliability, and converse with Finverse AI Financial Voice Assistant.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Slate400,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 3D Floating Interactive Metric Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Floating3DMetricCard(
                    title = "Health Score",
                    value = "88/100 A+",
                    icon = Icons.Default.Shield,
                    accentColor = EmeraldNeon,
                    modifier = Modifier.weight(1f),
                    delayMs = 0
                )
                Floating3DMetricCard(
                    title = "Monthly Surplus",
                    value = "+₹42,500/mo",
                    icon = Icons.Default.TrendingUp,
                    accentColor = CyanNeon,
                    modifier = Modifier.weight(1f),
                    delayMs = 300
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Floating3DMetricCard(
                    title = "20-Yr Twin",
                    value = "Compounding",
                    icon = Icons.Default.ShowChart,
                    accentColor = VioletNeon,
                    modifier = Modifier.weight(1f),
                    delayMs = 600
                )
                Floating3DMetricCard(
                    title = "Finverse AI Voice",
                    value = "Live Intelligence",
                    icon = Icons.AutoMirrored.Filled.Chat,
                    accentColor = AmberGold,
                    modifier = Modifier.weight(1f),
                    delayMs = 900
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { navController.navigate("profile") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(12.dp, RoundedCornerShape(16.dp), ambientColor = ElectricBlue, spotColor = CyanNeon),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = PureWhite, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Get Started (Set Up Profile)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PureWhite)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.loadDemoData()
                        navController.navigate("dashboard") { popUpTo("welcome") { inclusive = true } }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(CyanNeon, ElectricBlueLight)),
                        width = 1.2.dp
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White.copy(alpha = 0.05f))
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, tint = CyanNeonLight, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Explore Demo", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                }

                OutlinedButton(
                    onClick = {
                        loginNameInput = profile.userName
                        showLoginDialog = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(VioletNeon, SunsetPink)),
                        width = 1.2.dp
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White.copy(alpha = 0.05f))
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = VioletNeon, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("User Sign In", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                }
            }

            TextButton(
                onClick = { navController.navigate("guide") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = CyanNeonLight, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("App Guide & Walkthrough (How It Works)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CyanNeonLight)
            }
        }
    }
}

// ==========================================
// 2. DASHBOARD SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, viewModel: FinverseViewModel) {
    val profile by viewModel.profile.collectAsState()
    val isPlayingAudio by viewModel.isPlayingAudio.collectAsState()
    val currentPlayingId by viewModel.currentPlayingId.collectAsState()
    val healthScore = viewModel.calculationEngine.calculateHealthScore(profile)
    var showScoreExplainerDialog by remember { mutableStateOf(false) }

    val netWorth = profile.currentSavings + profile.currentInvestments + profile.emergencyFund - profile.outstandingLoanAmount
    val monthlySavings = profile.monthlyIncome - profile.essentialExpenses - profile.discretionaryExpenses - profile.emi

    val totalExp = profile.essentialExpenses + profile.discretionaryExpenses + (if (monthlySavings > 0) monthlySavings else 0.0)
    val needsPct = if (totalExp > 0) (profile.essentialExpenses / totalExp).toFloat() else 0.5f
    val wantsPct = if (totalExp > 0) (profile.discretionaryExpenses / totalExp).toFloat() else 0.3f
    val savingsPct = if (totalExp > 0 && monthlySavings > 0) (monthlySavings / totalExp).toFloat() else 0.2f

    val actionPlan = remember(profile) {
        viewModel.calculationEngine.generateActionPlan(profile)
    }

    val aiInsightText = remember(profile, monthlySavings) {
        val months = if (profile.essentialExpenses > 0) {
            String.format(java.util.Locale.US, "%.1f", profile.emergencyFund / profile.essentialExpenses)
        } else "0.0"
        val targetEmergency = viewModel.formatCurrency(profile.essentialExpenses * 6)
        val surplusStr = viewModel.formatCurrency(monthlySavings)
        "Your monthly surplus is $surplusStr, but your emergency fund covers only about $months months of essential expenses. Before increasing investments, consider building your emergency fund to $targetEmergency."
    }

    val aiRecommendationText = "If you invest ₹10,000 more per month, your projected 10-year wealth could increase by approximately ₹20.5 Lakh, assuming a 10% annual return."

    if (showScoreExplainerDialog) {
        ScoreExplainerDialog(onDismiss = { showScoreExplainerDialog = false })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FinverseLogoBadge(size = 36.dp, iconPadding = 0.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Finverse", fontWeight = FontWeight.Black, fontSize = 19.sp)
                            Text(
                                if (profile.userName.isNotBlank()) "Hi, ${profile.userName}" else "Financial Intelligence",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("guide") }) {
                        Icon(Icons.Default.MenuBook, contentDescription = "Guide", tint = CyanNeon)
                    }
                    IconButton(onClick = { navController.navigate("advisor") }) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "AI Voice Advisor", tint = ElectricBlue)
                    }
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Demo Mode Tag
            if (profile.isDemoData) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = VioletContainer,
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(VioletNeon, CyanNeon)))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoGraph, contentDescription = null, tint = VioletOnContainer, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Demo Simulation Mode", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VioletOnContainer)
                            }
                            TextButton(
                                onClick = { navController.navigate("profile") },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                            ) {
                                Text("Edit Real Profile", fontSize = 12.sp, fontWeight = FontWeight.Black, color = ElectricBlue)
                            }
                        }
                    }
                }
            }

            // Health Score Hero Card with Detailed Breakdown
            item {
                HealthScoreHeroCard(
                    healthScore = healthScore,
                    onExplainClick = { showScoreExplainerDialog = true }
                )
            }

            // 1. AI Financial Advisor Insight Card (with Voice Output)
            item {
                AiAdvisorInsightCard(
                    insight = aiInsightText,
                    recommendation = aiRecommendationText,
                    isPlaying = isPlayingAudio && currentPlayingId == "ai_insight_speech",
                    onVoiceToggle = {
                        val speech = "$aiInsightText $aiRecommendationText"
                        viewModel.speakCustomText(speech, id = "ai_insight_speech")
                    }
                )
            }

            // 2. AI Financial Action Plan Card (3 Actions with Voice Output)
            item {
                AiActionPlanCard(
                    actionPlan = actionPlan,
                    isPlaying = isPlayingAudio && currentPlayingId == "action_plan_speech",
                    onVoiceToggle = {
                        val speechBuilder = StringBuilder("Here is your Finverse Action Plan. ")
                        actionPlan.actions.forEach {
                            speechBuilder.append("${it.title}. Recommended: ${it.recommendation}. ")
                        }
                        speechBuilder.append("Expected result: Health score increases from ${actionPlan.expectedCurrentScore} to ${actionPlan.expectedProjectedScore}.")
                        viewModel.speakCustomText(speechBuilder.toString(), id = "action_plan_speech")
                    }
                )
            }

            // Key Financial Metrics (Net Worth & Cashflow)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ColorfulMetricCard(
                        title = "Net Worth",
                        amount = viewModel.formatCurrency(netWorth),
                        subtitle = "Assets - Liabilities",
                        icon = Icons.Default.AccountBalance,
                        gradient = listOf(ElectricBlue, ElectricBlueDark),
                        modifier = Modifier.weight(1f)
                    )
                    ColorfulMetricCard(
                        title = "Monthly Cashflow",
                        amount = viewModel.formatCurrency(monthlySavings),
                        subtitle = if (monthlySavings >= 0) "Net Monthly Surplus" else "Monthly Deficit",
                        icon = if (monthlySavings >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        gradient = if (monthlySavings >= 0) listOf(EmeraldNeon, Color(0xFF047857)) else listOf(SunsetPink, Color(0xFFBE123C)),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 50/30/20 Budget Breakdown Card
            item {
                ModernCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Monthly Cashflow Distribution", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            "Income: ${viewModel.formatCurrency(profile.monthlyIncome)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElectricBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    BudgetSplitBar(needsPct = needsPct, wantsPct = wantsPct, savingsPct = savingsPct)
                }
            }

            // Section: Intelligence Suite
            item {
                Text(
                    "Financial Intelligence Suite",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )
            }

            // Colorful Quick Action Cards
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ColorfulActionTile(
                            title = "Financial Twin",
                            subtitle = "20-Yr Wealth Projection",
                            icon = Icons.Default.ShowChart,
                            gradientColors = listOf(ElectricBlue, CyanNeon),
                            modifier = Modifier.weight(1f)
                        ) { navController.navigate("twin") }

                        ColorfulActionTile(
                            title = "What-If Lab",
                            subtitle = "Simulate Decisions",
                            icon = Icons.AutoMirrored.Filled.CompareArrows,
                            gradientColors = listOf(VioletNeon, SunsetPink),
                            modifier = Modifier.weight(1f)
                        ) { navController.navigate("whatif") }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ColorfulActionTile(
                            title = "Smart Goals",
                            subtitle = "Track Milestones",
                            icon = Icons.Default.Flag,
                            gradientColors = listOf(EmeraldNeon, Color(0xFF059669)),
                            modifier = Modifier.weight(1f)
                        ) { navController.navigate("goals") }

                        ColorfulActionTile(
                            title = "Pre-Purchase",
                            subtitle = "Affordability Verdict",
                            icon = Icons.Default.ShoppingCart,
                            gradientColors = listOf(AmberGold, Color(0xFFEA580C)),
                            modifier = Modifier.weight(1f)
                        ) { navController.navigate("purchase") }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ColorfulActionTile(
                            title = "Data Audit",
                            subtitle = "Audit Reliability",
                            icon = Icons.Default.VerifiedUser,
                            gradientColors = listOf(CyanNeon, ElectricBlueLight),
                            modifier = Modifier.weight(1f)
                        ) { navController.navigate("datacheck") }

                        ColorfulActionTile(
                            title = "Finverse AI",
                            subtitle = "Voice & Text Advisor",
                            icon = Icons.AutoMirrored.Filled.Chat,
                            gradientColors = listOf(IndigoRoyal, VioletNeon),
                            modifier = Modifier.weight(1f)
                        ) { navController.navigate("advisor") }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Complete Guide
                        ColorfulActionTile(
                            title = "Finverse Guide & Intro",
                            subtitle = "How Formulas, Health Scores Work",
                            icon = Icons.Default.MenuBook,
                            gradientColors = listOf(CyanNeon, EmeraldNeon),
                            modifier = Modifier.weight(1f)
                        ) { navController.navigate("guide") }
                        
                        ColorfulActionTile(
                            title = "About Finverse",
                            subtitle = "Mission & Security",
                            icon = Icons.Default.Info,
                            gradientColors = listOf(Slate400, SlateTextSecondary),
                            modifier = Modifier.weight(1f)
                        ) { navController.navigate("about") }
                    }
                }
            }

            // Smart Recommendation Tip Card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ElectricBlueContainer.copy(alpha = 0.6f),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(ElectricBlue, CyanNeon)))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Finverse Pro Tip", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ElectricBlueDark)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (profile.emergencyFund < profile.essentialExpenses * 6) {
                                    "Your emergency fund is currently ${viewModel.formatCurrency(profile.emergencyFund)}. Try directing ₹5,000 extra monthly to reach the 6-month safety buffer."
                                } else {
                                    "Great job on funding your emergency buffer! Direct surplus cashflows to equity SIPs to optimize 10-year compounding."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun HealthScoreHeroCard(
    healthScore: HealthScore,
    onExplainClick: () -> Unit
) {
    val gradeText = when {
        healthScore.overall >= 80 -> "Excellent (A+)"
        healthScore.overall >= 65 -> "Good (B)"
        healthScore.overall >= 50 -> "Fair (C)"
        else -> "Needs Attention (D)"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = ElectricBlue.copy(alpha = 0.35f),
                spotColor = CyanNeon.copy(alpha = 0.4f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            ElectricBlueDark,
                            ElectricBlue,
                            Color(0xFF0F52BA)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.4f), Color.Transparent)),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Financial Health Score",
                            color = PureWhite.copy(alpha = 0.85f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PureWhite.copy(alpha = 0.2f),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.6f), Color.Transparent)))
                        ) {
                            Text(
                                text = "Grade: $gradeText",
                                color = PureWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Animated Radial Score Gauge
                    CircularHealthGauge(score = healthScore.overall, size = 80.dp)
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = PureWhite.copy(alpha = 0.2f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(14.dp))

                // Breakdown Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Financial Health Breakdown",
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        "Total: ${healthScore.overall}/100",
                        color = CyanNeonLight,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 5 Detailed Breakdown Items
                HealthBreakdownRow(healthScore.emergencyItem)
                HealthBreakdownRow(healthScore.debtItem)
                HealthBreakdownRow(healthScore.savingItem)
                HealthBreakdownRow(healthScore.investmentItem)
                HealthBreakdownRow(healthScore.insuranceItem)

                Spacer(modifier = Modifier.height(14.dp))

                // "How did AI calculate my score?" Button
                Button(
                    onClick = onExplainClick,
                    colors = ButtonDefaults.buttonColors(containerColor = PureWhite.copy(alpha = 0.16f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Icon(
                        Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "How did AI calculate my score?",
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HealthBreakdownRow(item: HealthScoreItem) {
    Surface(
        color = PureWhite.copy(alpha = 0.08f),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.category,
                        color = PureWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "— ${item.score}/${item.maxScore}",
                        color = CyanNeonLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when {
                            item.isGood -> Icons.Default.CheckCircle
                            item.isWarning -> Icons.Default.Info
                            else -> Icons.Default.Warning
                        },
                        contentDescription = null,
                        tint = when {
                            item.isGood -> EmeraldNeon
                            item.isWarning -> AmberGold
                            else -> SunsetPink
                        },
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.status,
                        color = PureWhite.copy(alpha = 0.88f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ScoreExplainerDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoGraph, contentDescription = null, tint = ElectricBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI Scoring Methodology", fontWeight = FontWeight.Black, fontSize = 17.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Finverse uses an intelligent 100-point safety and wealth-building rubric to grade financial health objectively:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                ExplainerRubricCard(
                    title = "Emergency Fund (25 pts)",
                    desc = "6 months of essential living expenses = 25 pts. Having under 3 months lowers your score.",
                    color = CyanNeon
                )
                ExplainerRubricCard(
                    title = "Debt Management (20 pts)",
                    desc = "EMI & debt under 30% of income = 20 pts. High debt ratios deduct points to protect cashflow.",
                    color = AmberGold
                )
                ExplainerRubricCard(
                    title = "Monthly Savings (20 pts)",
                    desc = "Saving 20-30%+ of monthly income = 20 pts. Sustained surplus fuels future security.",
                    color = EmeraldNeon
                )
                ExplainerRubricCard(
                    title = "Investments & Compounding (20 pts)",
                    desc = "Active investments compounding towards long-term inflation beating targets.",
                    color = VioletNeon
                )
                ExplainerRubricCard(
                    title = "Insurance & Risk Shield (15 pts)",
                    desc = "Essential health and term life insurance to protect family and assets against crises.",
                    color = SunsetPink
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text("Understood", color = PureWhite, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun ExplainerRubricCard(title: String, desc: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
        }
    }
}

@Composable
fun AiAdvisorInsightCard(
    insight: String,
    recommendation: String,
    isPlaying: Boolean,
    onVoiceToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(listOf(ElectricBlue, CyanNeon)),
                    shape = RoundedCornerShape(20.dp)
                )
                .background(ElectricBlueContainer.copy(alpha = 0.25f))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = ElectricBlue.copy(alpha = 0.2f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🤖", fontSize = 16.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Finverse AI Insight",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AudioWaveformVisualizer(
                            isPlaying = isPlaying,
                            modifier = Modifier.size(width = 46.dp, height = 20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = onVoiceToggle,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                if (isPlaying) Icons.Default.PauseCircle else Icons.Default.VolumeUp,
                                contentDescription = "Listen to AI Insight",
                                tint = ElectricBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = insight,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = EmeraldNeon,
                        modifier = Modifier.size(18.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            "AI Recommendation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = EmeraldNeon
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = recommendation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AiActionPlanCard(
    actionPlan: AiFinancialActionPlan,
    isPlaying: Boolean,
    onVoiceToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(listOf(EmeraldNeon, CyanNeon)),
                    shape = RoundedCornerShape(20.dp)
                )
                .background(EmeraldContainer.copy(alpha = 0.2f))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldNeon.copy(alpha = 0.2f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Flag, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Your Financial Action Plan",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AudioWaveformVisualizer(
                            isPlaying = isPlaying,
                            modifier = Modifier.size(width = 46.dp, height = 20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = onVoiceToggle,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                if (isPlaying) Icons.Default.PauseCircle else Icons.Default.VolumeUp,
                                contentDescription = "Listen to Action Plan",
                                tint = EmeraldNeon,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                actionPlan.actions.forEach { action ->
                    ActionPlanStepRow(action)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldContainer,
                    border = BorderStroke(1.dp, EmeraldNeon.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Celebration, contentDescription = null, tint = EmeraldOnContainer, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Expected Result: Health Score: ${actionPlan.expectedCurrentScore} → ${actionPlan.expectedProjectedScore}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = EmeraldOnContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionPlanStepRow(action: com.finverse.domain.ActionStep) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.7f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = ElectricBlue.copy(alpha = 0.15f),
                modifier = Modifier.size(26.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${action.id}",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = ElectricBlue
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(action.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Current: ${action.current} | ${action.target}",
                    fontSize = 11.sp,
                    color = SlateTextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Recommended: ${action.recommendation}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldNeon
                )
            }
        }
    }
}

@Composable
fun SubMetricPill(label: String, score: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = color.copy(alpha = 0.25f)
        ) {
            Text(
                text = "$score%",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = PureWhite.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ColorfulMetricCard(
    title: String,
    amount: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.shadow(4.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.linearGradient(gradient)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = PureWhite, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = amount,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==========================================
// 3. FINANCIAL PROFILE SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: FinverseViewModel) {
    val profile by viewModel.profile.collectAsState()

    var userName by remember(profile) { mutableStateOf(profile.userName) }
    var userEmail by remember(profile) { mutableStateOf(profile.userEmail) }
    var income by remember(profile) { mutableStateOf(if (profile.monthlyIncome > 0) profile.monthlyIncome.toLong().toString() else "") }
    var essential by remember(profile) { mutableStateOf(if (profile.essentialExpenses > 0) profile.essentialExpenses.toLong().toString() else "") }
    var discretionary by remember(profile) { mutableStateOf(if (profile.discretionaryExpenses > 0) profile.discretionaryExpenses.toLong().toString() else "") }
    var savings by remember(profile) { mutableStateOf(if (profile.currentSavings > 0) profile.currentSavings.toLong().toString() else "") }
    var emergency by remember(profile) { mutableStateOf(if (profile.emergencyFund > 0) profile.emergencyFund.toLong().toString() else "") }
    var investments by remember(profile) { mutableStateOf(if (profile.currentInvestments > 0) profile.currentInvestments.toLong().toString() else "") }
    var debt by remember(profile) { mutableStateOf(if (profile.outstandingLoanAmount > 0) profile.outstandingLoanAmount.toLong().toString() else "") }
    var emi by remember(profile) { mutableStateOf(if (profile.emi > 0) profile.emi.toLong().toString() else "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Account & Financial Profile", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("guide") }) {
                        Icon(Icons.Default.MenuBook, contentDescription = "Guide", tint = CyanNeon)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Profile Header Card
            ModernCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                borderBrush = Brush.horizontalGradient(listOf(ElectricBlueLight, CyanNeon))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(ElectricBlue, VioletNeon))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (userName.isNotBlank()) userName.take(1).uppercase() else "V",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = PureWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (userName.isNotBlank()) userName else "User",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Intelligent Financial Twin",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldContainer
                    ) {
                        Text(
                            text = "Active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldOnContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(14.dp))

                ProfileInputField("Account Holder Name", userName, "Your Name") { userName = it }
                
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Dark Mode", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Toggle fully black theme", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    val isDark by com.finverse.ui.theme.ThemeManager.isDarkTheme.collectAsState()
                    Switch(
                        checked = isDark,
                        onCheckedChange = { com.finverse.ui.theme.ThemeManager.isDarkTheme.value = it }
                    )
                }
            }

            // Section 1: Monthly Cash Flow
            ProfileSectionCard(title = "Monthly Cash Flow", icon = Icons.Default.Payments, color = ElectricBlue) {
                ProfileInputField("Monthly Take-Home Income (₹)", income, "e.g. 85000") { income = it }
                ProfileInputField("Essential Expenses / Rent / Bills (₹)", essential, "e.g. 30000") { essential = it }
                ProfileInputField("Discretionary / Lifestyle Spends (₹)", discretionary, "e.g. 15000") { discretionary = it }
            }

            // Section 2: Assets & Wealth
            ProfileSectionCard(title = "Assets & Reserves", icon = Icons.Default.Savings, color = EmeraldNeon) {
                ProfileInputField("Liquid Savings in Bank (₹)", savings, "e.g. 100000") { savings = it }
                ProfileInputField("Emergency Fund (₹)", emergency, "e.g. 50000") { emergency = it }
                ProfileInputField("Investments (Mutual Funds, Stocks, PF) (₹)", investments, "e.g. 250000") { investments = it }
            }

            // Section 3: Liabilities
            ProfileSectionCard(title = "Loans & Liabilities", icon = Icons.Default.CreditCard, color = SunsetPink) {
                ProfileInputField("Total Outstanding Loan Balance (₹)", debt, "e.g. 500000") { debt = it }
                ProfileInputField("Monthly Total EMI (₹)", emi, "e.g. 12000") { emi = it }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val updated = profile.copy(
                        userName = userName.ifBlank { "User" },
                        userEmail = userEmail.ifBlank { "vivekdalvi147@gmail.com" },
                        hasCompletedOnboarding = true,
                        isLoggedIn = true,
                        monthlyIncome = income.toDoubleOrNull() ?: 0.0,
                        essentialExpenses = essential.toDoubleOrNull() ?: 0.0,
                        discretionaryExpenses = discretionary.toDoubleOrNull() ?: 0.0,
                        currentSavings = savings.toDoubleOrNull() ?: 0.0,
                        emergencyFund = emergency.toDoubleOrNull() ?: 0.0,
                        currentInvestments = investments.toDoubleOrNull() ?: 0.0,
                        outstandingLoanAmount = debt.toDoubleOrNull() ?: 0.0,
                        emi = emi.toDoubleOrNull() ?: 0.0,
                        isDemoData = false
                    )
                    viewModel.saveProfile(updated)
                    navController.navigate("dashboard") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp), ambientColor = ElectricBlue, spotColor = CyanNeon),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = PureWhite)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Financial Profile", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PureWhite)
            }

            // Secondary Actions (Switch Account / Log Out / Load Demo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.loadDemoData()
                        navController.navigate("dashboard") { popUpTo("welcome") { inclusive = true } }
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Load Demo", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = {
                        viewModel.logout()
                        navController.navigate("welcome") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SunsetPink)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = SunsetPink, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log Out", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SunsetPink)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ==========================================
// 4. FINANCIAL TWIN SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialTwinScreen(navController: NavController, viewModel: FinverseViewModel) {
    val profile by viewModel.profile.collectAsState()
    val isPlayingAudio by viewModel.isPlayingAudio.collectAsState()
    val currentPlayingId by viewModel.currentPlayingId.collectAsState()
    val scenario by viewModel.twinScenario.collectAsState()
    var selectedHorizon by remember { mutableIntStateOf(20) }

    val projections = remember(profile, selectedHorizon, scenario) {
        viewModel.calculationEngine.calculateProjections(profile, selectedHorizon, scenario)
    }

    val finalYearProj = projections.lastOrNull() ?: Projection(selectedHorizon, 0.0, 0.0, 0.0, 0.0, 0.0)

    val monthlySavings = profile.monthlyIncome - profile.essentialExpenses - profile.discretionaryExpenses - profile.emi
    val monthlyInvestmentContrib = if (monthlySavings > 0) monthlySavings * 0.7 else 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Twin (Simulation)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Horizon Selector Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(5, 10, 15, 20).forEach { horizon ->
                    FilterChip(
                        selected = selectedHorizon == horizon,
                        onClick = { selectedHorizon = horizon },
                        label = { Text("$horizon Years", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = PureWhite
                        )
                    )
                }
            }

            // Scenario Selector
            Column {
                Text(
                    text = "Simulation Scenario",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val scenarios = listOf(
                        Triple(TwinScenario.OPTIMISTIC, "Optimistic", EmeraldNeon),
                        Triple(TwinScenario.EXPECTED, "Expected", AmberGold),
                        Triple(TwinScenario.CONSERVATIVE, "Conservative", SunsetPink)
                    )

                    scenarios.forEach { (sc, label, color) ->
                        val isSelected = scenario == sc
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) color else SlateBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setTwinScenario(sc) }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Projected Final Wealth Hero Card (Nominal + Inflation-Adjusted)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(20.dp), ambientColor = ElectricBlue, spotColor = CyanNeon),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                listOf(ElectricBlueDark, ElectricBlue, Color(0xFF0F52BA))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Projected Wealth in $selectedHorizon Years",
                                    fontSize = 13.sp,
                                    color = PureWhite.copy(alpha = 0.85f),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = viewModel.formatCurrency(finalYearProj.netWorth),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PureWhite
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AudioWaveformVisualizer(
                                    isPlaying = isPlayingAudio && currentPlayingId == "twin_summary_speech",
                                    modifier = Modifier.size(width = 44.dp, height = 20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = {
                                        val speech = "In $selectedHorizon years, under the ${scenario.label} scenario, your projected net worth reaches ${viewModel.formatCurrency(finalYearProj.netWorth)}. Accounting for ${(scenario.inflationRate * 100).toInt()}% annual inflation, your real purchasing power equals ${viewModel.formatCurrency(finalYearProj.inflationAdjustedNetWorth)} in today's money."
                                        viewModel.speakCustomText(speech, id = "twin_summary_speech")
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        if (isPlayingAudio && currentPlayingId == "twin_summary_speech") Icons.Default.PauseCircle else Icons.Default.VolumeUp,
                                        contentDescription = "Listen to Simulation Summary",
                                        tint = CyanNeonLight,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = PureWhite.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Inflation-adjusted highlight
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PureWhite.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Inflation-Adjusted Value", fontSize = 11.sp, color = PureWhite.copy(alpha = 0.8f))
                                    Text(
                                        viewModel.formatCurrency(finalYearProj.inflationAdjustedNetWorth),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = EmeraldNeon
                                    )
                                }
                                Text(
                                    text = "Real Purchasing Power",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PureWhite.copy(alpha = 0.9f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Assets", fontSize = 11.sp, color = PureWhite.copy(alpha = 0.75f))
                                Text(
                                    viewModel.formatCurrency(finalYearProj.savings + finalYearProj.investments),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanNeonLight
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Remaining Liabilities", fontSize = 11.sp, color = PureWhite.copy(alpha = 0.75f))
                                Text(
                                    viewModel.formatCurrency(finalYearProj.debt),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SunsetPink
                                )
                            }
                        }
                    }
                }
            }

            // Assumptions Card
            ModernCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Simulation Assumptions", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ElectricBlue.copy(alpha = 0.15f)
                    ) {
                        Text(
                            scenario.label,
                            color = ElectricBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    AssumptionItem("Expected Return", "${(scenario.expectedReturnRate * 100).toInt()}%/yr", EmeraldNeon)
                    AssumptionItem("Inflation Rate", "${(scenario.inflationRate * 100).toInt()}%/yr", SunsetPink)
                    AssumptionItem("Income Growth", "${(scenario.incomeGrowthRate * 100).toInt()}%/yr", CyanNeon)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    AssumptionItem("Expense Growth", "${(scenario.expenseGrowthRate * 100).toInt()}%/yr", AmberGold)
                    AssumptionItem("Monthly Contribution", viewModel.formatCurrency(monthlyInvestmentContrib), ElectricBlue)
                }
            }

            // Simulation Trajectory Table with Inflation-Adjusted Column
            ModernCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Year-by-Year Compounding Breakdown", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Year", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateTextSecondary, modifier = Modifier.weight(0.9f))
                    Text("Assets", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateTextSecondary, modifier = Modifier.weight(1.3f), textAlign = TextAlign.End)
                    Text("Nominal", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateTextSecondary, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                    Text("Inflation-Adj", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateTextSecondary, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                }

                HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))

                projections.filter { it.year % 2 == 0 || it.year == selectedHorizon || it.year == 1 }.forEach { p ->
                    val totalAssets = p.savings + p.investments
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Yr ${p.year}", fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(0.9f))
                        Text(viewModel.formatCurrency(totalAssets), fontSize = 11.sp, color = CyanNeon, modifier = Modifier.weight(1.3f), textAlign = TextAlign.End)
                        Text(viewModel.formatCurrency(p.netWorth), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElectricBlue, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                        Text(viewModel.formatCurrency(p.inflationAdjustedNetWorth), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldNeon, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                    }
                    HorizontalDivider(color = SlateBorder.copy(alpha = 0.25f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AssumptionItem(label: String, value: String, valueColor: Color) {
    Column {
        Text(label, fontSize = 11.sp, color = SlateTextSecondary)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

// ==========================================
// 5. WHAT-IF SIMULATOR SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatIfScreen(navController: NavController, viewModel: FinverseViewModel) {
    val profile by viewModel.profile.collectAsState()

    var incomeDeltaPct by remember { mutableFloatStateOf(0f) }
    var expenseDeltaPct by remember { mutableFloatStateOf(0f) }
    var extraMonthlyInvestment by remember { mutableFloatStateOf(0f) }
    var newLoanAmount by remember { mutableFloatStateOf(0f) }

    val simIncome = profile.monthlyIncome * (1f + incomeDeltaPct / 100f)
    val simEssential = profile.essentialExpenses * (1f + expenseDeltaPct / 100f)
    val simDiscretionary = profile.discretionaryExpenses * (1f + expenseDeltaPct / 100f)
    val simNewEmi = (newLoanAmount * 0.02)
    val simEmi = profile.emi + simNewEmi
    val simDebt = profile.outstandingLoanAmount + newLoanAmount

    val simProfile = profile.copy(
        monthlyIncome = simIncome,
        essentialExpenses = simEssential,
        discretionaryExpenses = simDiscretionary,
        outstandingLoanAmount = simDebt,
        emi = simEmi,
        currentInvestments = profile.currentInvestments
    )

    val currentHealth = viewModel.calculationEngine.calculateHealthScore(profile)
    val simHealth = viewModel.calculationEngine.calculateHealthScore(simProfile)

    val currentProj = viewModel.calculationEngine.calculateProjections(profile, 10).lastOrNull()?.netWorth ?: 0.0
    val simProj = viewModel.calculationEngine.calculateProjections(simProfile, 10).lastOrNull()?.netWorth ?: 0.0
    val projDiff = simProj - currentProj

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("What-If Simulation Lab", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Simulation Outcome Card
            ModernCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                borderBrush = Brush.horizontalGradient(listOf(VioletNeon, CyanNeon))
            ) {
                Text("Simulation Impact (10-Yr Horizon)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Health Score Delta", fontSize = 12.sp, color = SlateTextSecondary)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${simHealth.overall}/100", fontSize = 20.sp, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            val delta = simHealth.overall - currentHealth.overall
                            Text(
                                text = if (delta >= 0) "(+$delta)" else "($delta)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (delta >= 0) EmeraldNeon else SunsetPink
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("10-Yr Net Worth Delta", fontSize = 12.sp, color = SlateTextSecondary)
                        Text(
                            text = (if (projDiff >= 0) "+" else "") + viewModel.formatCurrency(projDiff),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = if (projDiff >= 0) EmeraldNeon else SunsetPink
                        )
                    }
                }
            }

            // Slider Controls
            ModernCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Adjust Decision Variables", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(14.dp))

                // Income Change
                Text("Income Adjustment: ${incomeDeltaPct.toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Slider(
                    value = incomeDeltaPct,
                    onValueChange = { incomeDeltaPct = it },
                    valueRange = -50f..100f,
                    steps = 29
                )

                // Expense Change
                Text("Living Expense Adjustment: ${expenseDeltaPct.toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Slider(
                    value = expenseDeltaPct,
                    onValueChange = { expenseDeltaPct = it },
                    valueRange = -40f..60f,
                    steps = 19
                )

                // New Loan
                Text("Take New Loan: ${viewModel.formatCurrency(newLoanAmount.toDouble())}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Slider(
                    value = newLoanAmount,
                    onValueChange = { newLoanAmount = it },
                    valueRange = 0f..2000000f,
                    steps = 19
                )

                // Extra Monthly SIP
                Text("Additional Monthly Investment: ${viewModel.formatCurrency(extraMonthlyInvestment.toDouble())}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Slider(
                    value = extraMonthlyInvestment,
                    onValueChange = { extraMonthlyInvestment = it },
                    valueRange = 0f..50000f,
                    steps = 20
                )
            }

            // Reset Button
            OutlinedButton(
                onClick = {
                    incomeDeltaPct = 0f
                    expenseDeltaPct = 0f
                    extraMonthlyInvestment = 0f
                    newLoanAmount = 0f
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset Variables to Baseline")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ==========================================
// 6. FINANCIAL GOALS & LIFE EVENT PLANNER SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(navController: NavController, viewModel: FinverseViewModel) {
    val goals by viewModel.goals.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val isPlayingAudio by viewModel.isPlayingAudio.collectAsState()
    val currentPlayingId by viewModel.currentPlayingId.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    var goalName by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var currentAmount by remember { mutableStateOf("") }
    var monthlyContrib by remember { mutableStateOf("") }
    var targetYear by remember { mutableStateOf("2028") }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Life Event / Financial Goal", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Quick Templates:", fontSize = 12.sp, color = SlateTextSecondary, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Triple("Education", "2500000", "2032"),
                            Triple("Home", "2000000", "2029"),
                            Triple("Marriage", "1500000", "2028"),
                            Triple("Retire", "15000000", "2045")
                        ).forEach { (presetName, presetTarget, presetYr) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ElectricBlue.copy(alpha = 0.1f),
                                border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        goalName = presetName
                                        targetAmount = presetTarget
                                        targetYear = presetYr
                                        if (currentAmount.isBlank()) currentAmount = "50000"
                                        if (monthlyContrib.isBlank()) monthlyContrib = "15000"
                                    }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(presetName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ElectricBlue)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = goalName,
                        onValueChange = { goalName = it },
                        label = { Text("Goal Name (e.g. Higher Education)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = targetAmount,
                        onValueChange = { targetAmount = it },
                        label = { Text("Target Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = currentAmount,
                        onValueChange = { currentAmount = it },
                        label = { Text("Current Saved (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = monthlyContrib,
                        onValueChange = { monthlyContrib = it },
                        label = { Text("Planned Monthly SIP (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = targetYear,
                        onValueChange = { targetYear = it },
                        label = { Text("Target Year (e.g. 2030)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val g = FinancialGoal(
                            name = goalName.ifBlank { "New Goal" },
                            targetAmount = targetAmount.toDoubleOrNull() ?: 500000.0,
                            currentAmount = currentAmount.toDoubleOrNull() ?: 0.0,
                            monthlyContribution = monthlyContrib.toDoubleOrNull() ?: 5000.0,
                            targetYear = targetYear.toIntOrNull() ?: 2028
                        )
                        viewModel.saveGoal(g)
                        showAddDialog = false
                        goalName = ""
                        targetAmount = ""
                        currentAmount = ""
                        monthlyContrib = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Text("Save Goal", color = PureWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Life Event & Goals Planner", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ElectricBlue,
                contentColor = PureWhite
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (goals.isEmpty()) {
                item {
                    ModernCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Flag, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No Goals Configured", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Tap the + button below to track retirement, home purchase, children education, or marriage targets with automated shortfall analysis.",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = SlateTextSecondary
                            )
                        }
                    }
                }
            } else {
                items(goals) { goal ->
                    val progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
                    val lifeEvent = remember(goal, profile) {
                        viewModel.calculationEngine.calculateLifeEvent(
                            profile = profile,
                            title = goal.name,
                            targetYear = goal.targetYear,
                            targetAmount = goal.targetAmount,
                            currentSaved = goal.currentAmount
                        )
                    }

                    val goalAudioId = "goal_speech_${goal.id}"

                    ModernCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(goal.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Target by ${goal.targetYear} (${goal.targetYear - 2026} years left)", fontSize = 12.sp, color = SlateTextSecondary)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AudioWaveformVisualizer(
                                    isPlaying = isPlayingAudio && currentPlayingId == goalAudioId,
                                    modifier = Modifier.size(width = 36.dp, height = 18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = {
                                        val speech = "Goal: ${goal.name}. Target: ${viewModel.formatCurrency(goal.targetAmount)} by ${goal.targetYear}. Saved so far: ${viewModel.formatCurrency(goal.currentAmount)}. ${lifeEvent.aiAdvice}"
                                        viewModel.speakCustomText(speech, id = goalAudioId)
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        if (isPlayingAudio && currentPlayingId == goalAudioId) Icons.Default.PauseCircle else Icons.Default.VolumeUp,
                                        contentDescription = "Listen to Goal Feasibility",
                                        tint = ElectricBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(onClick = { viewModel.deleteGoal(goal.id) }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = SunsetPink)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (lifeEvent.projectedShortfall > 0) AmberGold else EmeraldNeon,
                            trackColor = EmeraldContainer
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Saved: ${viewModel.formatCurrency(goal.currentAmount)} (${(progress * 100).toInt()}%)", fontSize = 12.sp, color = SlateTextSecondary)
                            Text("Target: ${viewModel.formatCurrency(goal.targetAmount)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElectricBlue)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = SlateBorder.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Shortfall Analysis Section
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Required Monthly SIP", fontSize = 11.sp, color = SlateTextSecondary)
                                Text(
                                    "${viewModel.formatCurrency(lifeEvent.monthlyRequiredSaving)}/mo",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricBlue
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (lifeEvent.projectedShortfall > 0) SunsetPink.copy(alpha = 0.15f) else EmeraldContainer
                            ) {
                                Text(
                                    text = if (lifeEvent.projectedShortfall > 0) {
                                        "Shortfall: ${viewModel.formatCurrency(lifeEvent.projectedShortfall)}"
                                    } else {
                                        "On Track"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (lifeEvent.projectedShortfall > 0) SunsetPink else EmeraldOnContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = lifeEvent.aiAdvice,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(60.dp)) }
        }
    }
}

// ==========================================
// 7. PRE-PURCHASE AFFORDABILITY & EMOTIONAL SPENDING CHECK
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeforePurchaseScreen(navController: NavController, viewModel: FinverseViewModel) {
    val profile by viewModel.profile.collectAsState()
    val isPlayingAudio by viewModel.isPlayingAudio.collectAsState()
    val currentPlayingId by viewModel.currentPlayingId.collectAsState()

    var itemName by remember { mutableStateOf("New Smartphone") }
    var itemPrice by remember { mutableStateOf("60000") }
    var emotionalTrigger by remember { mutableStateOf("Sale / Discount Pressure") }
    var isEmi by remember { mutableStateOf(false) }
    var emiTenureMonths by remember { mutableFloatStateOf(6f) }

    val price = itemPrice.toDoubleOrNull() ?: 0.0

    val evaluation = remember(profile, itemName, price, isEmi, emiTenureMonths, emotionalTrigger) {
        viewModel.calculationEngine.calculatePrePurchaseComparison(
            profile = profile,
            itemName = itemName,
            price = price,
            emiMonths = if (isEmi) emiTenureMonths.toInt() else 0,
            reason = emotionalTrigger
        )
    }

    val emiCost = if (isEmi && emiTenureMonths > 0) price / emiTenureMonths else price

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pre-Purchase AI Advisor", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Emotional Spending & Purchase Details
            ModernCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Purchase Details & Mindset", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("What are you planning to buy?") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = itemPrice,
                    onValueChange = { itemPrice = it },
                    label = { Text("Price (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Why are you buying this?", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))

                val reasons = listOf(
                    "Long-term Need",
                    "Reward / Impulse",
                    "Sale / Discount Pressure",
                    "Boredom / Stress"
                )

                reasons.chunked(2).forEach { rowReasons ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowReasons.forEach { r ->
                            val isSelected = emotionalTrigger == r
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) ElectricBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) ElectricBlue else SlateBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { emotionalTrigger = r }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = r,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) ElectricBlue else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Payment via No-Cost / Low-Cost EMI", fontWeight = FontWeight.Medium)
                    Switch(checked = isEmi, onCheckedChange = { isEmi = it })
                }

                if (isEmi) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("EMI Duration: ${emiTenureMonths.toInt()} Months (${viewModel.formatCurrency(emiCost)}/mo)", fontSize = 13.sp)
                    Slider(
                        value = emiTenureMonths,
                        onValueChange = { emiTenureMonths = it },
                        valueRange = 3f..24f,
                        steps = 6
                    )
                }
            }
            
            // AI Verdict Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (evaluation.verdict) {
                        PurchaseVerdict.SAFE -> EmeraldContainer
                        PurchaseVerdict.MODERATE_RISK -> AmberGold.copy(alpha = 0.18f)
                        PurchaseVerdict.HIGH_RISK -> SunsetPink.copy(alpha = 0.18f)
                    }
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                when (evaluation.verdict) {
                                    PurchaseVerdict.SAFE -> Icons.Default.CheckCircle
                                    PurchaseVerdict.MODERATE_RISK -> Icons.Default.Info
                                    PurchaseVerdict.HIGH_RISK -> Icons.Default.Warning
                                },
                                contentDescription = null,
                                tint = when (evaluation.verdict) {
                                    PurchaseVerdict.SAFE -> EmeraldNeon
                                    PurchaseVerdict.MODERATE_RISK -> AmberGold
                                    PurchaseVerdict.HIGH_RISK -> SunsetPink
                                },
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Verdict: ${evaluation.verdictLabel}",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = when (evaluation.verdict) {
                                    PurchaseVerdict.SAFE -> EmeraldOnContainer
                                    PurchaseVerdict.MODERATE_RISK -> AmberGold
                                    PurchaseVerdict.HIGH_RISK -> SunsetPink
                                }
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AudioWaveformVisualizer(
                                isPlaying = isPlayingAudio && currentPlayingId == "pre_purchase_speech",
                                modifier = Modifier.size(width = 44.dp, height = 20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = {
                                    val speech = "Pre-purchase verdict: ${evaluation.verdictLabel}. ${evaluation.explanation} ${if (evaluation.suggestWaitPeriod) "Finverse recommends taking a 48-hour cool-off period before completing this purchase." else ""}"
                                    viewModel.speakCustomText(speech, id = "pre_purchase_speech")
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    if (isPlayingAudio && currentPlayingId == "pre_purchase_speech") Icons.Default.PauseCircle else Icons.Default.VolumeUp,
                                    contentDescription = "Listen to Pre-Purchase Verdict",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = evaluation.explanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )

                    // 48-Hour Wait Rule Banner if suggested
                    if (evaluation.suggestWaitPeriod) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AmberGold.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, AmberGold)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = AmberGold)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("48-Hour Cooling Rule Recommended", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AmberGold)
                                    Text(
                                        "Hold off on buying for 48 hours. If the urge fades, you save ₹${itemPrice}. If it remains, it is a conscious choice.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Buy Now vs Invest Comparison Card
            ModernCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = EmeraldNeon)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buy Now vs Invest That Amount", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "If you invest ${viewModel.formatCurrency(price)} instead at a 10% annual return, here is what it could grow into:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SunsetPink.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, SunsetPink.copy(alpha = 0.3f)),
                        modifier = Modifier.weight(1f).padding(end = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("If Spent Today", fontSize = 11.sp, color = SlateTextSecondary)
                            Text(viewModel.formatCurrency(price), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SunsetPink)
                            Text("Asset Value in 10Y: ₹0", fontSize = 10.sp, color = SlateTextSecondary)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EmeraldNeon.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, EmeraldNeon.copy(alpha = 0.4f)),
                        modifier = Modifier.weight(1.2f).padding(start = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("If Invested in 10Y", fontSize = 11.sp, color = SlateTextSecondary)
                            Text(viewModel.formatCurrency(evaluation.projectedWealth10Y), fontSize = 15.sp, fontWeight = FontWeight.Black, color = EmeraldNeon)
                            Text("+${viewModel.formatCurrency(evaluation.wealthGain)} gain", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldNeon)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ==========================================
// 8. DATA AUDIT & RELIABILITY SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataCheckScreen(navController: NavController, viewModel: FinverseViewModel) {
    val profile by viewModel.profile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Data Audit", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ModernCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    AuditItem(
                        passed = profile.monthlyIncome > (profile.essentialExpenses + profile.discretionaryExpenses),
                        passText = "Income exceeds regular expenditures",
                        failText = "Expenses exceed reported monthly income"
                    )
                    AuditItem(
                        passed = !(profile.outstandingLoanAmount > 0 && profile.emi == 0.0),
                        passText = "Loan balances and EMI schedules match",
                        failText = "Loan balance reported without matching EMI"
                    )
                    AuditItem(
                        passed = profile.emergencyFund >= profile.essentialExpenses * 3,
                        passText = "Emergency reserves cover at least 3 months",
                        failText = "Emergency fund is under 3 months of expenses"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text("Confirm & Return", fontWeight = FontWeight.Bold, color = PureWhite)
            }
        }
    }
}

// ==========================================
// 9. AI ADVISOR SCREEN (BOL AI LIVE + VOICE)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvisorScreen(navController: NavController, viewModel: FinverseViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val aiResponse by viewModel.aiResponse.collectAsState()
    val lastQuestion by viewModel.lastQuestion.collectAsState()
    val aiAudioBase64 by viewModel.aiAudioBase64.collectAsState()
    val isPlayingAudio by viewModel.isPlayingAudio.collectAsState()

    var prompt by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        selectedImageUri = uri
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
                selectedImageBytes = baos.toByteArray()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            selectedImageBytes = null
        }
    }

    val quickQuestions = listOf(
        "How can I improve my health score?",
        "Can I afford a new car purchase?",
        "Emergency fund recommendations",
        "Best investment split for my income"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FinverseLogoBadge(size = 32.dp, iconPadding = 0.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Tech Dev & Finverse AI", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Voice & Financial Intelligence", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.resetAiState() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Slate400)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Main Output Card
            ModernCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                borderBrush = Brush.linearGradient(listOf(ElectricBlueLight, CyanNeon))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (uiState is UIState.Loading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = ElectricBlue)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Tech Dev & Finverse AI is analyzing...", color = SlateTextSecondary, textAlign = TextAlign.Center)
                            }
                        }
                    } else if (aiResponse != null) {
                        // User Chat Bubble
                        if (lastQuestion != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Surface(
                                    color = ElectricBlue.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp),
                                    border = BorderStroke(1.dp, ElectricBlueLight.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = lastQuestion!!,
                                        modifier = Modifier.padding(12.dp),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        // Voice Audio Player Header
                        if (!aiAudioBase64.isNullOrBlank()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = ElectricBlue.copy(alpha = 0.12f),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = Brush.horizontalGradient(listOf(CyanNeon, ElectricBlueLight))
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        AudioWaveformVisualizer(
                                            isPlaying = isPlayingAudio,
                                            modifier = Modifier.size(width = 60.dp, height = 24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = if (isPlayingAudio) "Speaking Voice Output..." else "Voice Response Ready",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyanNeon
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            if (isPlayingAudio) viewModel.stopAiAudio()
                                            else viewModel.playAiAudio()
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            if (isPlayingAudio) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                            contentDescription = "Play/Stop Voice",
                                            tint = ElectricBlue,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                        }

                        MarkdownTextFormatter(text = aiResponse!!)
                    } else {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Ask Tech Dev & Finverse AI",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricBlue
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "Ask any questions about debt payoff, mutual funds, emergency funds, or tax optimization. You can also attach a receipt or document photo.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SlateTextSecondary,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Attachment chip if image selected
            if (selectedImageUri != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = EmeraldContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AttachFile, contentDescription = null, tint = EmeraldOnContainer, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Document/Bill Attached", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldOnContainer)
                        }
                        IconButton(
                            onClick = {
                                selectedImageUri = null
                                selectedImageBytes = null
                            },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = EmeraldOnContainer, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // Suggestion Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickQuestions) { q ->
                    SuggestionChip(
                        onClick = {
                            prompt = q
                            viewModel.askAdvisor(q, selectedImageBytes)
                        },
                        label = { Text(q, fontSize = 12.sp, fontWeight = FontWeight.Medium) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Input Bar with Attachment & Send
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Icon(
                        Icons.Default.AddPhotoAlternate,
                        contentDescription = "Attach Document",
                        tint = if (selectedImageUri != null) EmeraldNeon else ElectricBlue
                    )
                }

                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("Ask Bol AI a financial question...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                FilledIconButton(
                    onClick = {
                        if (prompt.isNotBlank() || selectedImageBytes != null) {
                            viewModel.askAdvisor(prompt.ifBlank { "Analyze this financial document and provide insights" }, selectedImageBytes)
                            prompt = ""
                            selectedImageUri = null
                            selectedImageBytes = null
                        }
                    },
                    modifier = Modifier.size(50.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = ElectricBlue)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = PureWhite)
                }
            }
        }
    }
}

// ==========================================
// 10. SECURITY & PRIVACY SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy & Security", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FinverseHeroLogo(size = 80.dp)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Read-Only Financial Intelligence",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Finverse can understand and simulate your money, but it can never control or access your funds.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            ModernCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    SecurityPoint(Icons.Default.Lock, "Zero Banking Credentials", "We never ask for passwords, bank logins, or UPI PINs.")
                    SecurityPoint(Icons.Default.Block, "No Transaction Authority", "Finverse cannot transfer funds, execute trades, or initiate payments.")
                    SecurityPoint(Icons.Default.Storage, "Local Offline Storage", "Your financial profile data is securely stored locally on your device via Room.")
                    SecurityPoint(Icons.Default.VisibilityOff, "Privacy-First Architecture", "No sensitive personal identification is ever sold or shared.")
                }
            }
        }
    }
}

// Helpers
@Composable
fun ProfileSectionCard(
    title: String,
    icon: ImageVector,
    color: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    ModernCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(14.dp))
        content()
    }
}

@Composable
fun ProfileInputField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = SlateTextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
    }
}

@Composable
fun AuditItem(passed: Boolean, passText: String, failText: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (passed) Icons.Default.CheckCircle else Icons.Default.Error,
            contentDescription = null,
            tint = if (passed) EmeraldNeon else SunsetPink,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = if (passed) passText else failText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (passed) MaterialTheme.colorScheme.onSurface else SunsetPink
        )
    }
}

@Composable
fun SecurityPoint(icon: ImageVector, title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ElectricBlueContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(desc, style = MaterialTheme.typography.bodySmall, color = SlateTextSecondary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Finverse", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF000000),
                                Color(0xFF000000)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Finverse3DHeroBadge(size = 90.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Finverse v1.0.0",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Text(
                        text = "Your Intelligent Financial Twin",
                        fontSize = 14.sp,
                        color = CyanNeonLight,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModernCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        "Our Mission",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ElectricBlue
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Finverse was built to democratize financial intelligence. We believe that everyone deserves a personalized financial advisor that can help simulate long-term wealth, audit daily spending, and provide real-time voice-driven insights.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = SlateTextSecondary
                    )
                }

                ModernCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        "Core Features",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ElectricBlue
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SecurityPoint(Icons.Default.TrendingUp, "20-Year Financial Twin", "Simulates compounding and predicts long-term net worth.")
                    Spacer(modifier = Modifier.height(12.dp))
                    SecurityPoint(Icons.Default.HealthAndSafety, "Health Scoring", "Rates your financial status dynamically out of 100.")
                    Spacer(modifier = Modifier.height(12.dp))
                    SecurityPoint(Icons.AutoMirrored.Filled.Chat, "Finverse AI Voice", "A smart, responsive AI that analyzes your data and responds using text and audio.")
                }

                Text(
                    text = "© 2026 Finverse Intelligence. All rights reserved.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate400,
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
