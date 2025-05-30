package Databases

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.Instant
import java.time.ZoneOffset

@Entity(tableName = "product_table")
data class ProductData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,  // primary key

    val barcode: String,
    val product_name: String,
    val categories: String,
    val image_url: String,
    val expiration_date: Long?,
    val add_day: Long?,
    val notes: String
) : Serializable // ✅ เพิ่มตรงนี้

fun ProductData.daysUntilExpiry(): Long? {
    val expiryDate = expiration_date ?: return null

    return try {
        val expirationLocalDate = Instant.ofEpochMilli(expiryDate)
            .atZone(ZoneOffset.UTC) // ✅ ปรับตรงนี้
            .toLocalDate()

        val currentDate = LocalDate.now(ZoneOffset.UTC) // ✅ ปรับตรงนี้ด้วย

        val days = ChronoUnit.DAYS.between(currentDate, expirationLocalDate)

        // ✅ เพิ่ม Log ตรงนี้เพื่อตรวจสอบ
        Log.d("daysUntilExpiry", "Now: $currentDate, Exp: $expirationLocalDate, Days left: $days")

        days
    } catch (e: Exception) {
        Log.e("daysUntilExpiry", "❌ Error parsing date: ${e.message}")
        null
    }
}



