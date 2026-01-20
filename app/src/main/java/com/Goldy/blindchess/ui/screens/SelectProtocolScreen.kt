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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.windowsizeclass.WindowSizeClass

private data class Protocol(val title: String, val subtitle: String, val icon: ImageVector, val route: String)

private val protocols = listOf(
    Protocol("Speed Colors", "Rapidly identify square colors", Icons.Default.ColorLens, "speed_colors"),
    Protocol("The Walker", "Follow the blindfold path", Icons.Default.DirectionsWalk, "the_walker_menu"),
    Protocol("Knight Vision", "Obstacle memory and jump visualization", Icons.Default.Visibility, "knight_vision"),
    Protocol("Personnel Database", "Check your high scores", Icons.Default.Leaderboard, "personnel_database")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectProtocolScreen(
    windowSizeClass: WindowSizeClass, 
    onBack: () -> Unit,
    onProtocolSelected: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SELECT PROTOCOL") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            Icon(protocol.icon, contentDescription = protocol.title, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = protocol.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = protocol.subtitle, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
