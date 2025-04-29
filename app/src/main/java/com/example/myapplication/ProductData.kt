// Data class สำหรับสินค้า
data class ProductData(
    val product_name: String,
    val categories: String,
    val image_url: String
)

// สร้าง list เก็บสินค้า (เหมือน database)
val productList = mutableListOf<ProductData>()

// ฟังก์ชันเพิ่มสินค้า
fun addProductToList(name: String, categories: String, imageUrl: String) {
    val product = ProductData(name, categories, imageUrl)
    productList.add(product)
}
