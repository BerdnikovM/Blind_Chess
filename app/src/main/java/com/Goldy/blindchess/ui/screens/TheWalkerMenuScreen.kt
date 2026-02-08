package com.Goldy.blindchess.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource // <--- ВАЖНО
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Goldy.blindchess.R // <--- ВАЖНО

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheWalkerMenuScreen(
    onTutorialClick: () -> Unit,
    onDifficultySelect: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.protocol_the_walker).uppercase()) }, // <-- ЗАМЕНА
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) // <-- ЗАМЕНА
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
                text = stringResource(R.string.walker_choose_difficulty), // <-- ЗАМЕНА
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(48.dp))

            // Кнопки Сложности
            MenuButton(
                title = stringResource(R.string.walker_easy), // <-- ЗАМЕНА
                subtitle = stringResource(R.string.walker_desc_easy), // <-- ЗАМЕНА
                onClick = { onDifficultySelect("EASY") },
                containerColor = Color(0xFF4CAF50),
                textColor = Color.White
            )

            Spacer(Modifier.height(24.dp))

            MenuButton(
                title = stringResource(R.string.walker_medium), // <-- ЗАМЕНА
                subtitle = stringResource(R.string.walker_desc_medium), // <-- ЗАМЕНА
                onClick = { onDifficultySelect("MEDIUM") },
                containerColor = Color(0xFFFF9800),
                textColor = Color.White
            )

            Spacer(Modifier.height(16.dp))

            MenuButton(
                title = stringResource(R.string.walker_hard), // <-- ЗАМЕНА
                subtitle = stringResource(R.string.walker_desc_hard), // <-- ЗАМЕНА
                onClick = { onDifficultySelect("HARD") },
                containerColor = Color(0xFFF44336),
                textColor = Color.White
            )

            Spacer(Modifier.height(16.dp))

            // Кнопка Обучения
            MenuButton(
                title = stringResource(R.string.tutorial), // <-- ЗАМЕНА
                subtitle = "",
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