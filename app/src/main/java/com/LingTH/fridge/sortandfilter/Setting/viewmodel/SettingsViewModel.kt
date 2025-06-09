package com.LingTH.fridge.sortandfilter.Setting.viewmodel

import com.LingTH.fridge.sortandfilter.Setting.Settings
import com.LingTH.fridge.sortandfilter.Setting.SettingsDao
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val dao: SettingsDao) : ViewModel() {

    private val _settings = MutableStateFlow<Settings?>(null)
    val settings: StateFlow<Settings?> get() = _settings

    init {
        viewModelScope.launch {
            _settings.value = dao.getSettings()
        }
    }

    fun saveSettings(data: Settings) {
        viewModelScope.launch {
            dao.insertOrUpdate(data)
            _settings.value = data
        }
    }
}