package com.avirajsharma.zenki.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avirajsharma.zenki.data.repository.FlashcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEditCardViewModel(
    private val deckId: Long,
    private val cardId: Long?,
    private val flashcardRepository: FlashcardRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditCardUiState())
    val uiState: StateFlow<AddEditCardUiState> = _uiState.asStateFlow()

    init {
        if (cardId != null) {
            viewModelScope.launch {
                flashcardRepository.findById(cardId)?.let { card ->
                    _uiState.value = AddEditCardUiState(
                        question = card.question,
                        answer = card.answer,
                        isEditing = true,
                    )
                }
            }
        }
    }

    fun updateQuestion(text: String) {
        _uiState.value = _uiState.value.copy(question = text, error = null)
    }

    fun updateAnswer(text: String) {
        _uiState.value = _uiState.value.copy(answer = text, error = null)
    }

    fun save(onComplete: () -> Unit) {
        val state = _uiState.value
        if (state.question.isBlank() || state.answer.isBlank()) {
            _uiState.value = state.copy(error = "Both fields are required")
            return
        }
        _uiState.value = state.copy(isSaving = true)
        viewModelScope.launch {
            if (state.isEditing && cardId != null) {
                flashcardRepository.findById(cardId)?.let { existing ->
                    flashcardRepository.updateFlashcard(
                        existing.copy(question = state.question, answer = state.answer),
                    )
                }
            } else {
                flashcardRepository.addFlashcard(deckId, state.question, state.answer)
            }
            _uiState.value = _uiState.value.copy(isSaving = false)
            onComplete()
        }
    }
}

data class AddEditCardUiState(
    val question: String = "",
    val answer: String = "",
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
)
