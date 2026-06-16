package com.avirajsharma.zenki.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avirajsharma.zenki.data.local.entity.Flashcard
import com.avirajsharma.zenki.data.remote.GroqError
import com.avirajsharma.zenki.data.remote.GroqService
import com.avirajsharma.zenki.data.repository.DeckRepository
import com.avirajsharma.zenki.data.repository.FlashcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AIGenerateViewModel(
    private val groqService: GroqService,
    private val deckRepository: DeckRepository,
    private val flashcardRepository: FlashcardRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AIGenerateUiState>(AIGenerateUiState.Idle())
    val uiState: StateFlow<AIGenerateUiState> = _uiState.asStateFlow()

    private var generatedCards: List<Flashcard> = emptyList()

    fun updateTopic(text: String) {
        val state = _uiState.value
        if (state is AIGenerateUiState.Idle) {
            _uiState.value = state.copy(topic = text)
        }
    }

    fun updateCardCount(count: Int) {
        val state = _uiState.value
        if (state is AIGenerateUiState.Idle) {
            _uiState.value = state.copy(cardCount = count)
        }
    }

    fun generate() {
        val state = _uiState.value as? AIGenerateUiState.Idle ?: return
        if (state.topic.isBlank()) {
            _uiState.value = state.copy(error = "Enter a topic first")
            return
        }
        _uiState.value = AIGenerateUiState.Loading
        viewModelScope.launch {
            try {
                generatedCards = groqService.generateFlashcards(state.topic, state.cardCount)
                _uiState.value = AIGenerateUiState.Preview(
                    cards = generatedCards,
                    topic = state.topic,
                )
            } catch (e: Exception) {
                val message = when (e) {
                    is GroqError -> e.message ?: "AI error"
                    is io.ktor.client.network.sockets.ConnectTimeoutException,
                    is io.ktor.client.network.sockets.SocketTimeoutException,
                        -> "Connection timed out. Check your network."
                    else -> "Error: ${e.message ?: "Unknown error"}"
                }
                _uiState.value = AIGenerateUiState.Error(message)
            }
        }
    }

    fun saveToNewDeck(deckName: String) {
        if (generatedCards.isEmpty()) return
        val topic = when (val s = _uiState.value) {
            is AIGenerateUiState.Preview -> s.topic
            else -> "AI-generated"
        }
        viewModelScope.launch {
            val deckId = deckRepository.createDeck(deckName, "AI-generated from: $topic")
            val cardsWithDeck = generatedCards.map { it.copy(deckId = deckId) }
            flashcardRepository.addFlashcards(cardsWithDeck)
            _uiState.value = AIGenerateUiState.Saved(deckId)
        }
    }

    fun dismissError() {
        _uiState.value = AIGenerateUiState.Idle()
    }

    fun reset() {
        _uiState.value = AIGenerateUiState.Idle()
        generatedCards = emptyList()
    }
}

sealed class AIGenerateUiState {
    data class Idle(
        val topic: String = "",
        val cardCount: Int = 10,
        val error: String? = null,
    ) : AIGenerateUiState()

    data object Loading : AIGenerateUiState()

    data class Preview(
        val cards: List<Flashcard>,
        val topic: String,
    ) : AIGenerateUiState()

    data class Error(val message: String) : AIGenerateUiState()

    data class Saved(val deckId: Long) : AIGenerateUiState()
}
