package com.avirajsharma.zenki.ui.screens.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avirajsharma.zenki.ui.viewmodel.QuizUiState
import com.avirajsharma.zenki.ui.viewmodel.QuizViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun QuizModeScreen(
    deckId: Long,
    onBack: () -> Unit,
) {
    val viewModel = koinInject<QuizViewModel> { parametersOf(deckId) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    QuizContent(
        state = state,
        onBack = onBack,
        onSelectAnswer = viewModel::selectAnswer,
        onNext = viewModel::nextQuestion,
        onRetry = viewModel::retryMistakes,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizContent(
    state: QuizUiState,
    onBack: () -> Unit,
    onSelectAnswer: (Int) -> Unit,
    onNext: () -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
        ) {
            when (state) {
                is QuizUiState.Loading -> QuizLoading()
                is QuizUiState.Empty -> QuizEmpty(onBack)
                is QuizUiState.Question -> QuestionView(
                    state = state,
                    onSelectAnswer = onSelectAnswer,
                )
                is QuizUiState.Answered -> AnswerView(
                    state = state,
                    onNext = onNext,
                )
                is QuizUiState.Results -> ResultsView(
                    state = state,
                    onBack = onBack,
                    onRetry = onRetry,
                )
            }
        }
    }
}

@Composable
private fun QuizLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun QuizEmpty(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Need at least 4 cards for a quiz",
            style = MaterialTheme.typography.headlineSmall,
        )
        Button(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
            Text("Go Back")
        }
    }
}

@Composable
private fun QuestionView(
    state: QuizUiState.Question,
    onSelectAnswer: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        QuizHeader(state.currentIndex, state.total, state.score)

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.outlinedCardElevation(defaultElevation = 2.dp),
        ) {
            Text(
                text = state.question.question,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(24.dp),
                textAlign = TextAlign.Center,
            )
        }

        AnswerOptions(
            options = state.question.options,
            onSelect = onSelectAnswer,
        )
    }
}

@Composable
private fun AnswerView(
    state: QuizUiState.Answered,
    onNext: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        QuizHeader(state.currentIndex, state.total, state.score)

        AnswerCard(state)

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Next")
        }
    }
}

@Composable
private fun AnswerCard(state: QuizUiState.Answered) {
    val color = if (state.isCorrect) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.errorContainer
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = if (state.isCorrect) "Correct!" else "Incorrect",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = "Answer: ${state.question.options[state.question.correctIndex]}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun ResultsView(
    state: QuizUiState.Results,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Quiz Complete",
            style = MaterialTheme.typography.headlineMedium,
        )

        Box(
            modifier = Modifier.padding(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                progress = { state.percentage / 100f },
                modifier = Modifier.size(120.dp),
                strokeWidth = 8.dp,
            )
            Text(
                text = "${state.percentage}%",
                style = MaterialTheme.typography.headlineMedium,
            )
        }

        Text(
            text = "${state.correct} / ${state.total} correct",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(
            modifier = Modifier.padding(top = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FilledTonalButton(onClick = onRetry) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Text("Retry Mistakes", modifier = Modifier.padding(start = 8.dp))
            }
            Button(onClick = onBack) {
                Text("Done")
            }
        }
    }
}

@Composable
private fun QuizHeader(current: Int, total: Int, score: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${current + 1} / $total",
            style = MaterialTheme.typography.labelLarge,
        )
        AssistChip(
            onClick = {},
            label = { Text("Score: $score") },
            leadingIcon = { Icon(Icons.Default.CheckCircle, null) },
        )
    }
}

@Composable
private fun AnswerOptions(
    options: List<String>,
    onSelect: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEachIndexed { index, option ->
            Button(
                onClick = { onSelect(index) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(option)
            }
        }
    }
}
