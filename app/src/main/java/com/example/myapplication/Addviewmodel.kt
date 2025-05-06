package com.example.myapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.barcode.getProductData
import com.example.myapplication.data.ProductData
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Addviewmodel: ViewModel() {
        var productData by mutableStateOf<ProductData?>(null)
        var isManualEntry by mutableStateOf(false)

//        fun loadProduct(barcode: String) {
//            viewModelScope.launch {
//                val data = getProductData(barcode)
//                if (data != null) {
//                    productData = data
//                } else {
//                    // ให้ผู้ใช้กรอกเอง
//                    isManualEntry = true
//                    productData = ProductData(
//                        barcode = barcode,
//                        product_name = "",
//                        categories = "",
//                        image_url = "",
//                    )
//                }
//            }
//        }
//
//    fun updateProduct(
//        name: String,
//        categories: String,
//        imageUrl: String,
//        add_day: String,
//        expie_day: String,
//        notes: String
//    ) {
//        productData = productData?.copy(
//            product_name = name,
//            categories = categories,
//            image_url = imageUrl,
//        )
//    }
//
//    fun saveProduct(
//        add_day: String,
//        expie_day: String,
//        notes: String,
//        onSaved: () -> Unit
//    ) {
//        viewModelScope.launch {
//
//
//            productData?.let {
//                // TODO: Save to database
//                // productDao.insert(it)
//                onSaved()
//            }
//        }
//    }

}

