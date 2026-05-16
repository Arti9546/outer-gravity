package com.hastashilpa.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hastashilpa.ui.auth.LoginScreen
import com.hastashilpa.ui.home.HomeScreen
import com.hastashilpa.ui.blueprint.BlueprintScreen
import com.hastashilpa.ui.estimator.EstimatorScreen
import com.hastashilpa.ui.marketplace.MarketplaceUploadScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { 
                    navController.navigate("home") { 
                        popUpTo("login") { inclusive = true } 
                    } 
                }
            )
        }
        composable("home") {
            HomeScreen(
                onNavigateToBlueprint = { id -> 
                    if (id != null) navController.navigate("blueprint/$id") 
                    else navController.navigate("blueprint")
                },
                onNavigateToEstimator = { navController.navigate("estimator") },
                onNavigateToMarketplace = { navController.navigate("marketplace") }
            )
        }
        composable("blueprint/{blueprintId}") { backStackEntry ->
            val blueprintId = backStackEntry.arguments?.getString("blueprintId")
            BlueprintScreen(
                blueprintId = blueprintId,
                onBackClick = { navController.navigateUp() }
            )
        }
        composable("blueprint") {
            BlueprintScreen(
                blueprintId = null,
                onBackClick = { navController.navigateUp() }
            )
        }
        composable("estimator") {
            EstimatorScreen(onBackClick = { navController.navigateUp() })
        }
        composable("marketplace") {
            MarketplaceUploadScreen(onBackClick = { navController.navigateUp() })
        }
    }
}
