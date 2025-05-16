package com.example.myapplication

import ProductDao
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.barcode.getProductData
import com.example.myapplication.data.ProductData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Addviewmodel(private val productDao: ProductDao) : ViewModel()
 {


        var productData by mutableStateOf<ProductData?>(null)
        var isManualEntry by mutableStateOf(false)

        fun loadProduct(barcode: String) {
            viewModelScope.launch {
                val data = getProductData(barcode)
                if (data != null) {
                    productData = data
                } else {
                    // ให้ผู้ใช้กรอกเอง
                    isManualEntry = true
                    productData = ProductData(
                        barcode = barcode,
                        product_name = "",
                        categories = "",
                        image_url = "",
                            expiration_date = null,
                            add_day = null,
                            notes = ""
                    )
                }
            }
        }

    fun updateProduct(
        name: String,
        categories: String,
        imageUrl: String,
        add_day: Long,
        expie_day: Long,
        notes: String
    ) {
        productData = productData?.copy(
            product_name = name,
            categories = categories,
            image_url = imageUrl,
                expiration_date = add_day,
                add_day = expie_day,
                notes = notes
        )
    }

     fun saveProduct(
         name: String,
         categories: String,
         imageUrl: String,
         add_day: Long,
         expie_day: Long,
         notes: String,
         onSaved: () -> Unit
     ) {
         viewModelScope.launch {
             // ถ้า productData ยัง null ให้สร้างใหม่ก่อน
             if (productData == null) {
                 productData = ProductData(
                     barcode = "123454681", // หรือใส่ค่า barcode ที่เหมาะสม
                     product_name = name,
                     categories = categories,
                     image_url = imageUrl,
                     expiration_date = expie_day,
                     add_day = add_day,
                     notes = notes
                 )
             } else {
                 // ถ้ามีแล้ว ก็ copy ค่าใหม่
                 Log.d("SaveProductDebug", "productData is not null, updating")
                 productData = productData?.copy(
                     product_name = name,
                     categories = categories,
                     image_url = imageUrl,
                     expiration_date = expie_day,
                     add_day = add_day,
                     notes = notes
                 )
             }


             try {
                 productData?.let {
                     Log.d("SaveProductDebug", "Calling insertProduct()")
                         productDao.insertProduct(it)
                     Log.d("SaveProductDebug", "Insert completed")
                     onSaved()
                 } ?: Log.d("SaveProductDebug", "productData is STILL null")
             } catch (e: Exception) {
                 Log.e("SaveProductError", "Error inserting product", e)
             }
         }
    }



 }

