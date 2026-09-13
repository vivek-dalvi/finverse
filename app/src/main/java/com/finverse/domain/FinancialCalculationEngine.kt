package com.finverse.domain

import com.finverse.data.model.FinancialProfile
import kotlin.math.pow
import kotlin.math.roundToInt

data class HealthScoreItem(
    val category: String,
    val score: Int,
    val maxScore: Int,
    val status: String,
    val isGood: Boolean,
    val isWarning: Boolean,
    val isCritical: Boolean,
    val advice: String
)

data class HealthScore(
    val overall: Int,
    val saving: Int,
    val emergencyFund: Int,
    val debt: Int,
    val investments: Int,
    val emergencyFundItem: HealthScoreItem = HealthScoreItem("Emergency Fund", 12, 25, "Below recommended safety level", false, true, false, "Build at least 6 months of essential expenses."),
    val debtManagementItem: HealthScoreItem = HealthScoreItem("Debt Management", 16, 20, "Moderate loan burden", true, false, false, "Keep debt payments under 30% of income."),
    val savingsItem: HealthScoreItem = HealthScoreItem("Savings", 20, 20, "Strong monthly surplus", true, false, false, "Great surplus rate over 20%."),
    val investmentsItem: HealthScoreItem = HealthScoreItem("Investments", 15, 20, "Good investment base", true, false, false, "Solid compounding base, keep SIP steady."),
    val insuranceItem: HealthScoreItem = HealthScoreItem("Insurance", 7, 15, "Review coverage", false, true, false, "Aim for term cover of at least 10x annual expenses.")
) {
    val items: List<HealthScoreItem>
        get() = listOf(emergencyFundItem, debtManagementItem, savingsItem, investmentsItem, insuranceItem)
    
    val emergencyItem: HealthScoreItem get() = emergencyFundItem
    val debtItem: HealthScoreItem get() = debtManagementItem
    val savingItem: HealthScoreItem get() = savingsItem
    val investmentItem: HealthScoreItem get() = investmentsItem
}

data class Projection(
    val year: Int,
    val savings: Double,
    val investments: Double,
    val debt: Double,
    val netWorth: Double,
    val nominalWealth: Double = netWorth,
    val realWealth: Double = netWorth
) {
    val inflationAdjustedNetWorth: Double get() = realWealth
}

enum class TwinScenario(val displayName: String, val returnRate: Double, val inflationRate: Double, val incomeGrowth: Double, val expenseGrowth: Double) {
    OPTIMISTIC("Optimistic", 0.12, 0.05, 0.10, 0.05),
    EXPECTED("Expected", 0.10, 0.06, 0.08, 0.06),
    CONSERVATIVE("Conservative", 0.07, 0.07, 0.05, 0.06);

    val label: String get() = displayName
    val expectedReturnRate: Double get() = returnRate
    val incomeGrowthRate: Double get() = incomeGrowth
    val expenseGrowthRate: Double get() = expenseGrowth
}

enum class PurchaseVerdict {
    SAFE, MODERATE_RISK, HIGH_RISK
}

data class PrePurchaseComparison(
    val currentSurplus: Double,
    val afterSurplus: Double,
    val currentHealthScore: Int,
    val afterHealthScore: Int,
    val currentEmergencyMonths: Double,
    val afterEmergencyMonths: Double,
    val current10YrWealth: Double,
    val after10YrWealth: Double,
    val verdict: PurchaseVerdict,
    val emotionalRiskTag: String,
    val lifestyleSpentThisMonth: Double,
    val budgetPercentUsed: Int,
    val aiRecommendation: String,
    val projectedWealth10Y: Double = 0.0,
    val wealthGain: Double = 0.0
) {
    val verdictLabel: String
        get() = when (verdict) {
            PurchaseVerdict.SAFE -> "Safe to buy"
            PurchaseVerdict.MODERATE_RISK -> "Moderate risk"
            PurchaseVerdict.HIGH_RISK -> "High impulse risk"
        }
    val explanation: String get() = aiRecommendation
    val suggestWaitPeriod: Boolean
        get() = verdict != PurchaseVerdict.SAFE || emotionalRiskTag.contains("Impulse", ignoreCase = true) || emotionalRiskTag.contains("Emotional", ignoreCase = true)
}

data class LifeEventCalculation(
    val title: String,
    val targetYear: Int,
    val targetAmount: Double,
    val currentSavings: Double,
    val monthlyRequiredSaving: Double,
    val projectedShortfall: Double,
    val aiAdvice: String
)

data class ActionStep(
    val id: Int,
    val title: String,
    val current: String,
    val target: String,
    val recommendation: String
)

data class AiFinancialActionPlan(
    val emergencyTarget: Double,
    val emergencyCurrent: Double,
    val emergencyMonthlyAdd: Double,
    val lifestyleCurrent: Double,
    val lifestyleSuggestedLimit: Double,
    val investmentCurrentMonthly: Double,
    val investmentRecommendedMonthly: Double,
    val currentScore: Int,
    val projectedScore: Int
) {
    val actions: List<ActionStep>
        get() = listOf(
            ActionStep(
                id = 1,
                title = "Emergency Fund",
                current = "₹${(emergencyCurrent / 1000).toInt()}k",
                target = "Goal: ₹${(emergencyTarget / 100000).toInt()}L (6 months)",
                recommendation = "+₹${emergencyMonthlyAdd.toInt()}/month for 24 months"
            ),
            ActionStep(
                id = 2,
                title = "Lifestyle Spending",
                current = "₹${(lifestyleCurrent / 1000).toInt()}k/month",
                target = "Safe Limit: ₹${(lifestyleSuggestedLimit / 1000).toInt()}k/month",
                recommendation = "Cap discretionary expenses to save ₹${(lifestyleCurrent - lifestyleSuggestedLimit).coerceAtLeast(0.0).toInt()}/month"
            ),
            ActionStep(
                id = 3,
                title = "Investment Growth",
                current = "₹${(investmentCurrentMonthly / 1000).toInt()}k/month",
                target = "Optimal: ₹${(investmentRecommendedMonthly / 1000).toInt()}k/month",
                recommendation = "Direct saved funds to SIP"
            )
        )

    val expectedCurrentScore: Int get() = currentScore
    val expectedProjectedScore: Int get() = projectedScore
}

class FinancialCalculationEngine {

    fun calculateHealthScore(profile: FinancialProfile): HealthScore {
        if (profile.monthlyIncome <= 0) {
            val emptyItem = HealthScoreItem("N/A", 0, 20, "No data", false, false, false, "Enter your income to evaluate.")
            return HealthScore(
                overall = 0, saving = 0, emergencyFund = 0, debt = 0, investments = 0,
                emergencyFundItem = emptyItem.copy(category = "Emergency Fund", maxScore = 25),
                debtManagementItem = emptyItem.copy(category = "Debt Management", maxScore = 20),
                savingsItem = emptyItem.copy(category = "Savings", maxScore = 20),
                investmentsItem = emptyItem.copy(category = "Investments", maxScore = 20),
                insuranceItem = emptyItem.copy(category = "Insurance", maxScore = 15)
            )
        }

        val monthlySavings = (profile.monthlyIncome - profile.essentialExpenses - profile.discretionaryExpenses - profile.emi).coerceAtLeast(0.0)
        val savingsRate = monthlySavings / profile.monthlyIncome
        
        // 1. Savings (Max 20)
        val savingScore = ((savingsRate / 0.25) * 20).roundToInt().coerceIn(0, 20)
        val savingStatus = when {
            savingScore >= 18 -> "Strong monthly surplus"
            savingScore >= 12 -> "Moderate monthly surplus"
            else -> "Tight monthly cashflow"
        }
        val savingItem = HealthScoreItem(
            category = "Savings",
            score = savingScore,
            maxScore = 20,
            status = savingStatus,
            isGood = savingScore >= 16,
            isWarning = savingScore in 10..15,
            isCritical = savingScore < 10,
            advice = if (savingScore >= 16) "Excellent surplus rate over 20% of income." else "Aim to save at least 20% of net monthly income."
        )

        // 2. Emergency Fund (Max 25)
        val targetEmergency = (profile.essentialExpenses * 6).coerceAtLeast(1.0)
        val emergencyRatio = (profile.emergencyFund / targetEmergency).coerceIn(0.0, 1.0)
        val emergencyScore = (emergencyRatio * 25).roundToInt()
        val emergencyMonths = if (profile.essentialExpenses > 0) profile.emergencyFund / profile.essentialExpenses else 0.0
        val emergencyStatus = when {
            emergencyMonths >= 5.5 -> "Safe 6-month safety buffer"
            emergencyMonths >= 3.0 -> "Moderate emergency reserve"
            else -> "Below recommended safety level"
        }
        val emergencyItem = HealthScoreItem(
            category = "Emergency Fund",
            score = emergencyScore,
            maxScore = 25,
            status = emergencyStatus,
            isGood = emergencyScore >= 20,
            isWarning = emergencyScore in 10..19,
            isCritical = emergencyScore < 10,
            advice = "Covers ${String.format("%.1f", emergencyMonths)} months. Target is 6 months (₹${(targetEmergency).toInt()})."
        )

        // 3. Debt Management (Max 20)
        val emiRatio = profile.emi / profile.monthlyIncome
        val debtScore = when {
            emiRatio <= 0.20 -> 20
            emiRatio <= 0.30 -> 18
            emiRatio <= 0.40 -> 14
            emiRatio <= 0.50 -> 8
            else -> 4
        }
        val debtStatus = when {
            debtScore >= 18 -> "Low debt burden"
            debtScore >= 14 -> "Moderate loan burden"
            else -> "High debt stress"
        }
        val debtItem = HealthScoreItem(
            category = "Debt Management",
            score = debtScore,
            maxScore = 20,
            status = debtStatus,
            isGood = debtScore >= 17,
            isWarning = debtScore in 12..16,
            isCritical = debtScore < 12,
            advice = "EMI is ${(emiRatio * 100).toInt()}% of income. Safe benchmark is under 30%."
        )

        // 4. Investments (Max 20)
        val annualIncome = profile.monthlyIncome * 12
        val investmentRatio = if (annualIncome > 0) profile.currentInvestments / (annualIncome * 0.5) else 0.0
        val investmentScore = (investmentRatio * 20).roundToInt().coerceIn(5, 20)
        val investmentStatus = when {
            investmentScore >= 17 -> "Strong wealth foundation"
            investmentScore >= 13 -> "Good investment base"
            else -> "Early investment stage"
        }
        val investmentItem = HealthScoreItem(
            category = "Investments",
            score = investmentScore,
            maxScore = 20,
            status = investmentStatus,
            isGood = investmentScore >= 16,
            isWarning = investmentScore in 11..15,
            isCritical = investmentScore < 11,
            advice = "Investments total ₹${profile.currentInvestments.toInt()}. Consistent compounding builds long-term wealth."
        )

        // 5. Insurance (Max 15)
        val idealCover = annualIncome * 10
        val insuranceRatio = if (idealCover > 0) profile.insuranceCoverage / idealCover else 0.0
        val insuranceScore = (insuranceRatio * 15).roundToInt().coerceIn(3, 15)
        val insuranceStatus = when {
            insuranceScore >= 12 -> "Well protected"
            insuranceScore >= 7 -> "Review coverage"
            else -> "Severely underinsured"
        }
        val insuranceItem = HealthScoreItem(
            category = "Insurance",
            score = insuranceScore,
            maxScore = 15,
            status = insuranceStatus,
            isGood = insuranceScore >= 12,
            isWarning = insuranceScore in 6..11,
            isCritical = insuranceScore < 6,
            advice = "Current cover: ₹${(profile.insuranceCoverage / 100000).toInt()}L. Ideal 10x income: ₹${(idealCover / 100000).toInt()}L."
        )

        val overall = (savingScore + emergencyScore + debtScore + investmentScore + insuranceScore).coerceIn(0, 100)

        return HealthScore(
            overall = overall,
            saving = savingScore,
            emergencyFund = emergencyScore,
            debt = debtScore,
            investments = investmentScore,
            emergencyFundItem = emergencyItem,
            debtManagementItem = debtItem,
            savingsItem = savingItem,
            investmentsItem = investmentItem,
            insuranceItem = insuranceItem
        )
    }

    fun calculateProjections(
        profile: FinancialProfile,
        years: Int = 20,
        scenario: TwinScenario = TwinScenario.EXPECTED
    ): List<Projection> {
        val projections = mutableListOf<Projection>()
        var currentSavings = profile.currentSavings + profile.emergencyFund
        var currentInvestments = profile.currentInvestments
        var currentDebt = profile.outstandingLoanAmount
        var currentIncome = profile.monthlyIncome
        var currentExpenses = profile.essentialExpenses + profile.discretionaryExpenses
        var currentEmi = profile.emi

        val initialNetWorth = currentSavings + currentInvestments - currentDebt
        projections.add(
            Projection(
                year = 0,
                savings = currentSavings,
                investments = currentInvestments,
                debt = currentDebt,
                netWorth = initialNetWorth,
                nominalWealth = initialNetWorth,
                realWealth = initialNetWorth
            )
        )

        for (year in 1..years) {
            currentIncome *= (1 + scenario.incomeGrowth)
            currentExpenses *= (1 + scenario.expenseGrowth)
            val annualSavingsSurplus = (currentIncome - currentExpenses - currentEmi).coerceAtLeast(0.0) * 12

            // Allocation: 30% to high-yield liquid/cash savings, 70% to equity/mutual investments
            currentSavings += (annualSavingsSurplus * 0.3)
            currentSavings *= (1 + 0.04) // standard liquid interest

            currentInvestments += (annualSavingsSurplus * 0.7)
            currentInvestments *= (1 + scenario.returnRate)

            if (currentDebt > 0) {
                currentDebt -= (currentEmi * 12)
                if (currentDebt < 0) currentDebt = 0.0
                else currentDebt *= (1 + 0.08)
            }

            val nominalNetWorth = currentSavings + currentInvestments - currentDebt
            val discountFactor = (1 + scenario.inflationRate).pow(year)
            val realNetWorth = nominalNetWorth / discountFactor

            projections.add(
                Projection(
                    year = year,
                    savings = currentSavings,
                    investments = currentInvestments,
                    debt = currentDebt,
                    netWorth = nominalNetWorth,
                    nominalWealth = nominalNetWorth,
                    realWealth = realNetWorth
                )
            )
        }

        return projections
    }

    fun calculatePrePurchaseComparison(
        profile: FinancialProfile,
        itemName: String,
        price: Double,
        emiMonths: Int = 0,
        reason: String = "Want/reward"
    ): PrePurchaseComparison {
        val currentSurplus = (profile.monthlyIncome - profile.essentialExpenses - profile.discretionaryExpenses - profile.emi).coerceAtLeast(0.0)
        val currentHealth = calculateHealthScore(profile)
        val currentEmergencyMonths = if (profile.essentialExpenses > 0) profile.emergencyFund / profile.essentialExpenses else 0.0
        val current10Yr = calculateProjections(profile, 10, TwinScenario.EXPECTED).last().nominalWealth

        val monthlyDeduction = if (emiMonths > 0) price / emiMonths else price
        val simulatedEmi = if (emiMonths > 0) profile.emi + monthlyDeduction else profile.emi
        val simulatedDiscretionary = if (emiMonths == 0) profile.discretionaryExpenses + price else profile.discretionaryExpenses
        val simulatedSavings = if (emiMonths == 0) (profile.currentSavings - price).coerceAtLeast(0.0) else profile.currentSavings

        val simulatedProfile = profile.copy(
            emi = simulatedEmi,
            discretionaryExpenses = simulatedDiscretionary,
            currentSavings = simulatedSavings
        )
        val afterSurplus = (simulatedProfile.monthlyIncome - simulatedProfile.essentialExpenses - simulatedProfile.discretionaryExpenses - simulatedProfile.emi).coerceAtLeast(0.0)
        val afterHealth = calculateHealthScore(simulatedProfile)
        val afterEmergencyMonths = if (simulatedProfile.essentialExpenses > 0) simulatedProfile.emergencyFund / simulatedProfile.essentialExpenses else 0.0
        val after10Yr = calculateProjections(simulatedProfile, 10, TwinScenario.EXPECTED).last().nominalWealth

        val lifestyleSpentThisMonth = profile.discretionaryExpenses
        val budgetPct = if (profile.discretionaryExpenses > 0) ((price / profile.discretionaryExpenses) * 100).toInt().coerceIn(1, 100) else 25

        val isEmotional = reason.contains("Stress", ignoreCase = true) ||
                reason.contains("Boredom", ignoreCase = true) ||
                reason.contains("Sale", ignoreCase = true) ||
                reason.contains("reward", ignoreCase = true)

        val emotionalRiskTag = when {
            reason.contains("Stress", ignoreCase = true) || reason.contains("Boredom", ignoreCase = true) -> "Possible Emotional Purchase"
            reason.contains("Sale", ignoreCase = true) -> "Impulse Discount Risk"
            reason.contains("Need", ignoreCase = true) || reason.contains("Planned", ignoreCase = true) -> "Planned & Safe Acquisition"
            else -> "Discretionary Lifestyle Spend"
        }

        val verdict = when {
            afterSurplus < 0 || afterHealth.overall < 45 || (price > profile.currentSavings && emiMonths == 0) -> PurchaseVerdict.HIGH_RISK
            afterHealth.overall < currentHealth.overall - 4 || budgetPct > 35 -> PurchaseVerdict.MODERATE_RISK
            else -> PurchaseVerdict.SAFE
        }

        val aiRecommendation = when (verdict) {
            PurchaseVerdict.SAFE -> "Affordable and financially safe. Fits cleanly within your monthly discretionary budget."
            PurchaseVerdict.MODERATE_RISK -> {
                if (isEmotional) "You already spent ₹${lifestyleSpentThisMonth.toInt()} on lifestyle this month. This purchase takes $budgetPct% of discretionary budget. AI Recommendation: Wait 48 hours and reconsider."
                else "Affordable, but waiting 2 months would provide a safer cash buffer."
            }
            PurchaseVerdict.HIGH_RISK -> "Significant financial strain. This purchase would compromise your monthly surplus and weaken your emergency cushion."
        }

        val projected10Y = price * (1.0 + 0.10).pow(10)
        val wealthGain = (projected10Y - price).coerceAtLeast(0.0)

        return PrePurchaseComparison(
            currentSurplus = currentSurplus,
            afterSurplus = afterSurplus,
            currentHealthScore = currentHealth.overall,
            afterHealthScore = afterHealth.overall,
            currentEmergencyMonths = currentEmergencyMonths,
            afterEmergencyMonths = afterEmergencyMonths,
            current10YrWealth = current10Yr,
            after10YrWealth = after10Yr,
            verdict = verdict,
            emotionalRiskTag = emotionalRiskTag,
            lifestyleSpentThisMonth = lifestyleSpentThisMonth,
            budgetPercentUsed = budgetPct,
            aiRecommendation = aiRecommendation,
            projectedWealth10Y = projected10Y,
            wealthGain = wealthGain
        )
    }

    fun calculateLifeEvent(
        profile: FinancialProfile,
        title: String,
        targetYear: Int,
        targetAmount: Double,
        currentSaved: Double
    ): LifeEventCalculation {
        val currentYear = 2026
        val yearsRemaining = (targetYear - currentYear).coerceAtLeast(1)
        val monthsRemaining = yearsRemaining * 12
        val remainingAmount = (targetAmount - currentSaved).coerceAtLeast(0.0)
        
        // Assuming modest 8% returns for compounding SIP towards goal
        val monthlyInterest = 0.08 / 12
        val requiredMonthlySaving = if (monthsRemaining > 0) {
            val factor = ((1 + monthlyInterest).pow(monthsRemaining) - 1) / monthlyInterest
            (remainingAmount / factor).coerceAtLeast(100.0)
        } else remainingAmount

        val monthlySurplus = (profile.monthlyIncome - profile.essentialExpenses - profile.discretionaryExpenses - profile.emi).coerceAtLeast(0.0)
        val gap = (requiredMonthlySaving - (monthlySurplus * 0.5)) // assume half surplus can go to this goal
        val projectedShortfall = (gap * monthsRemaining).coerceAtLeast(0.0)

        val aiAdvice = if (projectedShortfall > 0) {
            "At your current saving rate, you may fall short by approximately ₹${String.format("%.1f", projectedShortfall / 100000)} lakh. Increasing monthly savings by ₹${gap.toInt()} could close the gap."
        } else {
            "On track! Allocating ₹${requiredMonthlySaving.toInt()}/month guarantees reaching ₹${(targetAmount / 100000).toInt()}L by $targetYear."
        }

        return LifeEventCalculation(
            title = title,
            targetYear = targetYear,
            targetAmount = targetAmount,
            currentSavings = currentSaved,
            monthlyRequiredSaving = requiredMonthlySaving,
            projectedShortfall = projectedShortfall,
            aiAdvice = aiAdvice
        )
    }

    fun generateActionPlan(profile: FinancialProfile): AiFinancialActionPlan {
        val targetEmergency = (profile.essentialExpenses * 6).coerceAtLeast(180000.0)
        val emergencyCurrent = profile.emergencyFund
        val emergencyAdd = ((targetEmergency - emergencyCurrent) / 24).coerceIn(2000.0, 10000.0)
        
        val lifestyleCurrent = profile.discretionaryExpenses
        val lifestyleLimit = (lifestyleCurrent * 0.8).coerceAtLeast(5000.0)

        val currentMonthlySavings = (profile.monthlyIncome - profile.essentialExpenses - profile.discretionaryExpenses - profile.emi).coerceAtLeast(0.0)
        val investmentCurrent = currentMonthlySavings * 0.6
        val investmentRecommended = (currentMonthlySavings * 0.75).coerceAtLeast(investmentCurrent + 3000.0)

        val currentScore = calculateHealthScore(profile).overall
        val projectedScore = (currentScore + 12).coerceAtMost(94)

        return AiFinancialActionPlan(
            emergencyTarget = targetEmergency,
            emergencyCurrent = emergencyCurrent,
            emergencyMonthlyAdd = emergencyAdd,
            lifestyleCurrent = lifestyleCurrent,
            lifestyleSuggestedLimit = lifestyleLimit,
            investmentCurrentMonthly = investmentCurrent,
            investmentRecommendedMonthly = investmentRecommended,
            currentScore = currentScore,
            projectedScore = projectedScore
        )
    }
}

