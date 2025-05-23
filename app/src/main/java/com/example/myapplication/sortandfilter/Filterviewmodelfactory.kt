package com.example.myapplication.sortandfilter

import Databases.ProductDao
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FilterViewModelFactory(
    private val dao: ProductDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FilterViewModel(dao) as T
    }
}
