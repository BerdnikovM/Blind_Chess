package com.Goldy.blindchess.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource // <-- ВАЖНО
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Goldy.blindchess.R // <-- ВАЖНО
import com.Goldy.blindchess.utils.Square
import com.Goldy.blindchess.utils.TutorialChessboard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheWalkerTutorialScreen(onFinish: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }

    val tutorialTexts = listOf(
        stringResource(R.string.tut_walker_step_1),
        stringResource(R.string.tut_walker_step_2),
        stringResource(R.string.tut_walker_step_3),
        stringResource(R.string.tut_walker_step_4),
        stringResource(R.string.tut_walker_step_5),
        stringResource(R.string.tut_walker_step_6),
        stringResource(R.string.tut_walker_step_7),
        stringResource(R.string.tut_walker_step_8)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.how_to_play)) }, // <-- ЗАМЕНА (How to Play)
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
                        0 -> Text(stringResource(R.string.protocol_the_walker), fontSize = 40.sp, fontWeight = FontWeight.Bold) // <-- ЗАМЕНА
                        1 -> Text("c3", fontSize = 80.sp, fontWeight = FontWeight.Bold)
                        2 -> {
                            Text("↑", fontSize = 100.sp)
                            Text(stringResource(R.string.tut_walker_ex_move), fontSize = 40.sp) // <-- ЗАМЕНА (Пример хода: "2 Up" / "2 Вверх")
                        }
                        3, 4, 5 -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(Modifier.fillMaxWidth(0.9f)) {
                                    TutorialChessboard(
                                        startSquare = Square('c', 3),
                                        endSquare = Square('c', 5)
                                    )
                                }
                                Spacer(Modifier.height(16.dp))
                                Text("c3  →  c5", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        6 -> {
                            Row { Text("❤❤❤", fontSize = 40.sp, color = Color.Red) }
                        }
                        else -> Text(stringResource(R.string.protocol_the_walker), fontSize = 40.sp, fontWeight = FontWeight.Bold) // <-- ЗАМЕНА
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
                        onClick = { if (step < tutorialTexts.size - 1) step++ else onFinish() },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(if (step < tutorialTexts.size - 1) stringResource(R.string.next) else stringResource(R.string.play)) // <-- ЗАМЕНА
                    }
                }
            }
        }
    }
}