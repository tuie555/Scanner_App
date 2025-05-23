package Databases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddViewModelFactory(private val productDao: ProductDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(Addviewmodel::class.java)) {
            return Addviewmodel(productDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
