package com.avirajsharma.zenki.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.avirajsharma.zenki.data.local.entity.AppMetadata
import kotlinx.coroutines.flow.Flow


@Dao
interface AppMetadataDao {

    @Query("SELECT * FROM app_metadata WHERE `key` = :key")
    fun observeByKey(key: String): Flow<AppMetadata?>

    @Query("SELECT * FROM app_metadata WHERE `key` = :key")
    suspend fun findByKey(key: String): AppMetadata

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(metadata: AppMetadata)

    @Query("DELETE FROM app_metadata WHERE `key` = :key")
    suspend fun deleteByKey(key: String)
}