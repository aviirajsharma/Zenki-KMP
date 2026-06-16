package com.avirajsharma.zenki.data.repository

import com.avirajsharma.zenki.data.local.dao.AppMetadataDao
import com.avirajsharma.zenki.data.local.entity.AppMetadata
import com.avirajsharma.zenki.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemeModeRepository(
    private val metadataDao: AppMetadataDao,
) {
    fun observeThemeMode(): Flow<ThemeMode> =
        metadataDao.observeByKey(THEME_MODE_KEY)
            .map { metadata ->
                metadata?.let { ThemeMode.fromString(it.value) } ?: ThemeMode.System
            }

    suspend fun saveThemeMode(mode: ThemeMode) {
        metadataDao.save(AppMetadata(THEME_MODE_KEY, mode.name.uppercase()))
    }

    companion object {
        private const val THEME_MODE_KEY = "theme_mode"
    }
}
