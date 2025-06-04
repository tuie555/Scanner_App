package com.LingTH.fridge.Setting.viewmodel

import com.LingTH.fridge.Setting.SettingsDao
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

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