package com.Goldy.blindchess.ui.screens

import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.ToneGenerator
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Goldy.blindchess.R
import com.Goldy.blindchess.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Состояния игры Knight Vision
enum class KVState { PREVIEW, COUNTDOWN, START_SQUARE, INSTRUCTIONS, GUESSING, GAME_OVER }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnightVisionGameScreen(onBack: () -> Unit) {
    var gameState by remember { mutableStateOf(KVState.PREVIEW) }
    var wave by remember { mutableIntStateOf(1) }
    var round by remember { mutableIntStateOf(1) }
    var lives by remember { mutableIntStateOf(3) }

    // Таймеры
    var previewTime by remember { mutableIntStateOf(5) }
    var countdown by remember { mutableIntStateOf(3) }

    // Данные игры
    var startSquare by remember { mutableStateOf(getRandomSquare()) }
    var currentSquare by remember { mutableStateOf(startSquare) } // Позиция коня после инструкций
    var obstacles by remember { mutableStateOf<Map<Square, ChessPiece>>(emptyMap()) }
    var instructions by remember { mutableStateOf(listOf<MoveInstruction>()) }
    var validTargets by remember { mutableStateOf(listOf<Square>()) }

    // Используем val и = для списка
    val foundTargets = remember { mutableStateListOf<Square>() }

    // Для показа инструкций
    var currentInstructionIdx by remember { mutableIntStateOf(-1) }
    var showInstruction by remember { mutableStateOf(false) }

    // Визуальный фидбек
    var clickedSquare by remember { mutableStateOf<Square?>(null) }
    var feedbackColor by remember { mutableStateOf(Color.Transparent) }
    var isProcessing by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scoreManager = remember { ScoreManager(context) }
    val coroutineScope = rememberCoroutineScope()
    val toneGenerator = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }

    // Хелпер для загрузки картинок
    fun getBitmap(resId: Int) = BitmapFactory.decodeResource(context.resources, resId).asImageBitmap()

    // Фиксированная скорость показа инструкций (без ускорения)
    val timePerStep = 2.0

    fun prepareRound() {
        // 1. Генерируем препятствия
        val obstacleCount = (3 + (wave / 2)).coerceAtMost(6)

        var tempStart = getRandomSquare()
        var tempObstacles = generateObstacles(obstacleCount, tempStart)

        while (tempObstacles.containsKey(tempStart)) {
            tempStart = getRandomSquare()
            tempObstacles = generateObstacles(obstacleCount, tempStart)
        }

        startSquare = tempStart
        currentSquare = startSquare
        obstacles = tempObstacles

        // 2. Генерируем инструкции
        val moveCount = wave + 1
        val newInstructions = mutableListOf<MoveInstruction>()
        var pathSquare = startSquare

        var attempts = 0
        while (newInstructions.size < moveCount && attempts < 20) {
            val move = getKnightInstruction(pathSquare, obstacles)

            if (move.text == "TRAPPED") {
                newInstructions.clear()
                pathSquare = startSquare
                attempts++
                continue
            }

            newInstructions.add(move)
            pathSquare = Square(pathSquare.file + move.df, pathSquare.rank + move.dr)
        }

        if (newInstructions.isEmpty()) {
            prepareRound()
            return
        }

        instructions = newInstructions
        currentSquare = pathSquare // Финальная позиция

        // 3. Вычисляем правильные ответы
        validTargets = getValidKnightJumps(currentSquare, obstacles)
        foundTargets.clear()

        currentInstructionIdx = -1

        previewTime = 5
        gameState = KVState.PREVIEW

        clickedSquare = null
        isProcessing = false
    }

    LaunchedEffect(Unit) {
        prepareRound()
    }

    // Логика таймеров
    LaunchedEffect(gameState, currentInstructionIdx) {
        when (gameState) {
            KVState.PREVIEW -> {
                while (previewTime > 0) {
                    delay(1000)
                    previewTime--
                }
                gameState = KVState.COUNTDOWN
            }
            KVState.COUNTDOWN -> {
                countdown = 3
                while (countdown > 0) {
                    delay(1000)
                    countdown--
                }
                gameState = KVState.START_SQUARE
            }
            KVState.START_SQUARE -> {
                delay(2000)
                currentInstructionIdx = 0
                gameState = KVState.INSTRUCTIONS
            }
            KVState.INSTRUCTIONS -> {
                if (instructions.isNotEmpty() && currentInstructionIdx < instructions.size) {
                    showInstruction = true
                    delay((timePerStep * 1000 * 0.9).toLong())
                    showInstruction = false
                    delay((timePerStep * 1000 * 0.1).toLong())
                    currentInstructionIdx++
                } else {
                    gameState = KVState.GUESSING
                }
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.protocol_knight_vision).uppercase()) },
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
            // Верхняя панель статистики
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
                if (gameState == KVState.GUESSING) {
                    Text(stringResource(R.string.kv_found, foundTargets.size, validTargets.size), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                } else if (gameState == KVState.PREVIEW) {
                    Text(stringResource(R.string.kv_memorize_timer, previewTime), color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }

            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                when (gameState) {
                    KVState.PREVIEW -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.kv_memorize), style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.height(16.dp))
                            Box(Modifier.fillMaxWidth(0.95f)) {
                                BaseChessboard(
                                    onDrawSquare = { square ->
                                        obstacles[square]?.let { piece ->
                                            try {
                                                drawImage(
                                                    image = getBitmap(piece.resId),
                                                    dstSize = IntSize((size.width * 0.8).toInt(), (size.height * 0.8).toInt()),
                                                    dstOffset = IntOffset((size.width * 0.1).toInt(), (size.height * 0.1).toInt())
                                                )
                                            } catch (e: Exception) {
                                                drawCircle(Color.Gray, radius = size.minDimension / 3)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                    KVState.COUNTDOWN -> {
                        Text("$countdown", fontSize = 80.sp, fontWeight = FontWeight.Black)
                    }
                    KVState.START_SQUARE -> {
                        Text(stringResource(R.string.walker_start, startSquare.toString()), fontSize = 60.sp, fontWeight = FontWeight.Bold)
                    }
                    KVState.INSTRUCTIONS -> {
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
                    KVState.GUESSING -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.kv_click_jumps), style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(16.dp))

                            Box(Modifier.fillMaxWidth(0.95f)) {
                                BaseChessboard(
                                    onDrawSquare = { square ->
                                        // 1. Рисуем уже найденные цели (Зеленые)
                                        if (square in foundTargets) {
                                            drawCircle(Color.Green.copy(alpha = 0.5f), radius = size.minDimension / 3)
                                        }

                                        // 2. Подсветка клика (Зеленый/Красный)
                                        if (clickedSquare == square) {
                                            drawCircle(color = feedbackColor, radius = size.minDimension / 2.5f)
                                        }
                                    },
                                    onSquareClick = { clicked ->
                                        if (isProcessing) return@BaseChessboard
                                        // Игнорируем клик по уже найденным
                                        if (clicked in foundTargets) return@BaseChessboard
                                        // Игнорируем клик по самому коню
                                        if (clicked == currentSquare) return@BaseChessboard

                                        isProcessing = true
                                        clickedSquare = clicked

                                        val isValidMove = validTargets.contains(clicked)

                                        coroutineScope.launch {
                                            if (isValidMove) {
                                                // ПРАВИЛЬНО
                                                feedbackColor = Color.Green.copy(alpha = 0.8f)
                                                toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 100)
                                                foundTargets.add(clicked)

                                                delay(300)
                                                clickedSquare = null
                                                isProcessing = false

                                                // Условие победы в раунде
                                                if (foundTargets.size == validTargets.size) {
                                                    delay(500)
                                                    // Следующий раунд
                                                    val roundsInWave = if (wave == 1) 3 else 5
                                                    if (round < roundsInWave) {
                                                        round++
                                                        prepareRound()
                                                    } else {
                                                        wave++
                                                        round = 1
                                                        prepareRound()
                                                    }
                                                }
                                            } else {
                                                // ОШИБКА
                                                feedbackColor = Color.Red.copy(alpha = 0.8f)
                                                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
                                                lives--

                                                delay(500)
                                                clickedSquare = null

                                                if (lives <= 0) {
                                                    scoreManager.saveKnightVisionHighScore(wave)
                                                    gameState = KVState.GAME_OVER
                                                } else {
                                                    isProcessing = false
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                    KVState.GAME_OVER -> {
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