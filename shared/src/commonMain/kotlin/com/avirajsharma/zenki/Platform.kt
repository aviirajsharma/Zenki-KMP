package com.avirajsharma.zenki

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect object AppConfig {
    val groqApiKey: String
}

expect fun currentTimeMillis(): Long