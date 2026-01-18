package com.Goldy.blindchess.ui.screens

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Локальное перечисление только для состояний экрана
private enum class BlitzState { START, PLAYING, FINISHED }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedColorsBlitzScreen(onBack: () -> Unit) {
    var gameState by remember { mutableStateOf(BlitzState.START) }

    // Явно указываем тип <Square>, чтобы избежать ошибки "Cannot infer type"
    var currentSquare by remember { mutableStateOf<Square>(getRandomSquare()) }

    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(60) }

    // Явно указываем типы для Animatable, как мы делали в Zen режиме
    val backgroundColor = remember {
        Animatable<Color, AnimationVector4D>(
            initialValue = Color.Transparent,
            typeConverter = ColorToVector
        )
    }

    val coroutineScope = rememberCoroutineScope()
    val toneGenerator = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }

    // Таймер игры
    LaunchedEffect(gameState) {
        if (gameState == BlitzState.PLAYING) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            gameState = BlitzState.FINISHED
        }
    }

    fun processAnswer(answer: SquareColor) {
        val correct = getSquareColor(currentSquare) == answer

        if (correct) {
            score++
            currentSquare = getRandomSquare()
        } else {
            // Штраф 5 секунд
            timeLeft = (timeLeft - 5).coerceAtLeast(0)
            currentSquare = getRandomSquare()

            // Визуальная и звуковая индикация ТОЛЬКО на ошибку
            coroutineScope.launch {
                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
                backgroundColor.animateTo(Color.Red.copy(alpha = 0.3f), animationSpec = tween(100))
                backgroundColor.animateTo(Color.Transparent, animationSpec = tween(300))
            }
        }
    }

    fun restartGame() {
        score = 0
        timeLeft = 60
        currentSquare = getRandomSquare()
        gameState = BlitzState.PLAYING
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Speed Colors - Blitz") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (gameState == BlitzState.PLAYING) {
                        Text(
                            text = "Time: $timeLeft",
                            modifier = Modifier.padding(end = 16.dp),
                            style = MaterialTheme.typography.titleLarge,
                            color = if (timeLeft <= 10) Color.Red else MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(backgroundColor.value),
            contentAlignment = Alignment.Center
        ) {
            when (gameState) {
                BlitzState.START -> {
                    Button(
                        onClick = { gameState = BlitzState.PLAYING },
                        modifier = Modifier.size(160.dp),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text("START", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
                BlitzState.PLAYING -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Score: $score", fontSize = 24.sp, color = MaterialTheme.colorScheme.outline)
                        Spacer(Modifier.height(40.dp))
                        Text(text = currentSquare.toString(), fontSize = 120.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(80.dp))
                        Row(modifier = Modifier.fillMaxWidth(0.85f), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Button(
                                onClick = { processAnswer(SquareColor.WHITE) },
                                modifier = Modifier.weight(1f).height(80.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                            ) { Text("WHITE", fontWeight = FontWeight.Black, fontSize = 20.sp) }

                            Button(
                                onClick = { processAnswer(SquareColor.BLACK) },
                                modifier = Modifier.weight(1f).height(80.dp),
                                border = BorderStroke(2.dp, Color.White),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                            ) { Text("BLACK", fontWeight = FontWeight.Black, fontSize = 20.sp) }
                        }
                    }
                }
                BlitzState.FINISHED -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "TIME'S UP!", fontSize = 48.sp, fontWeight = FontWeight.Black, color = Color.Red)
                        Spacer(Modifier.height(16.dp))
                        Text(text = "Final Score: $score", fontSize = 32.sp)
                        Spacer(Modifier.height(48.dp))
                        Button(
                            onClick = ::restartGame,
                            modifier = Modifier.fillMaxWidth(0.7f).height(60.dp)
                        ) { Text("TRY AGAIN", fontSize = 20.sp) }
                        Spacer(Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth(0.7f).height(60.dp)
                        ) { Text("FINISH", fontSize = 18.sp) }
                    }
                }
            }
        }
    }
}