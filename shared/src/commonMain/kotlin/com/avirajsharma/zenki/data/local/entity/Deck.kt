package com.avirajsharma.zenki.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.avirajsharma.zenki.currentTimeMillis

@Entity(tableName = "decks")
data class Deck(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val createdAt: Long = currentTimeMillis(),
    val lastStudiedAt: Long? = null,
)
