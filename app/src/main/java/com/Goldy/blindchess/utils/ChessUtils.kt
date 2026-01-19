package com.Goldy.blindchess.utils

import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

@Composable
fun ChessboardWithNotation(squareToHighlight: Square, diagonals: Pair<String, String>) {
    val files = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
    val highlightColor = Color.Red.copy(alpha = 0.8f)
    val diagonalColor = Color.Blue.copy(alpha = 0.25f)
    val allDiagSquares = getDiagonalList(diagonals.first) + getDiagonalList(diagonals.second)

    Row(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
        Column(modifier = Modifier.fillMaxHeight().width(24.dp)) {
            for (rank in 8 downTo 1) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = rank.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
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