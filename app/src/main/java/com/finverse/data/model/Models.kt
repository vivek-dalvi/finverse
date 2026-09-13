package com.finverse.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "financial_profile")
data class FinancialProfile(
    @PrimaryKey val id: Int = 1,
    val userName: String = "Vivek Dalvi",
    val userEmail: String = "vivekdalvi147@gmail.com",
    val hasCompletedOnboarding: Boolean = false,
    val isLoggedIn: Boolean = false,
    val age: Int = 28,
    val monthlyIncome: Double = 0.0,
    val essentialExpenses: Double = 0.0,
    val discretionaryExpenses: Double = 0.0,
    val currentSavings: Double = 0.0,
    val currentInvestments: Double = 0.0,
    val outstandingLoanAmount: Double = 0.0,
    val emi: Double = 0.0,
    val emergencyFund: Double = 0.0,
    val insuranceCoverage: Double = 0.0,
    val isDemoData: Boolean = false
)

@Entity(tableName = "financial_goals")
data class FinancialGoal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val monthlyContribution: Double = 0.0,
    val targetYear: Int
)

@JsonClass(generateAdapter = true)
data class AIFileAttachment(
    val mimeType: String,
    val data: String // Base64 encoded string
)

@JsonClass(generateAdapter = true)
data class AIRequest(
    val prompt: String,
    val files: List<AIFileAttachment>? = null
)

@JsonClass(generateAdapter = true)
data class AIResponse(
    val success: Boolean,
    val text: String?,
    val audioBase64: String? = null,
    val error: String? = null
)
