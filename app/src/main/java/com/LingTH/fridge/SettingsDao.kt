package Databases

import androidx.room.*

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings_table WHERE id = 0")
    suspend fun getSettings(): Settings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: Settings)
}
