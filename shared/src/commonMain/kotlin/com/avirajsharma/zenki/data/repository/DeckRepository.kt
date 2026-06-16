package com.avirajsharma.zenki.data.repository

import com.avirajsharma.zenki.currentTimeMillis
import com.avirajsharma.zenki.data.local.dao.DeckDao
import com.avirajsharma.zenki.data.local.entity.Deck
import com.avirajsharma.zenki.data.local.entity.DeckWithCount
import kotlinx.coroutines.flow.Flow

class DeckRepository(
    private val deckDao: DeckDao,
) {
    fun observeDecks(): Flow<List<Deck>> = deckDao.observeAll()

    fun observeDecksWithCount(): Flow<List<DeckWithCount>> = deckDao.observeAllWithCount()

    suspend fun createDeck(name: String, description: String): Long {
        val deck = Deck(name = name, description = description)
        return deckDao.insert(deck)
    }

    suspend fun updateDeck(deck: Deck) = deckDao.update(deck)

    suspend fun deleteDeck(deck: Deck) = deckDao.delete(deck)

    suspend fun findDeck(id: Long): Deck? = deckDao.findById(id)

    suspend fun cardCount(deckId: Long): Int = deckDao.countCards(deckId)

    suspend fun knownCardCount(deckId: Long): Int = deckDao.countKnownCards(deckId)

    suspend fun markStudied(deckId: Long) =
        deckDao.updateLastStudied(deckId, currentTimeMillis())
}
