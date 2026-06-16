package com.avirajsharma.zenki.ui.screens.study

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avirajsharma.zenki.ui.viewmodel.StudyUiState
import com.avirajsharma.zenki.ui.viewmodel.StudyViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun StudyModeScreen(
    deckId: Long,
    onBack: () -> Unit,
) {
    val viewModel = koinInject<StudyViewModel> { parametersOf(deckId) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.currentIndex, state.cards.size) {
        if (state.cards.isNotEmpty() && state.currentIndex >= state.cards.size - 1) {
            viewModel.finishStudy()
        }
    }

    StudyContent(
        state = state,
        onBack = onBack,
        onFlip = viewModel::flipCard,
        onNext = viewModel::nextCard,
        onPrevious = viewModel::previousCard,
        onMarkKnown = { viewModel.markKnown(true) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudyContent(
    state: StudyUiState,
    onBack: () -> Unit,
    onFlip: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onMarkKnown: () -> Unit,
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Study Mode") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                )
                LinearProgressIndicator(
                    progress = { state.progress },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    ) { padding ->
        if (state.cards.isEmpty()) {
            EmptyStudyState(padding)
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            CounterBadge(state.currentIndex + 1, state.cards.size)

            FlipCard(
                isFlipped = state.isFlipped,
                front = state.currentCard?.question ?: "",
                back = state.currentCard?.answer ?: "",
                onFlip = onFlip,
            )

            StudyControls(
                onPrevious = onPrevious,
                onNext = onNext,
                onMarkKnown = onMarkKnown,
                canGoPrevious = state.currentIndex > 0,
                canGoNext = state.currentIndex < state.cards.size - 1,
            )
        }
    }
}

@Composable
private fun CounterBadge(current: Int, total: Int) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            text = "$current / $total",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun FlipCard(
    isFlipped: Boolean,
    front: String,
    back: String,
    onFlip: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onFlip),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            contentAlignment = Alignment.Center,
        ) {
            AnimatedContent(
                targetState = isFlipped,
                transitionSpec = {
                    fadeIn(tween(300)) + scaleIn(tween(300), initialScale = 0.9f) togetherWith
                            fadeOut(tween(300)) + scaleOut(tween(300), targetScale = 0.9f)
                },
                label = "flip",
            ) { flipped ->
                Text(
                    text = if (flipped) back else front,
                    style = if (flipped) {
                        MaterialTheme.typography.headlineSmall
                    } else {
                        MaterialTheme.typography.headlineMedium
                    },
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun StudyControls(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onMarkKnown: () -> Unit,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FilterChip(
            selected = false,
            onClick = onMarkKnown,
            label = { Text("Mark as Known") },
            leadingIcon = {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
            },
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            IconButton(
                onClick = onPrevious,
                enabled = canGoPrevious,
            ) {
                Icon(Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = "Previous")
            }
            IconButton(
                onClick = onNext,
                enabled = canGoNext,
            ) {
                Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Next")
            }
        }
    }
}

@Composable
private fun EmptyStudyState(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No cards to study",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
