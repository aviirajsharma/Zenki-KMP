package com.avirajsharma.zenki.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.avirajsharma.zenki.currentTimeMillis

@Entity(
    tableName = "study_sessions",
    foreignKeys = [
        ForeignKey(
            entity = Deck::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("deckId")],
)
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: Long,
    val cardsStudied: Int,
    val correctCount: Int,
    val startedAt: Long = currentTimeMillis(),
    val completedAt: Long? = null,
)
