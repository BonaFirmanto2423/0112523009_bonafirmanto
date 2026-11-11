package com.example.kulinernav.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kulinernav.ui.AppViewModelProvider

/**
 * Route = penghubung ViewModel dengan Screen.
 * Mengumpulkan state secara lifecycle-aware, lalu meneruskan
 * UiState + callback event ke HomeScreen.
 */
@Composable
fun HomeRoute(
    onPlaceClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onTestInvalidId: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onCategorySelected = viewModel::onCategorySelected,
        onResetFilter = viewModel::onResetFilter,
        onSortOrderChange = viewModel::onSortOrderChange,
        onRetry = viewModel::onRetry,
        onPlaceClick = onPlaceClick,
        onAddClick = onAddClick,
        onSettingsClick = onSettingsClick,
        onTestInvalidId = onTestInvalidId
    )
}
