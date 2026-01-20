package com.Goldy.blindchess.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheWalkerMenuScreen(
    onTutorialClick: () -> Unit,
    onDifficultySelect: (String) -> Unit, // Передаем строку: "EASY", "MEDIUM", "HARD"
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("The Walker") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Choose Difficulty",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(48.dp))

            // Кнопки Сложности
            MenuButton(
                title = "Easy",
                subtitle = "Rook moves only (↕ ↔)",
                onClick = { onDifficultySelect("EASY") },
                containerColor = Color(0xFF4CAF50), // Зеленый
                textColor = Color.White
            )

            Spacer(Modifier.height(24.dp))

            MenuButton(
                title = "Medium",
                subtitle = "Rook + Bishop moves (↗ ↘)",
                onClick = { onDifficultySelect("MEDIUM") },
                containerColor = Color(0xFFFF9800), // Оранжевый
                textColor = Color.White
            )

            Spacer(Modifier.height(16.dp))

            MenuButton(
                title = "Hard",
                subtitle = "All moves + Knight (♞)",
                onClick = { onDifficultySelect("HARD") },
                containerColor = Color(0xFFF44336), // Красный
                textColor = Color.White
            )

            Spacer(Modifier.height(16.dp))

            // Кнопка Обучения
            MenuButton(
                title = "Tutorial",
                subtitle = "Learn the rules",
                onClick = onTutorialClick,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                textColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun MenuButton(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    containerColor: Color,
    textColor: Color
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = textColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}