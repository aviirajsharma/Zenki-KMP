package com.avirajsharma.zenki.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avirajsharma.zenki.data.local.entity.Deck
import com.avirajsharma.zenki.data.local.entity.DeckWithCount
import com.avirajsharma.zenki.data.repository.DeckRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val deckRepository: DeckRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = deckRepository.observeDecksWithCount()
        .map { decks -> HomeUiState(decks = decks) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(),
        )

    fun createDeck(name: String, description: String) {
        viewModelScope.launch {
            deckRepository.createDeck(name, description)
        }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch {
            deckRepository.deleteDeck(deck)
        }
    }
}

data class HomeUiState(
    val decks: List<DeckWithCount> = emptyList(),
)
