package com.example.myapplication

import InventoryDatabase
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.data.ProductData
import kotlinx.coroutines.flow.Flow

class Productviewmodel(application: Application) : AndroidViewModel(application) {
    private  val dao = InventoryDatabase.getDatabase(application).productDao()

    val productFlow: Flow<List<ProductData>> =
        dao.getAllProducts()
}