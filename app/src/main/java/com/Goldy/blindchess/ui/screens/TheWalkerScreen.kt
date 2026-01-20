package com.Goldy.blindchess.ui.screens

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Goldy.blindchess.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class WalkerState { COUNTDOWN, START_SQUARE, INSTRUCTIONS, GUESSING, GAME_OVER }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheWalkerScreen(difficulty: WalkerDifficulty, onBack: () -> Unit) {
    var gameState by remember { mutableStateOf(WalkerState.COUNTDOWN) }
    var wave by remember { mutableIntStateOf(1) }
    var round by remember { mutableIntStateOf(1) }
    var lives by remember { mutableIntStateOf(3) }
    var countdown by remember { mutableIntStateOf(3) }

    var startSquare by remember { mutableStateOf(getRandomSquare()) }
    var targetSquare by remember { mutableStateOf(startSquare) }
    var instructions by remember { mutableStateOf(listOf<MoveInstruction>()) }
    var currentInstructionIdx by remember { mutableIntStateOf(-1) }
    var showInstruction by remember { mutableStateOf(false) }

    // Состояния для визуального фидбека
    var clickedSquare by remember { mutableStateOf<Square?>(null) }
    var feedbackColor by remember { mutableStateOf(Color.Transparent) }
    var isProcessing by remember { mutableStateOf(false) } // Блокировка кликов во время анимации

    val timePerStep = (2.0 - (wave - 1) * 0.1).coerceAtLeast(0.5)

    // Звуки и корутины
    val coroutineScope = rememberCoroutineScope()
    val toneGenerator = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }
    // Инициализируем ScoreManager
    val context = androidx.compose.ui.platform.LocalContext.current
    val scoreManager = remember { ScoreManager(context) }

    fun prepareRound() {
        startSquare = getRandomSquare()
        var tempSquare = startSquare
        val count = wave + 1
        val newInstructions = mutableListOf<MoveInstruction>()

        repeat(count) {
            val move = getValidMove(tempSquare, difficulty)
            newInstructions.add(move)
            tempSquare = Square((tempSquare.file + move.df), (tempSquare.rank + move.dr))
        }
        instructions = newInstructions
        targetSquare = tempSquare
        currentInstructionIdx = -1
        gameState = WalkerState.COUNTDOWN

        // Сброс фидбека
        clickedSquare = null
        isProcessing = false
    }

    LaunchedEffect(Unit) {
        prepareRound()
    }

    LaunchedEffect(gameState, currentInstructionIdx) {
        when (gameState) {
            WalkerState.COUNTDOWN -> {
                countdown = 3
                while (countdown > 0) {
                    delay(1000)
                    countdown--
                }
                gameState = WalkerState.START_SQUARE
            }
            WalkerState.START_SQUARE -> {
                delay(2000)
                currentInstructionIdx = 0
                gameState = WalkerState.INSTRUCTIONS
            }
            WalkerState.INSTRUCTIONS -> {
                if (instructions.isNotEmpty() && currentInstructionIdx < instructions.size) {
                    showInstruction = true
                    delay((timePerStep * 1000 * 0.9).toLong())
                    showInstruction = false
                    delay((timePerStep * 1000 * 0.1).toLong())
                    currentInstructionIdx++
                } else {
                    gameState = WalkerState.GUESSING
                }
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("The Walker - ${difficulty.name}") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                actions = {
                    Row(Modifier.padding(end = 16.dp)) {
                        repeat(lives) { Icon(Icons.Default.Favorite, null, tint = Color.Red) }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Wave $wave", fontWeight = FontWeight.Bold)
                    Row {
                        val roundsInWave = if (wave == 1) 3 else 5
                        repeat(roundsInWave) { i ->
                            Box(Modifier.size(12.dp).padding(2.dp).background(if (i < round - 1) Color.Green else Color.Gray))
                        }
                    }
                }
                Text("Speed: ${"%.1f".format(timePerStep)}s", style = MaterialTheme.typography.labelSmall)
            }

            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                when (gameState) {
                    WalkerState.COUNTDOWN -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (round == 1 && wave > 1) Text("TIME SPED UP!", color = Color.Red, fontWeight = FontWeight.Bold)
                            Text("$countdown", fontSize = 80.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    WalkerState.START_SQUARE -> {
                        Text("Start: ${startSquare}", fontSize = 60.sp, fontWeight = FontWeight.Bold)
                    }
                    WalkerState.INSTRUCTIONS -> {
                        if (showInstruction && currentInstructionIdx < instructions.size) {
                            val move = instructions[currentInstructionIdx]
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(move.icon, fontSize = 100.sp)
                                Text(move.text, fontSize = 40.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                    WalkerState.GUESSING -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Click the target square", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(16.dp))

                            Box(Modifier.fillMaxWidth(0.95f)) {
                                BaseChessboard(
                                    onDrawSquare = { square ->
                                        if (clickedSquare == square) {
                                            drawCircle(color = feedbackColor, radius = size.minDimension / 2.5f)
                                        }
                                    },
                                    onSquareClick = { clicked ->
                                        if (isProcessing) return@BaseChessboard
                                        isProcessing = true
                                        clickedSquare = clicked

                                        val isCorrect = (clicked == targetSquare)

                                        coroutineScope.launch {
                                            if (isCorrect) {
                                                feedbackColor = Color.Green.copy(alpha = 0.7f)
                                                toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 150)
                                            } else {
                                                feedbackColor = Color.Red.copy(alpha = 0.7f)
                                                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
                                            }

                                            delay(500)

                                            if (isCorrect) {
                                                val roundsInWave = if (wave == 1) 3 else 5
                                                if (round < roundsInWave) {
                                                    round++
                                                    prepareRound()
                                                } else {
                                                    wave++
                                                    round = 1
                                                    prepareRound()
                                                }
                                            } else {
                                                lives--
                                                if (lives <= 0) {
                                                    // 2. СОХРАНЯЕМ РЕКОРД ПРИ ПРОИГРЫШЕ
                                                    // Сохраняем текущую волну (wave)
                                                    scoreManager.saveWalkerHighScore(difficulty, wave)
                                                    gameState = WalkerState.GAME_OVER
                                                } else {
                                                    prepareRound()
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                    WalkerState.GAME_OVER -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("GAME OVER", fontSize = 40.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                            Text("Wave: $wave, Round: $round")
                            Button(onClick = { lives = 3; wave = 1; round = 1; prepareRound() }) { Text("Try Again") }
                        }
                    }
                }
            }
        }
    }
}