import androidx.room.PrimaryKey

// Data class สำหรับสินค้า
data class ProductData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val product_name: String,
    val categories: String,
    val image_url: String
)

// สร้าง list เก็บสินค้า (เหมือน database)
val productList = mutableListOf<ProductData>()

// ฟังก์ชันเพิ่มสินค้า
suspend fun addProductToDatabase(dao: ProductDao, name: String, categories: String, imageUrl: String) {
    val product = ProductData(product_name = name, categories = categories, image_url = imageUrl)
    dao.insert(product)
}

