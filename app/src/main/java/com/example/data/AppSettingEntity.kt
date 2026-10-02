package com.example.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "app_settings")
data class AppSettingEntity(
    @PrimaryKey
    val key: String,
    val value: String,
    val description: String = "",
    val category: String = "GENERAL", // MODERATION, BACKEND, SOCIAL, GENERAL
    val updatedAt: Long = System.currentTimeMillis()
)

@Dao
interface AppSettingDao {
    @Query("SELECT * FROM app_settings ORDER BY category, `key`")
    fun getAllSettings(): Flow<List<AppSettingEntity>>

    @Query("SELECT * FROM app_settings WHERE `key` = :key LIMIT 1")
    fun getSetting(key: String): Flow<AppSettingEntity?>

    @Query("SELECT value FROM app_settings WHERE `key` = :key LIMIT 1")
    suspend fun getValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: AppSettingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(settings: List<AppSettingEntity>)

    @Query("UPDATE app_settings SET value = :value, updatedAt = :time WHERE `key` = :key")
    suspend fun updateValue(key: String, value: String, time: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM app_settings")
    suspend fun getCount(): Int
}
