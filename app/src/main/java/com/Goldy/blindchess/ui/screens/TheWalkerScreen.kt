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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Goldy.blindchess.R
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

    var clickedSquare by remember { mutableStateOf<Square?>(null) }
    var feedbackColor by remember { mutableStateOf(Color.Transparent) }
    var isProcessing by remember { mutableStateOf(false) }

    val timePerStep = (2.0 - (wave - 1) * 0.1).coerceAtLeast(0.5)

    val coroutineScope = rememberCoroutineScope()
    val toneGenerator = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }
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
                // Для простоты покажем просто "The Walker", т.к. difficulty.name не переведен
                title = { Text(stringResource(R.string.protocol_the_walker)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } },
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
                    Text("${stringResource(R.string.wave)} $wave", fontWeight = FontWeight.Bold)
                    Row {
                        val roundsInWave = if (wave == 1) 3 else 5
                        repeat(roundsInWave) { i ->
                            Box(Modifier.size(12.dp).padding(2.dp).background(if (i < round - 1) Color.Green else Color.Gray))
                        }
                    }
                }
                Text("${stringResource(R.string.speed)}: ${"%.1f".format(timePerStep)}s", style = MaterialTheme.typography.labelSmall)
            }

            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                when (gameState) {
                    WalkerState.COUNTDOWN -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (round == 1 && wave > 1) Text(stringResource(R.string.walker_time_sped_up), color = Color.Red, fontWeight = FontWeight.Bold)
                            Text("$countdown", fontSize = 80.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    WalkerState.START_SQUARE -> {
                        Text(stringResource(R.string.walker_start, startSquare.toString()), fontSize = 60.sp, fontWeight = FontWeight.Bold)
                    }
                    WalkerState.INSTRUCTIONS -> {
                        if (showInstruction && currentInstructionIdx < instructions.size) {
                            val move = instructions[currentInstructionIdx]
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(move.icon, fontSize = 100.sp)
                                // ИСПРАВЛЕНИЕ: Используем функцию resolveInstructionText для перевода
                                Text(
                                    text = resolveInstructionText(move),
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    WalkerState.GUESSING -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.walker_click_target), style = MaterialTheme.typography.titleMedium)
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
                            Text(stringResource(R.string.game_over), fontSize = 40.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                            Text("${stringResource(R.string.wave)}: $wave, ${stringResource(R.string.round)}: $round")
                            Button(onClick = { lives = 3; wave = 1; round = 1; prepareRound() }) { Text(stringResource(R.string.try_again)) }
                        }
                    }
                }
            }
        }
    }
}