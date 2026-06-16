package com.avirajsharma.zenki.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.avirajsharma.zenki.currentTimeMillis

@Entity(
    tableName = "flashcards",
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
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: Long,
    val question: String,
    val answer: String,
    val isKnown: Boolean = false,
    val createdAt: Long = currentTimeMillis(),
)
