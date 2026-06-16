package com.avirajsharma.zenki

import platform.UIKit.UIDevice
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
class IOSPlatform : Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

@OptIn(ExperimentalNativeApi::class)
actual fun getPlatform(): Platform = IOSPlatform()

actual object AppConfig {
    actual val groqApiKey: String = BuildKonfig.GROQ_API_KEY
}

actual fun currentTimeMillis(): Long {
    return (platform.Foundation.NSDate().timeIntervalSinceReferenceDate() * 1000).toLong() +
            978307200000L
}