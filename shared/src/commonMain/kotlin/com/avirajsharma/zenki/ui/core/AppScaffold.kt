package com.avirajsharma.zenki.ui.core

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import com.avirajsharma.zenki.theme.ThemeMode
import com.avirajsharma.zenki.theme.ThemeToggleIconButton

private val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
private val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
private const val DurationMedium2 = 300
private const val DurationShort4 = 200

@Composable
fun AppScaffold(
    title: String,
    themeMode: ThemeMode,
    onThemeToggle: (ThemeMode) -> Unit,
    selectedTab: TopLevelTab? = null,
    onTabSelected: (TopLevelTab) -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    showTopBar: Boolean = true,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            if (showTopBar) {
                StudyTopAppBar(
                    title = title,
                    themeMode = themeMode,
                    onThemeToggle = onThemeToggle,
                )
            }
        },
        bottomBar = {
            BottomBarVisibility(
                visible = selectedTab != null,
                content = {
                    if (selectedTab != null) {
                        AppBottomNavigation(
                            selectedTab = selectedTab,
                            onTabSelected = onTabSelected,
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = floatingActionButton,
        content = content,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun StudyTopAppBar(
    title: String,
    themeMode: ThemeMode,
    onThemeToggle: (ThemeMode) -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        actions = {
            ThemeToggleIconButton(
                currentMode = themeMode,
                onToggle = onThemeToggle,
            )
        },
    )
}

@Composable
private fun BottomBarVisibility(
    visible: Boolean,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            tween(durationMillis = DurationMedium2, easing = EmphasizedDecelerate),
        ) + slideInVertically(
            tween(durationMillis = DurationMedium2, easing = EmphasizedDecelerate),
            initialOffsetY = { it },
        ),
        exit = fadeOut(
            tween(durationMillis = DurationShort4, easing = EmphasizedAccelerate),
        ) + slideOutVertically(
            tween(durationMillis = DurationShort4, easing = EmphasizedAccelerate),
            targetOffsetY = { it },
        ),
        content = { content() },
    )
}
