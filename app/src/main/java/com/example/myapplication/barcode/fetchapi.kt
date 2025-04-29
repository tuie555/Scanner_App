package com.example.myapplication.barcode

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// 1. สร้าง data class ที่จะเก็บข้อมูล
@Serializable
data class ProductResponse(
    val product: Product? = null
)

@Serializable
data class Product(
    val product_name: String? = null,
    val image_url: String? = null,
    val categories: String? = null
)

// 2. ฟังก์ชันดึงข้อมูลจาก API

suspend fun getProductData(barcode: String) {
    withContext(Dispatchers.IO) { // 👈 ย้ายมาทำงานใน IO thread
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        try {
            val response: ProductResponse = client.get("https://world.openfoodfacts.org/api/v0/product/$barcode.json").body()

            response.product?.let { product ->
                Log.d("GetProductData", "ชื่อสินค้า: ${product.product_name}")
                Log.d("GetProductData", "ประเภทสินค้า: ${product.categories}")
                Log.d("GetProductData", "รูปสินค้า: ${product.image_url}")
            } ?: run {
                Log.d("GetProductData", "ไม่พบข้อมูลสินค้า")
            }

        } catch (e: Exception) {
            Log.e("GetProductData", "เกิดข้อผิดพลาด", e)
        } finally {
            client.close()
        }
    }
}