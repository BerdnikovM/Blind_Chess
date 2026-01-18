package com.Goldy.blindchess.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.Goldy.blindchess.ui.screens.*

@Composable
fun AppNavigation(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                windowSizeClass = windowSizeClass, 
                onInitialize = { navController.navigate("select_protocol") }
            )
        }
        composable("select_protocol") {
            SelectProtocolScreen(
                windowSizeClass = windowSizeClass, 
                onBack = { navController.popBackStack() },
                onProtocolSelected = { route -> navController.navigate(route) }
            )
        }
        composable("speed_colors") {
            SpeedColorsScreen(
                onBack = { navController.popBackStack() },
                onModeSelected = { navController.navigate("speed_colors_zen") } // Simplified for now
            )
        }
        composable("the_walker") {
            TheWalkerScreen(onBack = { navController.popBackStack() })
        }
        composable("knight_vision") {
            KnightVisionScreen(onBack = { navController.popBackStack() })
        }
        composable("personnel_database") {
            PersonnelDatabaseScreen(onBack = { navController.popBackStack() })
        }
        composable("speed_colors_zen") {
            SpeedColorsZenScreen(onBack = { navController.popBackStack() })
        }
    }
}
