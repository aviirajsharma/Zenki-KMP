package com.avirajsharma.zenki.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ThemeToggleIconButton(
    currentMode: ThemeMode,
    onToggle: (ThemeMode) -> Unit,
) {
    IconButton(onClick = { onToggle(cycleTheme(currentMode)) }) {
        Icon(
            imageVector = resolveThemeIcon(currentMode),
            contentDescription = "Toggle theme",
        )
    }
}

private fun cycleTheme(current: ThemeMode): ThemeMode =
    when (current) {
        ThemeMode.Light -> ThemeMode.Dark
        ThemeMode.Dark -> ThemeMode.System
        ThemeMode.System -> ThemeMode.Light
    }

private fun resolveThemeIcon(mode: ThemeMode): ImageVector =
    when (mode) {
        ThemeMode.Light -> Icons.Default.LightMode
        ThemeMode.Dark -> Icons.Default.DarkMode
        ThemeMode.System -> Icons.AutoMirrored.Filled.Help
    }
