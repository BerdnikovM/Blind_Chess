package com.Goldy.blindchess.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnightVisionScreen(
    onBack: () -> Unit,
    onPlayClick: () -> Unit,     // Колбэк для игры
    onTutorialClick: () -> Unit  // Колбэк для туториала (пока заглушка, но подготовим)
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KNIGHT VISION") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                // Прозрачный фон
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Кнопка Игры
            Button(
                onClick = onPlayClick, // Вызываем колбэк
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(text = "Static interference", modifier = Modifier.padding(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка Туториала
            OutlinedButton(
                onClick = onTutorialClick, // <-- Должно быть так
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(text = "Tutorial", modifier = Modifier.padding(8.dp))
            }
        }
    }
}