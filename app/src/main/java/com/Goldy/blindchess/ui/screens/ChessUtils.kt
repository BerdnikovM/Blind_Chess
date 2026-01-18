package com.Goldy.blindchess.ui.screens

import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.ui.graphics.Color

// --- Общие данные для всех режимов Speed Colors ---

enum class SquareColor { WHITE, BLACK }

data class Square(val file: Char, val rank: Int) {
    override fun toString(): String = "$file$rank"
}

val ColorToVector = TwoWayConverter<Color, AnimationVector4D>(
    convertToVector = { color ->
        AnimationVector4D(color.red, color.green, color.blue, color.alpha)
    },
    convertFromVector = { vector ->
        Color(red = vector.v1, green = vector.v2, blue = vector.v3, alpha = vector.v4)
    }
)

fun getRandomSquare(): Square = Square(('a'..'h').random(), (1..8).random())

fun getSquareColor(square: Square): SquareColor {
    val fileIndex = square.file - 'a'
    val rankIndex = square.rank - 1
    return if ((fileIndex + rankIndex) % 2 == 0) SquareColor.BLACK else SquareColor.WHITE
}

// Теперь функция здесь и доступна всем
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