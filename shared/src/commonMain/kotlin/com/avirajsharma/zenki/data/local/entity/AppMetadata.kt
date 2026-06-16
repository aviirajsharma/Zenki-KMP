package com.avirajsharma.zenki.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_metadata")
data class AppMetadata(
    @PrimaryKey
    val key : String,
    val value : String
)