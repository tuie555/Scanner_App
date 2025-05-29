package Databases

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings_table")
data class Settings(
    @PrimaryKey val id: Int = 0,  // ใช้ id เดียวเสมอ เพื่อให้มีแค่แถวเดียว
    val alertBeforeExpiry: String,
    val alertMode: String,
    val repeatAlert: String,
    val email: String
)
fun defaultSettings(): Settings {
    return Settings(
        id = 0,
        alertBeforeExpiry = "3 days",
        alertMode = "Normal",
        repeatAlert = "3",
        email = ""
    )
}