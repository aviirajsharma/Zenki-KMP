package com.avirajsharma.zenki.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.avirajsharma.zenki.ui.screens.ai.AIGenerateScreen
import com.avirajsharma.zenki.ui.screens.card.AddEditCardScreen
import com.avirajsharma.zenki.ui.screens.deck.DeckDetailScreen
import com.avirajsharma.zenki.ui.screens.home.HomeScreen
import com.avirajsharma.zenki.ui.screens.quiz.QuizModeScreen
import com.avirajsharma.zenki.ui.screens.study.StudyModeScreen
import com.avirajsharma.zenki.ui.viewmodel.HomeViewModel
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey {
    @Serializable
    data object Home : Screen()

    @Serializable
    data class DeckDetail(val deckId: Long) : Screen()

    @Serializable
    data class StudyMode(val deckId: Long) : Screen()

    @Serializable
    data class QuizMode(val deckId: Long) : Screen()

    @Serializable
    data class AddEditCard(val deckId: Long, val cardId: Long? = null) : Screen()

    @Serializable
    data object AIGenerate : Screen()
}

@Composable
fun AppNavigation(
    backStack: NavBackStack<Screen>,
    homeViewModel: HomeViewModel,
    paddingValues: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    val currentScreen = backStack.lastOrNull() ?: Screen.Home

    Crossfade(
        targetState = currentScreen,
        animationSpec = tween(durationMillis = 300),
        modifier = modifier,
    ) { screen ->
        when (screen) {
            is Screen.Home -> {
                HomeScreen(
                    viewModel = homeViewModel,
                    contentPadding = paddingValues,
                    onDeckClick = { deckId ->
                        backStack.add(Screen.DeckDetail(deckId))
                    },
                )
            }
            is Screen.DeckDetail -> {
                DeckDetailScreen(
                    deckId = screen.deckId,
                    onBack = { backStack.removeLastOrNull() },
                    onStudy = { deckId -> backStack.add(Screen.StudyMode(deckId)) },
                    onQuiz = { deckId -> backStack.add(Screen.QuizMode(deckId)) },
                    onAddCard = { deckId -> backStack.add(Screen.AddEditCard(deckId)) },
                )
            }
            is Screen.StudyMode -> {
                StudyModeScreen(
                    deckId = screen.deckId,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
            is Screen.QuizMode -> {
                QuizModeScreen(
                    deckId = screen.deckId,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
            is Screen.AddEditCard -> {
                AddEditCardScreen(
                    deckId = screen.deckId,
                    cardId = screen.cardId,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
            is Screen.AIGenerate -> {
                AIGenerateScreen(
                    contentPadding = paddingValues,
                    onNavigateToDeck = { deckId ->
                        backStack.clear()
                        backStack.add(Screen.Home)
                        backStack.add(Screen.DeckDetail(deckId))
                    },
                )
            }
        }
    }
}
