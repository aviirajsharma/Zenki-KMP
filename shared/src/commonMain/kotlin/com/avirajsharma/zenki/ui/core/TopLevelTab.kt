package com.avirajsharma.zenki.ui.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopLevelTab(
    val label: String,
    val icon: ImageVector,
) {
    Decks("Decks", Icons.Default.Home),
    AIGenerate("AI", Icons.Default.AutoAwesome),
}