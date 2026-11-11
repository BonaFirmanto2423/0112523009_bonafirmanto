package com.example.kulinernav.ui.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kulinernav.ui.AppViewModelProvider

@Composable
fun PlaceFormRoute(
    onCancel: () -> Unit,
    onSaved: (placeId: Int, isEditMode: Boolean) -> Unit,
    viewModel: PlaceFormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Event satu kali: setelah tersimpan, serahkan navigasi ke NavHost
    LaunchedEffect(uiState.savedPlaceId) {
        uiState.savedPlaceId?.let { id ->
            onSaved(id, uiState.isEditMode)
            viewModel.onSaveHandled()
        }
    }

    PlaceFormScreen(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onCategoryChange = viewModel::onCategoryChange,
        onLocationChange = viewModel::onLocationChange,
        onRatingChange = viewModel::onRatingChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onSave = viewModel::onSave,
        onReset = viewModel::onReset,
        onCancel = onCancel
    )
}
