package com.avirajsharma.zenki.di

import com.avirajsharma.zenki.AppConfig
import com.avirajsharma.zenki.data.local.AppDatabase
import com.avirajsharma.zenki.data.local.createAppDatabase
import com.avirajsharma.zenki.data.local.dao.AppMetadataDao
import com.avirajsharma.zenki.data.local.dao.DeckDao
import com.avirajsharma.zenki.data.local.dao.FlashcardDao
import com.avirajsharma.zenki.data.local.dao.StudySessionDao
import com.avirajsharma.zenki.data.remote.GroqService
import com.avirajsharma.zenki.data.remote.createHttpClient
import com.avirajsharma.zenki.data.repository.DeckRepository
import com.avirajsharma.zenki.data.repository.FlashcardRepository
import com.avirajsharma.zenki.data.repository.StudySessionRepository
import com.avirajsharma.zenki.data.repository.ThemeModeRepository
import com.avirajsharma.zenki.ui.viewmodel.AIGenerateViewModel
import com.avirajsharma.zenki.ui.viewmodel.AddEditCardViewModel
import com.avirajsharma.zenki.ui.viewmodel.DeckDetailViewModel
import com.avirajsharma.zenki.ui.viewmodel.HomeViewModel
import com.avirajsharma.zenki.ui.viewmodel.QuizViewModel
import com.avirajsharma.zenki.ui.viewmodel.StudyViewModel
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule: Module = module {
    single<HttpClient> { createHttpClient() }
    single { createAppDatabase() }

    single<AppMetadataDao> { get<AppDatabase>().appMetadataDao() }
    single<DeckDao> { get<AppDatabase>().deckDao() }
    single<FlashcardDao> { get<AppDatabase>().flashcardDao() }
    single<StudySessionDao> { get<AppDatabase>().studySessionDao() }

    single { ThemeModeRepository(get()) }
    single { DeckRepository(get()) }
    single { FlashcardRepository(get()) }
    single { StudySessionRepository(get()) }
    single { GroqService(get(), apiKey = AppConfig.groqApiKey) }

    single { HomeViewModel(get()) }
    factory { (deckId: Long) -> DeckDetailViewModel(deckId, get(), get()) }
    factory { (deckId: Long) -> StudyViewModel(deckId, get(), get()) }
    factory { (deckId: Long) -> QuizViewModel(deckId, get(), get(), get()) }
    factory { (deckId: Long, cardId: Long?) -> AddEditCardViewModel(deckId, cardId, get()) }
    single { AIGenerateViewModel(get(), get(), get()) }
}
