package com.Goldy.blindchess.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource // <-- ВАЖНЫЙ ИМПОРТ 1
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import com.Goldy.blindchess.R // <-- ВАЖНЫЙ ИМПОРТ 2 (Ваш пакет)

// Изменили типы title и subtitle на Int (ID ресурса)
private data class Protocol(val titleResId: Int, val subtitleResId: Int, val icon: ImageVector, val route: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectProtocolScreen(
    windowSizeClass: WindowSizeClass,
    onBack: () -> Unit,
    onProtocolSelected: (String) -> Unit
) {
    // Список создаем здесь, чтобы иметь доступ к R.string
    val protocols = listOf(
        Protocol(R.string.protocol_speed_colors, R.string.protocol_desc_speed, Icons.Default.ColorLens, "speed_colors"),
        Protocol(R.string.protocol_the_walker, R.string.protocol_desc_walker, Icons.Default.DirectionsWalk, "the_walker_menu"),
        Protocol(R.string.protocol_knight_vision, R.string.protocol_desc_knight, Icons.Default.Visibility, "knight_vision"),
        Protocol(R.string.btn_database, R.string.db_title, Icons.Default.Leaderboard, "personnel_database") // База данных
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.select_protocol_title)) }, // <-- ЗАМЕНА 1
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back)) // <-- ЗАМЕНА 2
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(protocols) { protocol ->
                ProtocolCard(protocol = protocol, onClick = { onProtocolSelected(protocol.route) })
            }
        }
    }
}

@Composable
private fun ProtocolCard(protocol: Protocol, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // stringResource(protocol.titleResId) - достаем строку по ID
            Icon(protocol.icon, contentDescription = stringResource(protocol.titleResId), modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = stringResource(protocol.titleResId), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = stringResource(protocol.subtitleResId), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}