package com.avirajsharma.zenki.data.repository

import com.avirajsharma.zenki.currentTimeMillis
import com.avirajsharma.zenki.data.local.dao.StudySessionDao
import com.avirajsharma.zenki.data.local.entity.StudySession
import kotlinx.coroutines.flow.Flow

class StudySessionRepository(
    private val sessionDao: StudySessionDao,
) {
    fun observeSessions(deckId: Long): Flow<List<StudySession>> =
        sessionDao.observeByDeckId(deckId)

    suspend fun recordSession(
        deckId: Long,
        cardsStudied: Int,
        correctCount: Int,
    ): Long {
        val session = StudySession(
            deckId = deckId,
            cardsStudied = cardsStudied,
            correctCount = correctCount,
            completedAt = currentTimeMillis(),
        )
        return sessionDao.insert(session)
    }

    suspend fun deckStats(deckId: Long): SessionStats {
        val stats = sessionDao.aggregateStats(deckId)
        return SessionStats(
            totalStudied = stats.totalStudied ?: 0,
            totalCorrect = stats.totalCorrect ?: 0,
            sessionCount = stats.sessionCount ?: 0,
        )
    }

    suspend fun allStats(): SessionStats {
        val stats = sessionDao.aggregateAllStats()
        return SessionStats(
            totalStudied = stats.totalStudied ?: 0,
            totalCorrect = stats.totalCorrect ?: 0,
            sessionCount = stats.sessionCount ?: 0,
        )
    }
}

data class SessionStats(
    val totalStudied: Int,
    val totalCorrect: Int,
    val sessionCount: Int,
)
