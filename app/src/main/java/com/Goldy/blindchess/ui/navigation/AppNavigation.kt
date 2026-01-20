package com.Goldy.blindchess.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.Goldy.blindchess.ui.screens.*
// Импортируем enum сложности из нашего пакета utils
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
                // Обратите внимание: "the_walker" теперь ведет в меню этого режима
                onProtocolSelected = { route -> navController.navigate(route) }
            )
        }

        // --- SPEED COLORS (Меню и режимы) ---
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

        // --- THE WALKER (Новая навигация) ---

        // 1. Меню режима The Walker
        composable("the_walker_menu") {
            TheWalkerMenuScreen(
                onTutorialClick = { navController.navigate("the_walker_tutorial") },
                onDifficultySelect = { difficulty ->
                    // Передаем выбранную сложность (EASY, MEDIUM, HARD) в маршрут
                    navController.navigate("the_walker_game/$difficulty")
                },
                onBack = { navController.popBackStack() }
            )
        }

        // 2. Экран самой игры с параметром сложности
        composable(
            route = "the_walker_game/{difficulty}",
            arguments = listOf(navArgument("difficulty") { type = NavType.StringType })
        ) { backStackEntry ->
            // Получаем строку из маршрута и превращаем в Enum
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

        // 3. Туториал для Walker
        composable("the_walker_tutorial") {
            TheWalkerTutorialScreen(onFinish = { navController.popBackStack() })
        }

        // --- ДРУГИЕ РЕЖИМЫ ---
        composable("knight_vision") {
            KnightVisionScreen(onBack = { navController.popBackStack() })
        }

        composable("personnel_database") {
            PersonnelDatabaseScreen(onBack = { navController.popBackStack() })
        }
    }
}