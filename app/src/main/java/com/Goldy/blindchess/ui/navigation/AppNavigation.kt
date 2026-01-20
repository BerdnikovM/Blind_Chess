package com.Goldy.blindchess.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.Goldy.blindchess.ui.screens.*
import com.Goldy.blindchess.utils.WalkerDifficulty

@Composable
fun AppNavigation(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {

        // --- ГЛАВНЫЕ ЭКРАНЫ ---
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

        // --- SPEED COLORS ---
        composable("speed_colors") {
            SpeedColorsScreen(
                onBack = { navController.popBackStack() },
                onModeSelected = { mode ->
                    when (mode) {
                        "zen" -> navController.navigate("speed_colors_zen")
                        "blitz" -> navController.navigate("speed_colors_blitz")
                        "tutorial" -> navController.navigate("speed_colors_tutorial")
                    }
                }
            )
        }

        composable("speed_colors_zen") {
            SpeedColorsZenScreen(onBack = { navController.popBackStack() })
        }

        composable("speed_colors_blitz") {
            SpeedColorsBlitzScreen(onBack = { navController.popBackStack() })
        }

        composable("speed_colors_tutorial") {
            SpeedColorsTutorialScreen(onBack = { navController.popBackStack() })
        }

        // --- THE WALKER ---

        // 1. Меню
        composable("the_walker_menu") {
            TheWalkerMenuScreen(
                onTutorialClick = { navController.navigate("the_walker_tutorial") },
                onDifficultySelect = { difficulty ->
                    navController.navigate("the_walker_game/$difficulty")
                },
                onBack = { navController.popBackStack() }
            )
        }

        // 2. Игра
        composable(
            route = "the_walker_game/{difficulty}",
            arguments = listOf(navArgument("difficulty") { type = NavType.StringType })
        ) { backStackEntry ->
            val diffStr = backStackEntry.arguments?.getString("difficulty") ?: "EASY"
            val difficulty = try {
                WalkerDifficulty.valueOf(diffStr)
            } catch (e: Exception) {
                WalkerDifficulty.EASY
            }

            TheWalkerScreen(
                difficulty = difficulty,
                onBack = { navController.popBackStack() }
            )
        }

        // 3. Туториал
        composable("the_walker_tutorial") {
            TheWalkerTutorialScreen(onFinish = { navController.popBackStack() })
        }

        // --- KNIGHT VISION ---

        // 1. Меню
        composable("knight_vision") {
            KnightVisionScreen(
                onBack = { navController.popBackStack() },
                onPlayClick = { navController.navigate("knight_vision_game") },
                onTutorialClick = { navController.navigate("knight_vision_tutorial") } // Теперь ведет куда надо
            )
        }

        // 2. Игра
        composable("knight_vision_game") {
            KnightVisionGameScreen(onBack = { navController.popBackStack() })
        }

        // 3. Туториал (ДОБАВЛЕНО)
        composable("knight_vision_tutorial") {
            KnightVisionTutorialScreen(onFinish = { navController.popBackStack() })
        }

        // --- БАЗА ДАННЫХ ---
        composable("personnel_database") {
            PersonnelDatabaseScreen(onBack = { navController.popBackStack() })
        }
    }
}