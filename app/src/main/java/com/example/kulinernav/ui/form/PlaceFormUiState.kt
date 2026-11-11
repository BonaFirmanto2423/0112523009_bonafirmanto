package com.example.kulinernav.ui.form

/** Screen UI state untuk Form tambah/edit (immutable). */
data class PlaceFormUiState(
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isNotFound: Boolean = false,
    val name: String = "",
    val category: String = "",
    val location: String = "",
    val ratingText: String = "",
    val description: String = "",
    val showErrors: Boolean = false,
    val nameError: String? = null,
    val categoryError: String? = null,
    val locationError: String? = null,
    val ratingError: String? = null,
    val descriptionError: String? = null,
    /** Diisi setelah berhasil disimpan; Route memakai nilai ini untuk navigasi. */
    val savedPlaceId: Int? = null
)
