package com.Goldy.blindchess.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Goldy.blindchess.utils.ScoreManager
import com.Goldy.blindchess.utils.WalkerDifficulty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonnelDatabaseScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scoreManager = remember { ScoreManager(context) }

    // Получаем данные
    val zenBest = scoreManager.getZenHighScore()
    val blitzBest = scoreManager.getBlitzHighScore()

    val walkerEasy = scoreManager.getWalkerHighScore(WalkerDifficulty.EASY)
    val walkerMedium = scoreManager.getWalkerHighScore(WalkerDifficulty.MEDIUM)
    val walkerHard = scoreManager.getWalkerHighScore(WalkerDifficulty.HARD)

    // Получаем рекорд Knight Vision (метод уже есть в вашем ScoreManager)
    val knightVisionBest = scoreManager.getKnightVisionHighScore()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PERSONNEL DATABASE") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- SPEED COLORS ---
            Text(
                text = "SPEED COLORS",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))
            RecordCard(title = "Zen Best Streak", score = zenBest.toString())
            Spacer(modifier = Modifier.height(8.dp))
            RecordCard(title = "Blitz High Score", score = blitzBest.toString())

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(32.dp))

            // --- THE WALKER ---
            Text(
                text = "THE WALKER (Best Wave)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))
            RecordCard(title = "Easy Mode", score = "Wave $walkerEasy")
            Spacer(modifier = Modifier.height(8.dp))
            RecordCard(title = "Medium Mode", score = "Wave $walkerMedium")
            Spacer(modifier = Modifier.height(8.dp))
            RecordCard(title = "Hard Mode", score = "Wave $walkerHard")

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(32.dp))

            // --- KNIGHT VISION (Добавлено) ---
            Text(
                text = "KNIGHT VISION (Best Wave)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))
            RecordCard(title = "Static Interference", score = "Wave $knightVisionBest")

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun RecordCard(title: String, score: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(
                text = score,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}