package com.avirajsharma.zenki.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avirajsharma.zenki.data.local.entity.Flashcard
import com.avirajsharma.zenki.data.repository.DeckRepository
import com.avirajsharma.zenki.data.repository.FlashcardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DeckDetailViewModel(
    private val deckId: Long,
    private val deckRepository: DeckRepository,
    private val flashcardRepository: FlashcardRepository,
) : ViewModel() {

    val flashcards: StateFlow<List<Flashcard>> = flashcardRepository.observeFlashcards(deckId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    val cardCount: StateFlow<Int> = flashcards
        .map { it.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0,
        )

    fun deleteCard(card: Flashcard) {
        viewModelScope.launch {
            flashcardRepository.deleteFlashcard(card)
        }
    }

    fun deleteDeck() {
        viewModelScope.launch {
            deckRepository.findDeck(deckId)?.let { deckRepository.deleteDeck(it) }
        }
    }
}
