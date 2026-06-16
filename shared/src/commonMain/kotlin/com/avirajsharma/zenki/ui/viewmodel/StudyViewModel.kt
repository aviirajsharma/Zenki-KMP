package com.avirajsharma.zenki.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avirajsharma.zenki.data.local.entity.Flashcard
import com.avirajsharma.zenki.data.repository.DeckRepository
import com.avirajsharma.zenki.data.repository.FlashcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StudyViewModel(
    private val deckId: Long,
    private val deckRepository: DeckRepository,
    private val flashcardRepository: FlashcardRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudyUiState())
    val uiState: StateFlow<StudyUiState> = _uiState.asStateFlow()

    private var cards: List<Flashcard> = emptyList()

    init {
        viewModelScope.launch {
            cards = flashcardRepository.observeFlashcards(deckId).first()
            if (cards.isNotEmpty()) {
                _uiState.value = StudyUiState(
                    cards = cards,
                    currentCard = cards.first(),
                    currentIndex = 0,
                    progress = 1f / cards.size,
                )
            }
        }
    }

    fun flipCard() {
        _uiState.value = _uiState.value.copy(isFlipped = !_uiState.value.isFlipped)
    }

    fun nextCard() {
        val state = _uiState.value
        val nextIndex = (state.currentIndex + 1).coerceAtMost(cards.size - 1)
        if (nextIndex != state.currentIndex) {
            _uiState.value = state.copy(
                currentIndex = nextIndex,
                currentCard = cards[nextIndex],
                isFlipped = false,
                progress = (nextIndex + 1).toFloat() / cards.size,
            )
        }
    }

    fun previousCard() {
        val state = _uiState.value
        val prevIndex = (state.currentIndex - 1).coerceAtLeast(0)
        if (prevIndex != state.currentIndex) {
            _uiState.value = state.copy(
                currentIndex = prevIndex,
                currentCard = cards[prevIndex],
                isFlipped = false,
                progress = (prevIndex + 1).toFloat() / cards.size,
            )
        }
    }

    fun markKnown(known: Boolean) {
        val card = _uiState.value.currentCard ?: return
        viewModelScope.launch {
            flashcardRepository.markKnown(card.id, known)
        }
        nextCard()
    }

    fun finishStudy() {
        viewModelScope.launch {
            deckRepository.markStudied(deckId)
        }
    }
}

data class StudyUiState(
    val cards: List<Flashcard> = emptyList(),
    val currentCard: Flashcard? = null,
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val progress: Float = 0f,
)
