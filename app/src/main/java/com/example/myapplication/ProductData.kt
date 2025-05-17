import androidx.room.Entity
import androidx.room.PrimaryKey

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

