package com.avirajsharma.zenki.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.context.GlobalContext

actual fun createAppDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val context: Context = GlobalContext.get().get()
    val dbFile = context.getDatabasePath("zenki.db")
    return Room.databaseBuilder(context, AppDatabase::class.java, dbFile.absolutePath)
}