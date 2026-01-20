package com.Goldy.blindchess.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Goldy.blindchess.utils.Square
import com.Goldy.blindchess.utils.TutorialChessboard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheWalkerTutorialScreen(onFinish: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }

    val tutorialTexts = listOf(
        "Welcome to The Walker! This mode trains your ability to visualize piece movement.",
        "First, you will see a starting square. For example, 'c3'. Remember it!",
        "Then, you will see a series of instructions. For example: '2 Up'.",
        "Your goal is to follow these moves in your mind starting from 'c3'.",
        "If you start at c3 and go 2 Up... you end up at c5.",
        "After instructions finish, an empty board appears. You must click the final square (c5).",
        "You have 3 lives. Each mistake costs a life. The game gets faster every wave!",
        "Ready to walk? Let's go!"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("How to Play") },
                navigationIcon = { IconButton(onClick = onFinish) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                // Прозрачный фон для единого стиля
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        // Используем Column для вертикального разделения пространства
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ВЕРХНЯЯ ЧАСТЬ: Занимает все свободное место (weight 1f) и центрирует контент
            Box(
                modifier = Modifier
                    .weight(1f) // Это ключевой момент адаптивности
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    when (step) {
                        0 -> Text("The Walker", fontSize = 40.sp, fontWeight = FontWeight.Bold)
                        1 -> Text("c3", fontSize = 80.sp, fontWeight = FontWeight.Bold)
                        2 -> {
                            Text("↑", fontSize = 100.sp)
                            Text("2 Up", fontSize = 40.sp)
                        }
                        3, 4, 5 -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(Modifier.fillMaxWidth(0.9f)) {
                                    TutorialChessboard(
                                        startSquare = Square('c', 3),
                                        endSquare = Square('c', 5)
                                    )
                                }
                                Spacer(Modifier.height(16.dp))
                                Text("c3  →  c5", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        6 -> {
                            Row { Text("❤❤❤", fontSize = 40.sp, color = Color.Red) }
                        }
                        else -> Text("The Walker", fontSize = 40.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // НИЖНЯЯ ЧАСТЬ: Диалоговое окно (всегда прижато к низу)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = tutorialTexts.getOrElse(step) { "" },
                        fontSize = 18.sp,
                        minLines = 3,
                        lineHeight = 24.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { if (step < tutorialTexts.size - 1) step++ else onFinish() },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(if (step < tutorialTexts.size - 1) "NEXT" else "PLAY")
                    }
                }
            }
        }
    }
}