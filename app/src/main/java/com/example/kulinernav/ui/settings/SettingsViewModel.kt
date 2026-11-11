package com.example.kulinernav.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kulinernav.data.settings.SettingsRepository
import com.example.kulinernav.data.settings.SortOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Screen UI state untuk halaman Pengaturan. */
data class SettingsUiState(
    val isLoading: Boolean = true,
    val darkMode: Boolean = false,
    val sortOrder: SortOrder = SortOrder.NAME_ASC
)

/**
 * Membaca dan mengubah preference di DataStore lewat SettingsRepository.
 * Nilai yang tampil selalu berasal dari DataStore, jadi tetap sama setelah app dibuka ulang.
 */
class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _uiState.update {
                    it.copy(isLoading = false, darkMode = settings.darkMode, sortOrder = settings.sortOrder)
                }
            }
        }
    }

    fun onDarkModeChange(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setDarkMode(enabled) }
    }

    fun onSortOrderChange(order: SortOrder) {
        viewModelScope.launch { settingsRepository.setSortOrder(order) }
    }
}
