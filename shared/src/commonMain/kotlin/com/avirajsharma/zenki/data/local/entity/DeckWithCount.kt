package com.avirajsharma.zenki.data.local.entity

import androidx.room.Embedded

data class DeckWithCount(
    @Embedded val deck: Deck,
    val cardCount: Int,
    val knownCount: Int,
) {
    val progress: Float
        get() = if (cardCount == 0) 0f else knownCount.toFloat() / cardCount.toFloat()
}