package com.Goldy.blindchess.ui.screens

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.Goldy.blindchess.utils.*
import kotlinx.coroutines.launch

private enum class GameState { GUESSING, RESULT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedColorsZenScreen(onBack: () -> Unit) {
    var gameState by remember { mutableStateOf(GameState.GUESSING) }
    var currentSquare by remember { mutableStateOf<Square>(getRandomSquare()) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var streak by remember { mutableIntStateOf(0) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val scoreManager = remember { ScoreManager(context) }

    val backgroundColor = remember {
        Animatable<Color, AnimationVector4D>(initialValue = Color.Transparent, typeConverter = ColorToVector)
    }

    val coroutineScope = rememberCoroutineScope()
    val toneGenerator = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }

    fun processAnswer(answer: SquareColor) {
        val correct = getSquareColor(currentSquare) == answer
        isCorrect = correct
        gameState = GameState.RESULT
        if (correct) {
            streak++
        } else {
            scoreManager.saveZenHighScore(streak)
            streak = 0
        }

        coroutineScope.launch {
            val target = if (correct) {
                toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 150)
                Color.Green.copy(alpha = 0.2f)
            } else {
                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
                Color.Red.copy(alpha = 0.2f)
            }
            backgroundColor.animateTo(target, animationSpec = tween(150))
            backgroundColor.animateTo(Color.Transparent, animationSpec = tween(400))
        }
    }

    fun nextRound() {
        currentSquare = getRandomSquare()
        isCorrect = null
        gameState = GameState.GUESSING
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Speed Colors - Zen") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                actions = {
                    Text(
                        text = "Streak: $streak",
                        modifier = Modifier.padding(end = 16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (streak > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(backgroundColor.value)) {
            if (gameState == GameState.GUESSING) {
                GuessingContent(square = currentSquare, onAnswer = ::processAnswer)
            } else {
                isCorrect?.let {
                    ResultContent(
                        square = currentSquare,
                        isCorrect = it,
                        onContinue = ::nextRound,
                        onFinish = {
                            scoreManager.saveZenHighScore(streak)
                            onBack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun GuessingContent(square: Square, onAnswer: (SquareColor) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = square.toString(), fontSize = 100.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(80.dp))
        Row(modifier = Modifier.fillMaxWidth(0.85f), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { onAnswer(SquareColor.WHITE) },
                modifier = Modifier.weight(1f).height(70.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) { Text("WHITE", fontWeight = FontWeight.Black, fontSize = 18.sp) }

            Button(
                onClick = { onAnswer(SquareColor.BLACK) },
                modifier = Modifier.weight(1f).height(70.dp),
                border = BorderStroke(2.dp, Color.White),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) { Text("BLACK", fontWeight = FontWeight.Black, fontSize = 18.sp) }
        }
    }
}

@Composable
private fun ResultContent(
    square: Square,
    isCorrect: Boolean,
    onContinue: () -> Unit,
    onFinish: () -> Unit
) {
    val diagonals = getDiagonals(square)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isCorrect) "SUCCESS" else "FAILURE",
            color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
            fontSize = 40.sp, fontWeight = FontWeight.Black
        )
        Spacer(Modifier.height(24.dp))

        ChessboardWithNotation(squareToHighlight = square, diagonals = diagonals)

        Spacer(Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("DIAGONALS", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                Text(diagonals.first, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(diagonals.second, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(32.dp))
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth(0.9f).height(60.dp)) {
            Text("CONTINUE", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onFinish, // Теперь просто вызываем переданный колбэк
            modifier = Modifier.fillMaxWidth(0.9f).height(56.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Text("FINISH SESSION", fontSize = 16.sp, color = MaterialTheme.colorScheme.outline)
        }
        Spacer(Modifier.height(24.dp))
    }
}