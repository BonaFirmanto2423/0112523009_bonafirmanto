package com.example.kulinernav.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kulinernav.ui.AppViewModelProvider

@Composable
fun DetailRoute(
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    onDeleted: () -> Unit,
    viewModel: DetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Setelah data dihapus, minta NavHost kembali ke Home (navigasi tetap di luar ViewModel)
    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) onDeleted()
    }

    DetailScreen(
        uiState = uiState,
        onBack = onBack,
        onEdit = onEdit,
        onDeleteClick = viewModel::onDeleteClick,
        onDismissDeleteDialog = viewModel::onDismissDeleteDialog,
        onConfirmDelete = viewModel::onConfirmDelete
    )
}
