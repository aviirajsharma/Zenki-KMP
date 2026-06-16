package com.avirajsharma.zenki

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual object AppConfig {
    actual val groqApiKey: String = BuildKonfig.GROQ_API_KEY
}

actual fun currentTimeMillis(): Long = System.currentTimeMillis()