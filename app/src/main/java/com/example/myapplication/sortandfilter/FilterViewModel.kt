package com.example.myapplication.sortandfilter

import Databases.ProductDao
import Databases.ProductData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FilterViewModel (dao: ProductDao): ViewModel(){
    val allCategories = dao.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProductsWithPhotos = dao.getWithPhotos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProductsWithoutPhotos = dao.getWithoutPhotos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExpirationDates = dao.getAllExpirationDates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allAddedDates = dao.getAllAddedDates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    var selectedCategory = MutableStateFlow<List<String>>(emptyList())
        private set

    var selectedExpiredIn = MutableStateFlow<List<String>>(emptyList())
        private set

    var selectedAdded = MutableStateFlow<List<String>>(emptyList())
        private set

    var selectedAddedPhoto = MutableStateFlow<List<String>>(emptyList())
        private set

    var selectedExpirationDate = MutableStateFlow<List<String>>(emptyList())
        private set

    var productName = MutableStateFlow("")
        private set

    // 🟢 Setters
    fun setSelectedCategory(value: List<String>) { selectedCategory.value = value }
    fun setSelectedExpiredIn(value: List<String>) { selectedExpiredIn.value = value }
    fun setSelectedAdded(value: List<String>) { selectedAdded.value = value }
    fun setSelectedAddedPhoto(value: List<String>) { selectedAddedPhoto.value = value }
    fun setSelectedExpirationDate(value: List<String>) { selectedExpirationDate.value = value }
    fun setProductName(value: String) { productName.value = value }
    fun getFilteredByPhoto(option: String,dao: ProductDao): Flow<List<ProductData>> {
        return when (option) {
            "Added Photo" -> allProductsWithPhotos
            "NO Photo" -> allProductsWithoutPhotos
            else -> dao.getAllProducts()
        }
    }
}