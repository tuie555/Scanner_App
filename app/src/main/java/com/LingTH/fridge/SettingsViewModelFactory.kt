package ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import Databases.SettingsDao
import viewmodel.SettingsViewModel

class SettingsViewModelFactory(
    private val settingsDao: SettingsDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(settingsDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
