package com.avirajsharma.zenki.ui.core

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberAppSnackbarHostState(): SnackbarHostState = remember { SnackbarHostState() }

suspend fun SnackbarHostState.showSuccess(message: String) {
    showSnackbar(message, duration = SnackbarDuration.Short)
}

suspend fun SnackbarHostState.showError(message: String) {
    showSnackbar(message, duration = SnackbarDuration.Long)
}