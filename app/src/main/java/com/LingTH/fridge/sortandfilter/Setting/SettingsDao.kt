package com.LingTH.fridge.sortandfilter.Setting

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings_table WHERE id = 0")
    suspend fun getSettings(): Settings?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertOrUpdate(settings: Settings)
}