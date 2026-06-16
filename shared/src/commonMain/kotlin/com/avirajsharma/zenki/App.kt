package com.avirajsharma.zenki

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavBackStack
import com.avirajsharma.zenki.data.repository.DeckRepository
import com.avirajsharma.zenki.data.repository.ThemeModeRepository
import com.avirajsharma.zenki.navigation.AppNavigation
import com.avirajsharma.zenki.navigation.Screen
import com.avirajsharma.zenki.theme.AppTheme
import com.avirajsharma.zenki.theme.ThemeMode
import com.avirajsharma.zenki.ui.core.AppScaffold
import com.avirajsharma.zenki.ui.core.TopLevelTab
import com.avirajsharma.zenki.ui.core.rememberAppSnackbarHostState
import com.avirajsharma.zenki.ui.screens.home.CreateDeckDialog
import com.avirajsharma.zenki.ui.screens.home.HomeFab
import com.avirajsharma.zenki.ui.viewmodel.AIGenerateViewModel
import com.avirajsharma.zenki.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

import zenki.shared.generated.resources.Res
import zenki.shared.generated.resources.compose_multiplatform

@Composable
fun App() {
    val themeRepository = koinInject<ThemeModeRepository>()
    val deckRepository = koinInject<DeckRepository>()
    val homeViewModel = koinInject<HomeViewModel>()
    val aiGenerateViewModel = koinInject<AIGenerateViewModel>()
    val themeMode by themeRepository.observeThemeMode()
        .collectAsState(ThemeMode.System)
    val scope = rememberCoroutineScope()
    val decks by homeViewModel.uiState.collectAsState()

    val backStack = remember {
        NavBackStack<Screen>(Screen.Home)
    }
    val currentScreen: Screen = backStack.lastOrNull() ?: Screen.Home

    var showAddDeckDialog by remember { mutableStateOf(false) }

    val selectedTab = when (currentScreen) {
        is Screen.Home -> TopLevelTab.Decks
        is Screen.AIGenerate -> TopLevelTab.AIGenerate
        else -> null
    }

    AppTheme(themeMode = themeMode) {
        val snackbarHostState = rememberAppSnackbarHostState()

        val isTopLevelScreen = currentScreen is Screen.Home || currentScreen is Screen.AIGenerate

        AppScaffold(
            title = resolveTitle(currentScreen),
            themeMode = themeMode,
            onThemeToggle = { mode ->
                scope.launch { themeRepository.saveThemeMode(mode) }
            },
            selectedTab = selectedTab,
            onTabSelected = { tab ->
                if (tab == TopLevelTab.AIGenerate) {
                    aiGenerateViewModel.reset()
                }
                backStack.clear()
                backStack.add(
                    when (tab) {
                        TopLevelTab.Decks -> Screen.Home
                        TopLevelTab.AIGenerate -> Screen.AIGenerate
                    },
                )
            },
            snackbarHostState = snackbarHostState,
            showTopBar = isTopLevelScreen,
            floatingActionButton = {
                if (currentScreen is Screen.Home) {
                    HomeFab(onClick = { showAddDeckDialog = true })
                }
            },
        ) { paddingValues ->
            AppNavigation(
                backStack = backStack,
                homeViewModel = homeViewModel,
                paddingValues = paddingValues,
                modifier = Modifier,
            )

            if (showAddDeckDialog) {
                CreateDeckDialog(
                    onDismiss = { showAddDeckDialog = false },
                    onCreate = { name, description ->
                        homeViewModel.createDeck(name, description)
                        showAddDeckDialog = false
                    },
                )
            }
        }
    }
}

private fun resolveTitle(screen: Screen): String =
    when (screen) {
        is Screen.Home -> "Zenki"
        is Screen.DeckDetail -> "Deck"
        is Screen.StudyMode -> "Study"
        is Screen.QuizMode -> "Quiz"
        is Screen.AddEditCard -> "Card"
        is Screen.AIGenerate -> "AI Generate"
    }