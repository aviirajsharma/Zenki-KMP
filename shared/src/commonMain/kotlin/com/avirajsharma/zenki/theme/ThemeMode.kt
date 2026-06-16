package com.avirajsharma.zenki.theme

enum class ThemeMode {
    Light,
    Dark,
    System;

    companion object {
        fun fromString(value: String): ThemeMode =
            entries.find { it.name.equals(value, ignoreCase = true) } ?: System
    }
}