package Databases

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.Instant

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
)

@RequiresApi(Build.VERSION_CODES.O)
fun ProductData.daysUntilExpiry(): Long? {
    val expiryDate = expiration_date ?: return null

    return try {
        val expirationLocalDate = Instant.ofEpochMilli(expiryDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val currentDate = LocalDate.now()
        ChronoUnit.DAYS.between(currentDate, expirationLocalDate)
    } catch (e: Exception) {
        null  // กรณีที่เกิดข้อผิดพลาดในการแปลงวันที่
    }
}

