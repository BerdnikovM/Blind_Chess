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
import androidx.compose.ui.res.stringResource // <--- ВАЖНО
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Goldy.blindchess.R // <--- ВАЖНО
import com.Goldy.blindchess.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class BlitzState { START, PLAYING, FINISHED }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedColorsBlitzScreen(onBack: () -> Unit) {
    var gameState by remember { mutableStateOf(BlitzState.START) }

    var currentSquare by remember { mutableStateOf<Square>(getRandomSquare()) }

    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(60) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val scoreManager = remember { com.Goldy.blindchess.utils.ScoreManager(context) }

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
            scoreManager.saveBlitzHighScore(score)
        }
    }

    fun processAnswer(answer: SquareColor) {
        val correct = getSquareColor(currentSquare) == answer

        if (correct) {
            score++
            currentSquare = getRandomSquare()
        } else {
            timeLeft = (timeLeft - 5).coerceAtLeast(0)
            currentSquare = getRandomSquare()

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
                title = { Text(stringResource(R.string.sc_blitz)) }, // <-- ЗАМЕНА
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) // <-- ЗАМЕНА
                    }
                },
                actions = {
                    if (gameState == BlitzState.PLAYING) {
                        Text(
                            text = "${stringResource(R.string.sc_time)}: $timeLeft", // <-- ЗАМЕНА
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
                        Text(stringResource(R.string.play), fontSize = 24.sp, fontWeight = FontWeight.Bold) // <-- ЗАМЕНА (START -> PLAY)
                    }
                }
                BlitzState.PLAYING -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${stringResource(R.string.sc_score)}: $score", fontSize = 24.sp, color = MaterialTheme.colorScheme.outline) // <-- ЗАМЕНА
                        Spacer(Modifier.height(40.dp))
                        Text(text = currentSquare.toString(), fontSize = 120.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(80.dp))
                        Row(modifier = Modifier.fillMaxWidth(0.85f), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Button(
                                onClick = { processAnswer(SquareColor.WHITE) },
                                modifier = Modifier.weight(1f).height(80.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                            ) { Text(stringResource(R.string.sc_white), fontWeight = FontWeight.Black, fontSize = 20.sp) } // <-- ЗАМЕНА

                            Button(
                                onClick = { processAnswer(SquareColor.BLACK) },
                                modifier = Modifier.weight(1f).height(80.dp),
                                border = BorderStroke(2.dp, Color.White),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                            ) { Text(stringResource(R.string.sc_black), fontWeight = FontWeight.Black, fontSize = 20.sp) } // <-- ЗАМЕНА
                        }
                    }
                }
                BlitzState.FINISHED -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(R.string.game_over), fontSize = 48.sp, fontWeight = FontWeight.Black, color = Color.Red) // <-- ЗАМЕНА (TIME'S UP -> GAME OVER)
                        Spacer(Modifier.height(16.dp))
                        Text(text = "${stringResource(R.string.sc_score)}: $score", fontSize = 32.sp) // <-- ЗАМЕНА (Final Score -> Score)
                        Spacer(Modifier.height(48.dp))
                        Button(
                            onClick = ::restartGame,
                            modifier = Modifier.fillMaxWidth(0.7f).height(60.dp)
                        ) { Text(stringResource(R.string.try_again), fontSize = 20.sp) } // <-- ЗАМЕНА
                        Spacer(Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth(0.7f).height(60.dp)
                        ) { Text(stringResource(R.string.back).uppercase(), fontSize = 18.sp) } // <-- ЗАМЕНА
                    }
                }
            }
        }
    }
}