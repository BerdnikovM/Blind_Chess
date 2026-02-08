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
import androidx.compose.ui.res.stringResource // <-- ВАЖНО
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Goldy.blindchess.R // <-- ВАЖНО
import com.Goldy.blindchess.utils.BaseChessboard
import com.Goldy.blindchess.utils.Square

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnightVisionTutorialScreen(onFinish: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    fun getBitmap(resId: Int) = BitmapFactory.decodeResource(context.resources, resId).asImageBitmap()

    // Используем ресурсы для текстов (6 шагов)
    val tutorialTexts = listOf(
        stringResource(R.string.tut_kv_step_1),
        stringResource(R.string.tut_kv_step_2),
        stringResource(R.string.tut_kv_step_3),
        stringResource(R.string.tut_kv_step_4),
        stringResource(R.string.tut_kv_step_5),
        stringResource(R.string.tut_kv_step_6)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.how_to_play)) }, // <-- ЗАМЕНА
                navigationIcon = { IconButton(onClick = onFinish) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } }, // <-- ЗАМЕНА
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    when (step) {
                        0 -> {
                            Text(stringResource(R.string.protocol_knight_vision), fontSize = 36.sp, fontWeight = FontWeight.Bold) // <-- ЗАМЕНА
                            Spacer(Modifier.height(24.dp))
                            // Если вы использовали Image(bitmap = ...), можно вернуть. Если нет, просто текст.
                            // Для простоты оставил только текст, если картинки не загружены
                        }
                        1 -> {
                            Text(stringResource(R.string.kv_memorize_ex), color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 32.sp) // <-- ЗАМЕНА (Memorize!)
                            Spacer(Modifier.height(16.dp))
                            Box(Modifier.fillMaxWidth(0.9f)) {
                                BaseChessboard(
                                    onDrawSquare = { square ->
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
                        2, 3 -> {
                            if (step == 3) {
                                Text("♞", fontSize = 100.sp)
                                Spacer(Modifier.height(16.dp))
                            }
                            Text(
                                text = if (step == 2) stringResource(R.string.kv_start_ex) else stringResource(R.string.kv_move_ex), // <-- ЗАМЕНА (Start: d2 / 2 Up...)
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        4, 5 -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(R.string.kv_click_jumps), style = MaterialTheme.typography.titleMedium) // <-- ЗАМЕНА (Click valid jumps)
                                Spacer(Modifier.height(8.dp))
                                Box(Modifier.fillMaxWidth(0.9f)) {
                                    BaseChessboard(
                                        onDrawSquare = { square ->
                                            val validTargets = listOf(
                                                Square('g', 5), Square('g', 3), Square('f', 2),
                                                Square('d', 2), Square('c', 3), Square('c', 5)
                                            )
                                            val blockedTargets = listOf(Square('d', 6), Square('f', 6))

                                            if (square in validTargets) {
                                                drawCircle(Color.Green.copy(alpha = 0.5f), radius = size.minDimension/3)
                                            }
                                            if (square in blockedTargets) {
                                                drawCircle(Color.Red.copy(alpha = 0.5f), radius = size.minDimension/3)
                                            }

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
                        Text(if (step < tutorialTexts.size - 1) stringResource(R.string.next) else stringResource(R.string.play)) // <-- ЗАМЕНА
                    }
                }
            }
        }
    }
}