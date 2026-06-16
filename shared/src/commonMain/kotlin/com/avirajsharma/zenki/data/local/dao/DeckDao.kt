package com.avirajsharma.zenki.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.avirajsharma.zenki.data.local.entity.Deck
import com.avirajsharma.zenki.data.local.entity.DeckWithCount
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Deck>>

    @Query("""
        SELECT d.*,
               COUNT(f.id) AS cardCount,
               SUM(CASE WHEN f.isKnown = 1 THEN 1 ELSE 0 END) AS knownCount
        FROM decks d
        LEFT JOIN flashcards f ON d.id = f.deckId
        GROUP BY d.id
        ORDER BY d.createdAt DESC
    """)
    fun observeAllWithCount(): Flow<List<DeckWithCount>>

    @Query("SELECT * FROM decks WHERE id = :id")
    suspend fun findById(id: Long): Deck?

    @Insert
    suspend fun insert(deck: Deck): Long

    @Update
    suspend fun update(deck: Deck)

    @Delete
    suspend fun delete(deck: Deck)

    @Query("SELECT COUNT(*) FROM flashcards WHERE deckId = :deckId")
    suspend fun countCards(deckId: Long): Int

    @Query("SELECT COUNT(*) FROM flashcards WHERE deckId = :deckId AND isKnown = 1")
    suspend fun countKnownCards(deckId: Long): Int

    @Query("UPDATE decks SET lastStudiedAt = :timestamp WHERE id = :deckId")
    suspend fun updateLastStudied(deckId: Long, timestamp: Long)
}
