package com.Goldy.blindchess.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedColorsTutorialScreen(onBack: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(animation = tween(800), repeatMode = RepeatMode.Reverse),
        label = "scale"
    )

    val tutorialTexts = listOf(
        "Welcome to Speed Colors! This is a great trainer to develop your chess vision.",
        "In the center of the screen, you will see coordinates of a random square, for example, c3.",
        "Your task is to determine as quickly as possible the color of this square: white or black. Also try to guess which diagonals pass through this square.",
        "Use these buttons to answer. Try pressing them now!",
        "In Zen mode, you can train without rushing, and in Blitz mode, you can compete for a time record.",
        "Ready to start training? Good luck!"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tutorial") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        // ИСПРАВЛЕНИЕ: Используем Column вместо Box для вертикального разделения
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ВЕРХНЯЯ ЧАСТЬ: Игровая симуляция. Занимает все доступное место (weight 1f)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "c3",
                        fontSize = 100.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.scale(if (step == 1) pulseScale else 1f),
                        color = if (step == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(Modifier.height(80.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { if (step == 3) step++ },
                            modifier = Modifier
                                .weight(1f)
                                .height(70.dp)
                                .scale(if (step == 3) pulseScale else 1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            border = BorderStroke(1.dp, Color.Gray)
                        ) { Text("WHITE", fontWeight = FontWeight.Bold) }

                        Button(
                            onClick = { if (step == 3) step++ },
                            modifier = Modifier
                                .weight(1f)
                                .height(70.dp)
                                .scale(if (step == 3) pulseScale else 1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            ),
                            border = BorderStroke(2.dp, Color.White)
                        ) { Text("BLACK", fontWeight = FontWeight.Bold) }
                    }
                }
            }

            // НИЖНЯЯ ЧАСТЬ: Диалоговое окно
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f),
                tonalElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = tutorialTexts[step],
                        fontSize = 17.sp,
                        lineHeight = 22.sp,
                        minLines = 3
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { if (step < tutorialTexts.size - 1) step++ else onBack() },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(if (step < tutorialTexts.size - 1) "NEXT" else "GOT IT")
                    }
                }
            }
        }
    }
}