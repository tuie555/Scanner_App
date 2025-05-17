package com.example.myapplication

import InventoryDatabase
import ProductData
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow

class Productviewmodel(application: Application) : AndroidViewModel(application) {
    private  val dao = InventoryDatabase.getDatabase(application).productDao()

    val productFlow: Flow<List<ProductData>> =
        dao.getAllProducts()
}