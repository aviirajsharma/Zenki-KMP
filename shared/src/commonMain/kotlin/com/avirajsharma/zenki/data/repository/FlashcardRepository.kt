package com.avirajsharma.zenki.data.repository

import com.avirajsharma.zenki.data.local.dao.FlashcardDao
import com.avirajsharma.zenki.data.local.entity.Flashcard
import kotlinx.coroutines.flow.Flow

class FlashcardRepository(
    private val flashcardDao: FlashcardDao,
) {
    fun observeFlashcards(deckId: Long): Flow<List<Flashcard>> =
        flashcardDao.observeByDeckId(deckId)

    suspend fun addFlashcard(deckId: Long, question: String, answer: String): Long {
        val card = Flashcard(deckId = deckId, question = question, answer = answer)
        return flashcardDao.insert(card)
    }

    suspend fun addFlashcards(cards: List<Flashcard>): List<Long> =
        flashcardDao.insertAll(cards)

    suspend fun updateFlashcard(card: Flashcard) = flashcardDao.update(card)

    suspend fun deleteFlashcard(card: Flashcard) = flashcardDao.delete(card)

    suspend fun markKnown(cardId: Long, known: Boolean) =
        flashcardDao.markKnown(cardId, known)

    suspend fun findUnknownCards(deckId: Long): List<Flashcard> =
        flashcardDao.findUnknownByDeckId(deckId)

    suspend fun findById(id: Long): Flashcard? = flashcardDao.findById(id)
}
