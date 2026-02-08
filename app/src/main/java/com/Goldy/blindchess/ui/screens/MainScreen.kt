package com.Goldy.blindchess.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Goldy.blindchess.R

@Composable
fun MainScreen(
    windowSizeClass: WindowSizeClass,
    onInitialize: () -> Unit,
    onSettingsClick: () -> Unit // Только настройки, базу данных не трогаем
) {
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
                text = stringResource(R.string.main_title),
                fontSize = if (isCompact) 32.sp else 48.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                style = MaterialTheme.typography.displayMedium
            )

            Spacer(modifier = Modifier.height(if (isCompact) 48.dp else 128.dp))

            // Кнопка Старт
            Button(
                onClick = onInitialize,
                modifier = Modifier.fillMaxWidth(if (isCompact) 0.8f else 0.5f).height(60.dp)
            ) {
                Text(stringResource(R.string.btn_start))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Кнопка Настроек (Базы данных тут нет)
            OutlinedButton(
                onClick = onSettingsClick,
                modifier = Modifier.fillMaxWidth(if (isCompact) 0.8f else 0.5f).height(60.dp)
            ) {
                Text(stringResource(R.string.btn_settings))
            }
        }
    }
}