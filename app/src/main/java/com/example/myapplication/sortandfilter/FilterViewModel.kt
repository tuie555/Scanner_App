package com.example.myapplication.sortandfilter

import Databases.ProductDao
import Databases.ProductData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FilterViewModel(private val dao: ProductDao) : ViewModel() {

    // --- ข้อมูลตั้งต้น ---
    val allCategories = dao.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProducts = dao.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProductsWithPhotos = dao.getWithPhotos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProductsWithoutPhotos = dao.getWithoutPhotos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExpirationDates = dao.getAllExpirationDates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAddedDates = dao.getAllAddedDates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Filter criteria ---
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

    // --- Setters ---
    fun setSelectedCategory(value: List<String>) { selectedCategory.value = value }
    fun setSelectedExpiredIn(value: List<String>) { selectedExpiredIn.value = value }
    fun setSelectedAdded(value: List<String>) { selectedAdded.value = value }
    fun setSelectedAddedPhoto(value: List<String>) { selectedAddedPhoto.value = value }
    fun setSelectedExpirationDate(value: List<String>) { selectedExpirationDate.value = value }

    fun getFilteredByPhoto(option: String,dao: ProductDao): Flow<List<ProductData>> {
        return when (option) {
            "Added Photo" -> allProductsWithPhotos
            "NO Photo" -> allProductsWithoutPhotos
            else -> dao.getAllProducts()
        }
    }
    val filteredProducts = MutableStateFlow<List<ProductData>>(emptyList())

    init {
        viewModelScope.launch {
            launch { allProducts.collect { filterProducts() } }
            launch { selectedCategory.collect { filterProducts() } }
            launch { selectedExpiredIn.collect { filterProducts() } }
            launch { selectedAdded.collect { filterProducts() } }
            launch { selectedAddedPhoto.collect { filterProducts() } }
            launch { selectedExpirationDate.collect { filterProducts() } }
            launch { productName.collect { filterProducts() } }
        }
    }
    private fun filterProducts() {
        val dateFormatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        val currentProducts = allProducts.value

        val filtered = currentProducts.filter { product ->
            val expirationDateStr = product.expiration_date?.let { dateFormatter.format(java.util.Date(it)) } ?: ""
            val addDayStr = product.add_day?.let { dateFormatter.format(java.util.Date(it)) } ?: ""

            (selectedCategory.value.isEmpty() || selectedCategory.value.contains(product.categories)) &&
                    (selectedExpiredIn.value.isEmpty() || selectedExpiredIn.value.contains(expirationDateStr)) &&
                    (selectedAdded.value.isEmpty() || selectedAdded.value.contains(addDayStr)) &&
                    (selectedAddedPhoto.value.isEmpty() || (
                            selectedAddedPhoto.value.contains("Added Photo") && product.image_url.isNotBlank() ||
                                    selectedAddedPhoto.value.contains("NO Photo") && product.image_url.isBlank()
                            )) &&
                    (selectedExpirationDate.value.isEmpty() || selectedExpirationDate.value.contains(expirationDateStr)) &&
                    (productName.value.isEmpty() || product.product_name.contains(productName.value, ignoreCase = true) || product.categories.contains(productName.value, ignoreCase = true))
        }

        filteredProducts.value = filtered
    }
    fun setSearchText(text: String) {
        productName.value = text
    }

}
