package com.example.kulinernav.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.kulinernav.data.PlaceRepository
import com.example.kulinernav.navigation.Detail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * State holder untuk Detail.
 * Argument id dibaca dari SavedStateHandle, data dibaca dari Room lewat Repository (Flow).
 */
class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val placeId: Int = savedStateHandle.toRoute<Detail>().id

    private val _uiState = MutableStateFlow(DetailUiState(placeId = placeId))
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            placeRepository.observePlace(placeId).collect { place ->
                _uiState.update { it.copy(isLoading = false, place = place) }
            }
        }
    }

    fun onDeleteClick() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    /** Hapus dari Room lewat Repository, lalu tandai agar Route kembali ke Home. */
    fun onConfirmDelete() {
        viewModelScope.launch {
            _uiState.update { it.copy(showDeleteDialog = false, isDeleted = true) }
            placeRepository.deletePlace(placeId)
        }
    }
}
