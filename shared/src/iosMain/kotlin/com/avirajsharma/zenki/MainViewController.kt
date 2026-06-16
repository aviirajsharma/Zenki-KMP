package com.avirajsharma.zenki

import androidx.compose.ui.window.ComposeUIViewController
import com.avirajsharma.zenki.di.appModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(appModule)
}

fun MainViewController() = ComposeUIViewController(
    configure = {
        // Koin is initialized before Compose tree
        initKoin()
    }
) { App() }