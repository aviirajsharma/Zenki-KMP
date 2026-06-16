package com.avirajsharma.zenki.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.avirajsharma.zenki.data.local.entity.Flashcard
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards WHERE deckId = :deckId ORDER BY createdAt DESC")
    fun observeByDeckId(deckId: Long): Flow<List<Flashcard>>

    @Query("SELECT * FROM flashcards WHERE id = :id")
    suspend fun findById(id: Long): Flashcard?

    @Insert
    suspend fun insert(flashcard: Flashcard): Long

    @Insert
    suspend fun insertAll(flashcards: List<Flashcard>): List<Long>

    @Update
    suspend fun update(flashcard: Flashcard)

    @Delete
    suspend fun delete(flashcard: Flashcard)

    @Query("DELETE FROM flashcards WHERE deckId = :deckId")
    suspend fun deleteByDeckId(deckId: Long)

    @Query("UPDATE flashcards SET isKnown = :known WHERE id = :id")
    suspend fun markKnown(id: Long, known: Boolean)

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId AND isKnown = 0")
    suspend fun findUnknownByDeckId(deckId: Long): List<Flashcard>
}
