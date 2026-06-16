package com.avirajsharma.zenki.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.avirajsharma.zenki.data.local.dao.AppMetadataDao
import com.avirajsharma.zenki.data.local.dao.DeckDao
import com.avirajsharma.zenki.data.local.dao.FlashcardDao
import com.avirajsharma.zenki.data.local.dao.StudySessionDao
import com.avirajsharma.zenki.data.local.entity.AppMetadata
import com.avirajsharma.zenki.data.local.entity.Deck
import com.avirajsharma.zenki.data.local.entity.Flashcard
import com.avirajsharma.zenki.data.local.entity.StudySession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO


@Database(
    entities = [
        AppMetadata::class,
        Deck::class,
        Flashcard::class,
        StudySession::class
    ],
    version = 1,
    exportSchema = false
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appMetadataDao(): AppMetadataDao
    abstract fun deckDao(): DeckDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun studySessionDao(): StudySessionDao
}


@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

expect fun createAppDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>


fun createAppDatabase(): AppDatabase {
    return createAppDatabaseBuilder()//Platform-specific builder laata hai.
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)//Database queries background thread pe chalengi
        .build()
}