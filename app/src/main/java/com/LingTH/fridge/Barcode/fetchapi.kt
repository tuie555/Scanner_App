package com.LingTH.fridge.Barcode

import Databases.ProductData
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


// 1. สร้าง data class ที่จะเก็บข้อมูล
@Serializable
data class ProductResponse(
    val product: Product? = null
)

@Serializable
data class Product(
    @SerialName("product_name")
    val productName: String? = null,

    @SerialName("image_url")
    val imageUrl: String? = null,

    val categories: String? = null
)



suspend fun getProductData(barcode: String): ProductData? = withContext(Dispatchers.IO) {

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true

                prettyPrint = true
            })
        }
    }

    try {
        val response: ProductResponse = client
            .get("https://world.openfoodfacts.org/api/v0/product/$barcode.json")
            .body()


        response.product?.let { product ->
            val name = product.productName ?: "Unknown Product"
            val categories = product.categories ?: "Unknown Categories"
            val imageUrl = product.imageUrl ?: ""

            Log.d("Databases.ProductData", "Name: $name, Categories: $categories, Image URL: $imageUrl")

            return@withContext ProductData(
                barcode = barcode,
                product_name = name,
                categories = categories,
                image_url = imageUrl,
                expiration_date = null,
                add_day = null,
                notes = ""
            )
        } ?: run {
            Log.e("GetProductData", "Product field is null in response")
            return@withContext null
        }


    } catch (e: Exception) {
        Log.e("GetProductData", "An error occurred", e)
        null
    } finally {
        client.close()
    }
}



