package com.finverse.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.finverse.ui.FinverseViewModel
import com.finverse.ui.screens.*

@Composable
fun FinverseNavGraph() {
    val navController = rememberNavController()
    val viewModel: FinverseViewModel = viewModel(factory = FinverseViewModel.Factory)
    val profile by viewModel.profile.collectAsState()
    
    var hasNavigatedFromWelcome by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }

    // Smart Auto-Navigation: If user already completed onboarding or has financial data, jump straight to dashboard
    LaunchedEffect(profile.hasCompletedOnboarding, profile.monthlyIncome) {
        if (!hasNavigatedFromWelcome && (profile.hasCompletedOnboarding || profile.monthlyIncome > 0)) {
            hasNavigatedFromWelcome = true
            navController.navigate("dashboard") {
                popUpTo("welcome") { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeScreen(navController, viewModel)
        }
        composable("dashboard") {
            DashboardScreen(navController, viewModel)
        }
        composable("profile") {
            ProfileScreen(navController, viewModel)
        }
        composable("twin") {
            FinancialTwinScreen(navController, viewModel)
        }
        composable("whatif") {
            WhatIfScreen(navController, viewModel)
        }
        composable("goals") {
            GoalsScreen(navController, viewModel)
        }
        composable("datacheck") {
            DataCheckScreen(navController, viewModel)
        }
        composable("purchase") {
            BeforePurchaseScreen(navController, viewModel)
        }
        composable("advisor") {
            AdvisorScreen(navController, viewModel)
        }
        composable("security") {
            SecurityScreen(navController)
        }
        composable("guide") {
            GuideScreen(navController)
        }
        composable("about") {
            AboutUsScreen(navController)
        }
    }
}
