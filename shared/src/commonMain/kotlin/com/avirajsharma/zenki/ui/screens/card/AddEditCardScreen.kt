package com.avirajsharma.zenki.ui.screens.card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avirajsharma.zenki.ui.viewmodel.AddEditCardUiState
import com.avirajsharma.zenki.ui.viewmodel.AddEditCardViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun AddEditCardScreen(
    deckId: Long,
    cardId: Long?,
    onBack: () -> Unit,
) {
    val viewModel = koinInject<AddEditCardViewModel> { parametersOf(deckId, cardId) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AddEditCardContent(
        state = state,
        onBack = onBack,
        onQuestionChange = viewModel::updateQuestion,
        onAnswerChange = viewModel::updateAnswer,
        onSave = { viewModel.save(onComplete = onBack) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditCardContent(
    state: AddEditCardUiState,
    onBack: () -> Unit,
    onQuestionChange: (String) -> Unit,
    onAnswerChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Edit Card" else "Add Card") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
        ) {
            OutlinedTextField(
                value = state.question,
                onValueChange = onQuestionChange,
                label = { Text("Question") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving,
                isError = state.error != null && state.question.isBlank(),
                supportingText = {
                    if (state.error != null && state.question.isBlank()) {
                        Text("Question is required")
                    }
                },
            )

            OutlinedTextField(
                value = state.answer,
                onValueChange = onAnswerChange,
                label = { Text("Answer") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                minLines = 3,
                enabled = !state.isSaving,
                isError = state.error != null && state.answer.isBlank(),
                supportingText = {
                    if (state.error != null && state.answer.isBlank()) {
                        Text("Answer is required")
                    }
                },
            )

            if (state.error != null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            Button(
                onClick = onSave,
                enabled = !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
                Text("Save")
            }
        }
    }
}
