package com.Goldy.blindchess.ui.screens

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// --- Вспомогательные данные ---
enum class SquareColor { WHITE, BLACK }

private data class Square(val file: Char, val rank: Int) {
    override fun toString(): String = "$file$rank"
}

private val ColorToVector = TwoWayConverter<Color, AnimationVector4D>(
    convertToVector = { color ->
        AnimationVector4D(color.red, color.green, color.blue, color.alpha)
    },
    convertFromVector = { vector ->
        Color(red = vector.v1, green = vector.v2, blue = vector.v3, alpha = vector.v4)
    }
)

private fun getRandomSquare(): Square {
    val file = ('a'..'h').random()
    val rank = (1..8).random()
    return Square(file, rank)
}

private fun getSquareColor(square: Square): SquareColor {
    val fileIndex = square.file - 'a'
    val rankIndex = square.rank - 1
    return if ((fileIndex + rankIndex) % 2 == 0) SquareColor.BLACK else SquareColor.WHITE
}

private fun getDiagonals(square: Square): Pair<String, String> {
    val fIdx = square.file - 'a'
    val rIdx = square.rank - 1
    fun findEnd(df: Int, dr: Int): String {
        var f = fIdx; var r = rIdx
        while (f + df in 0..7 && r + dr in 0..7) { f += df; r += dr }
        return "${'a' + f}${r + 1}"
    }
    return "${findEnd(-1, -1)}-${findEnd(1, 1)}" to "${findEnd(-1, 1)}-${findEnd(1, -1)}"
}

private enum class GameState { GUESSING, RESULT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedColorsZenScreen(onBack: () -> Unit) {
    var gameState by remember { mutableStateOf(GameState.GUESSING) }
    var currentSquare by remember { mutableStateOf(getRandomSquare()) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }

    // Счетчик серии побед
    var streak by remember { mutableIntStateOf(0) }

    val backgroundColor = remember {
        Animatable(initialValue = Color.Transparent, typeConverter = ColorToVector)
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
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    // Отображение Streak в правом углу
                    Text(
                        text = "Streak: $streak",
                        modifier = Modifier.padding(end = 16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (streak > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(backgroundColor.value)
        ) {
            if (gameState == GameState.GUESSING) {
                GuessingContent(square = currentSquare, onAnswer = ::processAnswer)
            } else {
                isCorrect?.let {
                    ResultContent(
                        square = currentSquare,
                        isCorrect = it,
                        onContinue = ::nextRound,
                        onFinish = onBack
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
                // ИСПРАВЛЕНИЕ: Добавлена белая обводка для кнопки BLACK
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

        // Шахматная доска с нотацией
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

        // ИСПРАВЛЕНИЕ: Кнопка Finish теперь крупнее и заметнее
        OutlinedButton(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth(0.9f).height(56.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Text("FINISH SESSION", fontSize = 16.sp, color = MaterialTheme.colorScheme.outline)
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ChessboardWithNotation(squareToHighlight: Square, diagonals: Pair<String, String>) {
    val files = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
    val highlightColor = Color.Red.copy(alpha = 0.8f)
    val diagonalColor = Color.Blue.copy(alpha = 0.25f)

    fun getDiagonalList(range: String): List<String> {
        val parts = range.split("-"); if (parts.size != 2) return emptyList()
        val res = mutableListOf<String>()
        var f = parts[0][0]; var r = parts[0][1].toString().toInt()
        val ef = parts[1][0]; val er = parts[1][1].toString().toInt()
        val df = if (ef > f) 1 else if (ef < f) -1 else 0
        val dr = if (er > r) 1 else if (er < r) -1 else 0
        while (true) {
            res.add("$f$r")
            if (f == ef && r == er) break
            f = (f.code + df).toChar(); r += dr
        }
        return res
    }

    val allDiagSquares = getDiagonalList(diagonals.first) + getDiagonalList(diagonals.second)

    Row(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
        // Вертикальная нотация (Цифры 8-1)
        Column(modifier = Modifier.fillMaxHeight().width(24.dp)) {
            for (rank in 8 downTo 1) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = rank.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp)) // Уголок под буквами
        }

        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            // Сама доска
            Column(modifier = Modifier.weight(1f).fillMaxWidth().background(Color.Black).padding(1.dp)) {
                for (rank in 8 downTo 1) {
                    Row(modifier = Modifier.weight(1f)) {
                        for (file in files) {
                            val current = Square(file, rank)
                            val isWhite = ((file - 'a') + (rank - 1)) % 2 != 0
                            Box(
                                modifier = Modifier
                                    .weight(1f).fillMaxHeight()
                                    .background(if (isWhite) Color(0xFFEEEEEE) else Color(0xFF444444))
                                    .drawBehind {
                                        if (current.toString() in allDiagSquares) drawRect(diagonalColor)
                                        if (current == squareToHighlight) drawCircle(color = highlightColor, radius = size.minDimension / 3)
                                    }
                            )
                        }
                    }
                }
            }
            // Горизонтальная нотация (Буквы a-h)
            Row(modifier = Modifier.height(24.dp).fillMaxWidth()) {
                for (file in files) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                        Text(text = file.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}