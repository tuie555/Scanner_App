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
import kotlinx.coroutines.launch

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
             productData = productData?.copy(
                 product_name = name,
                 categories = categories,
                 image_url = imageUrl,
                 expiration_date = expie_day,
                 add_day = add_day,
                 notes = notes
             )

             Log.d("SaveProduct", "productData before let: $productData")
             productData?.let {
                 Log.d("SaveProduct", "Saving product: $it")
                 productDao.insertProduct(it)
                 onSaved()
             }
         }
     }



 }

