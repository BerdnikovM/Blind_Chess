package com.Goldy.blindchess.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Goldy.blindchess.R
import com.Goldy.blindchess.utils.BaseChessboard
import com.Goldy.blindchess.utils.Square

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnightVisionTutorialScreen(onFinish: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    // Хелпер для загрузки картинок
    fun getBitmap(resId: Int) = BitmapFactory.decodeResource(context.resources, resId).asImageBitmap()

    val tutorialTexts = listOf(
        "Welcome to Knight Vision! This mode trains your calculation and memory for Knight moves.",
        "Phase 1: Memorization. You will see obstacles on the board (e.g., at d6 and f6). Remember them!",
        // ИСПРАВЛЕНИЕ: Изменили пример старта на d2
        "Phase 2: The Path. You will get a starting square (e.g., d2) and a Knight move instruction.",
        "Follow the path in your mind! If you start at d2 and go '2 Up, 1 Right', you end up at e4.",
        "Phase 3: The Attack. From the final position (e4), click ALL valid squares the Knight can jump to.",
        // ИСПРАВЛЕНИЕ: Уточнили текст про препятствия
        "Be careful! You must avoid the obstacles you memorized earlier (d6, f6). Good luck!"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("How to Play") },
                navigationIcon = { IconButton(onClick = onFinish) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ВЕРХНЯЯ ЧАСТЬ: Демонстрация (Адаптивная)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    when (step) {
                        0 -> { // Приветствие
                            Text("Knight Vision", fontSize = 36.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(24.dp))
                        }
                        1 -> { // Запоминание (Препятствия на d6, f6)
                            Text("Memorize!", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 32.sp)
                            Spacer(Modifier.height(16.dp))
                            Box(Modifier.fillMaxWidth(0.9f)) {
                                BaseChessboard(
                                    onDrawSquare = { square ->
                                        // Рисуем демо-препятствия
                                        if (square == Square('d', 6) || square == Square('f', 6)) {
                                            try {
                                                drawImage(
                                                    image = getBitmap(R.drawable.pawn),
                                                    dstSize = IntSize((size.width * 0.8).toInt(), (size.height * 0.8).toInt()),
                                                    dstOffset = IntOffset((size.width * 0.1).toInt(), (size.height * 0.1).toInt())
                                                )
                                            } catch (e: Exception) {}
                                        }
                                    }
                                )
                            }
                        }
                        2, 3 -> { // Инструкции (Старт d2 -> 2 Up, 1 Right)
                            // Сначала проверяем, нужно ли показать иконку (для шага 3)
                            if (step == 3) {
                                Text("♞", fontSize = 100.sp)
                                Spacer(Modifier.height(16.dp)) // Отступ между иконкой и текстом
                            }

                            // Затем выводим сам текст
                            Text(
                                text = if (step == 2) "Start: d2" else "2 Up, 1 Right",
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        4, 5 -> { // Угадывание (Конь на e4, препятствия d6, f6)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Click valid jumps!", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(8.dp))
                                Box(Modifier.fillMaxWidth(0.9f)) {
                                    BaseChessboard(
                                        onDrawSquare = { square ->
                                            // Сценарий: Конь пришел на e4.
                                            // Препятствия (запомненные): d6, f6.

                                            // Валидные прыжки с e4 (исключая d6 и f6)
                                            val validTargets = listOf(
                                                Square('g', 5), Square('g', 3), Square('f', 2),
                                                Square('d', 2), Square('c', 3), Square('c', 5)
                                            )
                                            // Заблокированные прыжки с e4
                                            val blockedTargets = listOf(Square('d', 6), Square('f', 6))

                                            // Зеленые - куда надо кликать
                                            if (square in validTargets) {
                                                drawCircle(Color.Green.copy(alpha = 0.5f), radius = size.minDimension/3)
                                            }
                                            // Красный - куда нельзя (там препятствие)
                                            // ИСПРАВЛЕНИЕ: Теперь красные круги совпадают с препятствиями из фазы 1
                                            if (square in blockedTargets) {
                                                drawCircle(Color.Red.copy(alpha = 0.5f), radius = size.minDimension/3)
                                            }

                                            // Рисуем коня на e5 для наглядности
                                            if (square == Square('e', 4)) {
                                                try {
                                                    drawImage(
                                                        image = getBitmap(R.drawable.knight),
                                                        dstSize = IntSize((size.width * 0.8).toInt(), (size.height * 0.8).toInt()),
                                                        dstOffset = IntOffset((size.width * 0.1).toInt(), (size.height * 0.1).toInt())
                                                    )
                                                } catch (e: Exception) {}
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // НИЖНЯЯ ЧАСТЬ: Диалог
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = tutorialTexts.getOrElse(step) { "" },
                        fontSize = 18.sp,
                        minLines = 3,
                        lineHeight = 24.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (step < tutorialTexts.size - 1) step++ else onFinish()
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(if (step < tutorialTexts.size - 1) "NEXT" else "PLAY")
                    }
                }
            }
        }
    }
}