package com.finverse.data.repository

import com.finverse.data.local.FinancialDao
import com.finverse.data.model.FinancialGoal
import com.finverse.data.model.FinancialProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FinancialRepository(private val dao: FinancialDao) {

    val profile: Flow<FinancialProfile> = dao.getProfile().map { 
        it ?: FinancialProfile() 
    }

    val goals: Flow<List<FinancialGoal>> = dao.getGoals()

    suspend fun saveProfile(profile: FinancialProfile) {
        dao.saveProfile(profile.copy(hasCompletedOnboarding = true, isLoggedIn = true))
    }

    suspend fun saveGoal(goal: FinancialGoal) {
        dao.saveGoal(goal)
    }

    suspend fun deleteGoal(id: Int) {
        dao.deleteGoal(id)
    }
    
    suspend fun logout() {
        val current = dao.getProfileDirect() ?: FinancialProfile()
        dao.saveProfile(current.copy(isLoggedIn = false, hasCompletedOnboarding = false))
    }
    
    suspend fun loadDemoData() {
        val demoProfile = FinancialProfile(
            id = 1,
            userName = "Vivek Dalvi",
            userEmail = "vivekdalvi147@gmail.com",
            hasCompletedOnboarding = true,
            isLoggedIn = true,
            age = 28,
            monthlyIncome = 85000.0,
            essentialExpenses = 30000.0,
            discretionaryExpenses = 15000.0,
            currentSavings = 120000.0,
            currentInvestments = 250000.0,
            outstandingLoanAmount = 500000.0,
            emi = 12000.0,
            emergencyFund = 50000.0,
            insuranceCoverage = 1000000.0,
            isDemoData = true
        )
        dao.saveProfile(demoProfile)
        
        dao.saveGoal(FinancialGoal(name = "Dream Car", targetAmount = 1500000.0, currentAmount = 200000.0, monthlyContribution = 10000.0, targetYear = 2028))
        dao.saveGoal(FinancialGoal(name = "House Down Payment", targetAmount = 3000000.0, currentAmount = 500000.0, monthlyContribution = 25000.0, targetYear = 2030))
    }
}
