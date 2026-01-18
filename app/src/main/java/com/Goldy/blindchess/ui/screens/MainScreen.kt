package com.Goldy.blindchess.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

@Composable
fun MainScreen(windowSizeClass: WindowSizeClass, onInitialize: () -> Unit) {
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "BlindChess",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "CLASSIFIED TRAINING TOOL",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(if (isCompact) 64.dp else 128.dp))
            Button(
                onClick = onInitialize,
                modifier = Modifier.fillMaxWidth(if (isCompact) 0.8f else 0.5f)
            ) {
                Text(text = "INITIALIZE SEQUENCE >", modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { /* TODO: Handle settings */ },
                modifier = Modifier.fillMaxWidth(if (isCompact) 0.8f else 0.5f)
            ) {
                Text(text = "SETTINGS", modifier = Modifier.padding(8.dp))
            }
        }
    }
}
