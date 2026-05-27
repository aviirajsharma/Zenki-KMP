package com.avirajsharma.zenki

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform