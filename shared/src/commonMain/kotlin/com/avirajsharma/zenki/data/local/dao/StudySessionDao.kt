package com.avirajsharma.zenki.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.avirajsharma.zenki.data.local.entity.StudySession
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {
    @Insert
    suspend fun insert(session: StudySession): Long

    @Query("SELECT * FROM study_sessions WHERE deckId = :deckId ORDER BY startedAt DESC")
    fun observeByDeckId(deckId: Long): Flow<List<StudySession>>

    @Query(
        """
        SELECT 
            SUM(cardsStudied) as totalStudied,
            SUM(correctCount) as totalCorrect,
            COUNT(*) as sessionCount
        FROM study_sessions 
        WHERE deckId = :deckId
        """,
    )
    suspend fun aggregateStats(deckId: Long): StudyStats

    @Query(
        """
        SELECT 
            SUM(cardsStudied) as totalStudied,
            SUM(correctCount) as totalCorrect,
            COUNT(*) as sessionCount
        FROM study_sessions
        """,
    )
    suspend fun aggregateAllStats(): StudyStats
}

data class StudyStats(
    val totalStudied: Int?,
    val totalCorrect: Int?,
    val sessionCount: Int?,
)
