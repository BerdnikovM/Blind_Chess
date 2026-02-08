package com.Goldy.blindchess.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource // <--- ВАЖНО
import androidx.compose.ui.unit.dp
import com.Goldy.blindchess.R // <--- ВАЖНО

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedColorsScreen(onBack: () -> Unit, onModeSelected: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.protocol_speed_colors).uppercase()) }, // <-- ЗАМЕНА
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back)) // <-- ЗАМЕНА
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { onModeSelected("zen") }, modifier = Modifier.fillMaxWidth(0.8f)) {
                Text(text = stringResource(R.string.sc_zen), modifier = Modifier.padding(8.dp)) // <-- ЗАМЕНА
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onModeSelected("blitz") }, modifier = Modifier.fillMaxWidth(0.8f)) {
                Text(text = stringResource(R.string.sc_blitz), modifier = Modifier.padding(8.dp)) // <-- ЗАМЕНА
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { onModeSelected("tutorial") },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(text = stringResource(R.string.tutorial), modifier = Modifier.padding(8.dp)) // <-- ЗАМЕНА
            }
        }
    }
}