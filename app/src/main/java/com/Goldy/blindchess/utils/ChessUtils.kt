package com.Goldy.blindchess.utils

import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Goldy.blindchess.R
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

// --- Общие данные ---
enum class SquareColor { WHITE, BLACK }

data class Square(val file: Char, val rank: Int) {
    override fun toString(): String = "$file$rank"
}

val ColorToVector = TwoWayConverter<Color, AnimationVector4D>(
    convertToVector = { color -> AnimationVector4D(color.red, color.green, color.blue, color.alpha) },
    convertFromVector = { vector -> Color(red = vector.v1, green = vector.v2, blue = vector.v3, alpha = vector.v4) }
)

fun getRandomSquare(): Square = Square(('a'..'h').random(), (1..8).random())

fun getSquareColor(square: Square): SquareColor {
    val fileIndex = square.file - 'a'
    val rankIndex = square.rank - 1
    return if ((fileIndex + rankIndex) % 2 == 0) SquareColor.BLACK else SquareColor.WHITE
}

fun getDiagonals(square: Square): Pair<String, String> {
    val fIdx = square.file - 'a'
    val rIdx = square.rank - 1
    fun findEnd(df: Int, dr: Int): String {
        var f = fIdx; var r = rIdx
        while (f + df in 0..7 && r + dr in 0..7) { f += df; r += dr }
        return "${'a' + f}${r + 1}"
    }
    return "${findEnd(-1, -1)}-${findEnd(1, 1)}" to "${findEnd(-1, 1)}-${findEnd(1, -1)}"
}

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

// --- УНИВЕРСАЛЬНАЯ БАЗОВАЯ ДОСКА ---
@Composable
fun BaseChessboard(
    modifier: Modifier = Modifier,
    onDrawSquare: (DrawScope.(Square) -> Unit)? = null,
    onSquareClick: ((Square) -> Unit)? = null,
    overlayContent: (@Composable BoxScope.() -> Unit)? = null
) {
    val files = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')

    Row(modifier = modifier.fillMaxWidth().aspectRatio(1f)) {
        Column(modifier = Modifier.fillMaxHeight().width(24.dp)) {
            for (rank in 8 downTo 1) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = rank.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxSize().background(Color.Black).padding(1.dp)) {
                    for (rank in 8 downTo 1) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (file in files) {
                                val current = Square(file, rank)
                                val isWhite = ((file - 'a') + (rank - 1)) % 2 != 0
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(if (isWhite) Color(0xFFEEEEEE) else Color(0xFF444444))
                                        .then(
                                            if (onSquareClick != null) Modifier.clickable { onSquareClick(current) }
                                            else Modifier
                                        )
                                        .drawBehind {
                                            onDrawSquare?.invoke(this, current)
                                        }
                                )
                            }
                        }
                    }
                }
                overlayContent?.invoke(this)
            }

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

@Composable
fun ChessboardWithNotation(squareToHighlight: Square, diagonals: Pair<String, String>) {
    val highlightColor = Color.Red.copy(alpha = 0.8f)
    val diagonalColor = Color.Blue.copy(alpha = 0.25f)
    val allDiagSquares = getDiagonalList(diagonals.first) + getDiagonalList(diagonals.second)

    BaseChessboard(
        onDrawSquare = { square ->
            if (square.toString() in allDiagSquares) drawRect(diagonalColor)
            if (square == squareToHighlight) drawCircle(color = highlightColor, radius = size.minDimension / 3)
        }
    )
}

@Composable
fun TutorialChessboard(startSquare: Square, endSquare: Square) {
    val startColor = Color.Green.copy(alpha = 0.5f)
    val endColor = Color.Red.copy(alpha = 0.5f)
    val arrowColor = Color.Yellow

    BaseChessboard(
        onDrawSquare = { square ->
            if (square == startSquare) drawCircle(color = startColor, radius = size.minDimension / 2.5f)
            if (square == endSquare) drawCircle(color = endColor, radius = size.minDimension / 2.5f)
        },
        overlayContent = {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val boardWidth = size.width; val boardHeight = size.height
                val cellW = boardWidth / 8; val cellH = boardHeight / 8

                val startX = (startSquare.file - 'a') * cellW + cellW / 2
                val startY = (8 - startSquare.rank) * cellH + cellH / 2
                val endX = (endSquare.file - 'a') * cellW + cellW / 2
                val endY = (8 - endSquare.rank) * cellH + cellH / 2

                drawLine(color = arrowColor, start = Offset(startX, startY), end = Offset(endX, endY), strokeWidth = 8.dp.toPx(), cap = StrokeCap.Round)

                val angle = atan2(endY - startY, endX - startX)
                val arrowHeadSize = 20.dp.toPx()
                val tipX = endX; val tipY = endY
                val leftX = endX - arrowHeadSize * cos(angle - Math.PI / 6)
                val leftY = endY - arrowHeadSize * sin(angle - Math.PI / 6)
                val rightX = endX - arrowHeadSize * cos(angle + Math.PI / 6)
                val rightY = endY - arrowHeadSize * sin(angle + Math.PI / 6)

                val path = Path().apply { moveTo(tipX, tipY); lineTo(leftX.toFloat(), leftY.toFloat()); lineTo(rightX.toFloat(), rightY.toFloat()); close() }
                drawPath(path, arrowColor)
            }
        }
    )
}

// --- Логика генерации ходов ---

enum class WalkerDifficulty { EASY, MEDIUM, HARD }

// ОБНОВЛЕННЫЙ КЛАСС:
// - templateRes и args: для перевода в UI
// - text: сохраняем для совместимости (проверка "TRAPPED")
data class MoveInstruction(
    val templateRes: Int,
    val args: List<Any>, // Числа или ID ресурсов (Int)
    val icon: String,
    val df: Int,
    val dr: Int,
    val text: String = "" // Для технических целей (например "TRAPPED")
)

// Хелпер: выбор ID ресурса направления
private fun getDirectionRes(step: Int, isVertical: Boolean): Int {
    return if (isVertical) {
        if (step > 0) R.string.dir_up else R.string.dir_down
    } else {
        if (step > 0) R.string.dir_right else R.string.dir_left
    }
}

// Хелпер: иконка стрелки
private fun getArrowIcon(fStep: Int, rStep: Int): String {
    return when {
        fStep == 0 && rStep > 0 -> "↑"
        fStep == 0 && rStep < 0 -> "↓"
        fStep > 0 && rStep == 0 -> "→"
        fStep < 0 && rStep == 0 -> "←"
        fStep > 0 && rStep > 0 -> "↗"
        fStep > 0 && rStep < 0 -> "↘"
        fStep < 0 && rStep > 0 -> "↖"
        else -> "↙"
    }
}

fun getValidMove(current: Square, difficulty: WalkerDifficulty): MoveInstruction {
    val possibleMoves = mutableListOf<MoveInstruction>()
    val files = 'a'..'h'; val ranks = 1..8
    val steps = listOf(-5, -4, -3, -2, -1, 1, 2, 3, 4, 5)

    // EASY (Прямые)
    steps.forEach { fStep ->
        if ((current.file + fStep) in files) {
            possibleMoves.add(
                MoveInstruction(
                    templateRes = R.string.move_template_simple,
                    args = listOf(Math.abs(fStep), getDirectionRes(fStep, false)),
                    icon = getArrowIcon(fStep, 0),
                    df = fStep, dr = 0,
                    text = "${Math.abs(fStep)} ${if (fStep > 0) "Right" else "Left"}" // Legacy text
                )
            )
        }
    }
    steps.forEach { rStep ->
        if ((current.rank + rStep) in ranks) {
            possibleMoves.add(
                MoveInstruction(
                    templateRes = R.string.move_template_simple,
                    args = listOf(Math.abs(rStep), getDirectionRes(rStep, true)),
                    icon = getArrowIcon(0, rStep),
                    df = 0, dr = rStep,
                    text = "${Math.abs(rStep)} ${if (rStep > 0) "Up" else "Down"}" // Legacy text
                )
            )
        }
    }

    // MEDIUM & HARD (Диагонали)
    if (difficulty == WalkerDifficulty.MEDIUM || difficulty == WalkerDifficulty.HARD) {
        steps.forEach { fStep ->
            steps.forEach { rStep ->
                if (Math.abs(fStep) == Math.abs(rStep)) {
                    if ((current.file + fStep) in files && (current.rank + rStep) in ranks) {
                        possibleMoves.add(
                            MoveInstruction(
                                templateRes = R.string.move_template_diagonal,
                                args = listOf(
                                    Math.abs(rStep),
                                    getDirectionRes(rStep, true), // Вертикаль
                                    getDirectionRes(fStep, false) // Горизонталь
                                ),
                                icon = getArrowIcon(fStep, rStep),
                                df = fStep, dr = rStep,
                                text = "Diagonal Move" // Legacy placeholder
                            )
                        )
                    }
                }
            }
        }
    }

    // HARD (Конь)
    if (difficulty == WalkerDifficulty.HARD) {
        val knightOffsets = listOf(Pair(1, 2), Pair(2, 1), Pair(2, -1), Pair(1, -2), Pair(-1, -2), Pair(-2, -1), Pair(-2, 1), Pair(-1, 2))
        knightOffsets.forEach { (fStep, rStep) ->
            if ((current.file + fStep) in files && (current.rank + rStep) in ranks) {
                possibleMoves.add(
                    MoveInstruction(
                        templateRes = R.string.move_template_knight,
                        args = listOf(
                            Math.abs(rStep), getDirectionRes(rStep, true),
                            Math.abs(fStep), getDirectionRes(fStep, false)
                        ),
                        icon = "♞",
                        df = fStep, dr = rStep,
                        text = "Knight Move" // Legacy placeholder
                    )
                )
            }
        }
    }
    return possibleMoves.random()
}

// --- Логика для режима Knight Vision ---

enum class ChessPiece(val resId: Int, val maxCount: Int = 8) {
    PAWN(R.drawable.pawn),
    ROOK(R.drawable.rook),
    KNIGHT(R.drawable.knight),
    BISHOP(R.drawable.bishop),
    QUEEN(R.drawable.queen),
    KING(R.drawable.king, 1)
}

fun generateObstacles(count: Int, excludeSquare: Square): Map<Square, ChessPiece> {
    val obstacles = mutableMapOf<Square, ChessPiece>()
    val availableSquares = mutableListOf<Square>()

    for (f in 'a'..'h') {
        for (r in 1..8) {
            val sq = Square(f, r)
            if (sq != excludeSquare) availableSquares.add(sq)
        }
    }
    availableSquares.shuffle()

    val pieceCounts = mutableMapOf<ChessPiece, Int>()
    var placed = 0
    var attempt = 0
    while (placed < count && attempt < availableSquares.size) {
        val square = availableSquares[attempt]
        val piece = ChessPiece.values().random()

        val currentCount = pieceCounts.getOrDefault(piece, 0)
        if (currentCount < piece.maxCount) {
            obstacles[square] = piece
            pieceCounts[piece] = currentCount + 1
            placed++
        }
        attempt++
    }
    return obstacles
}

fun getKnightInstruction(current: Square, obstacles: Map<Square, ChessPiece>): MoveInstruction {
    val possibleMoves = mutableListOf<MoveInstruction>()
    val files = 'a'..'h'; val ranks = 1..8
    val knightOffsets = listOf(
        Pair(1, 2), Pair(2, 1), Pair(2, -1), Pair(1, -2),
        Pair(-1, -2), Pair(-2, -1), Pair(-2, 1), Pair(-1, 2)
    )

    knightOffsets.forEach { (fStep, rStep) ->
        val targetFile = current.file + fStep
        val targetRank = current.rank + rStep
        val targetSq = Square(targetFile, targetRank)

        if (targetFile in files && targetRank in ranks && !obstacles.containsKey(targetSq)) {
            possibleMoves.add(
                MoveInstruction(
                    templateRes = R.string.move_template_knight,
                    args = listOf(
                        Math.abs(rStep), getDirectionRes(rStep, true),
                        Math.abs(fStep), getDirectionRes(fStep, false)
                    ),
                    icon = "♞",
                    df = fStep, dr = rStep,
                    text = "Knight Move"
                )
            )
        }
    }

    return if (possibleMoves.isNotEmpty()) possibleMoves.random()
    else MoveInstruction(0, emptyList(), "X", 0, 0, "TRAPPED") // Сохраняем "TRAPPED" в text для логики
}

fun getValidKnightJumps(from: Square, obstacles: Map<Square, ChessPiece>): List<Square> {
    val valid = mutableListOf<Square>()
    val files = 'a'..'h'; val ranks = 1..8
    val knightOffsets = listOf(
        Pair(1, 2), Pair(2, 1), Pair(2, -1), Pair(1, -2),
        Pair(-1, -2), Pair(-2, -1), Pair(-2, 1), Pair(-1, 2)
    )

    knightOffsets.forEach { (fStep, rStep) ->
        val targetFile = from.file + fStep
        val targetRank = from.rank + rStep
        val targetSq = Square(targetFile, targetRank)

        if (targetFile in files && targetRank in ranks && !obstacles.containsKey(targetSq)) {
            valid.add(targetSq)
        }
    }
    return valid
}

// --- НОВЫЙ ХЕЛПЕР ДЛЯ UI ---
// Позволяет получить переведенный текст инструкции прямо в Composable
@Composable
fun resolveInstructionText(instruction: MoveInstruction): String {
    if (instruction.text == "TRAPPED") return "TRAPPED" // Fallback для тех. кейса

    // Преобразуем список аргументов: если это Int (ресурс строки), берем строку, иначе оставляем как есть
    val resolvedArgs = instruction.args.map { arg ->
        if (arg is Int && arg > 1000000) { // Простая эвристика, что это ID ресурса
            stringResource(id = arg)
        } else {
            arg
        }
    }.toTypedArray()

    // Форматируем строку по шаблону
    return if (instruction.templateRes != 0) {
        stringResource(id = instruction.templateRes, formatArgs = resolvedArgs)
    } else {
        instruction.text // Fallback
    }
}