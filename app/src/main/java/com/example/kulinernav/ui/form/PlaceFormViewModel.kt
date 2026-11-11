package com.example.kulinernav.ui.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.kulinernav.data.PlaceRepository
import com.example.kulinernav.navigation.NEW_PLACE_ID
import com.example.kulinernav.navigation.PlaceForm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * State holder untuk Form tambah/edit.
 * Data lama dibaca dari Room (getById) dan disimpan kembali lewat Repository (insert/update).
 */
class PlaceFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val placeId: Int = savedStateHandle.toRoute<PlaceForm>().id
    private val isEditMode = placeId != NEW_PLACE_ID

    /** Isi awal form, dipakai oleh tombol Reset. */
    private var initialState = PlaceFormUiState(isEditMode = isEditMode, isLoading = isEditMode)

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<PlaceFormUiState> = _uiState.asStateFlow()

    init {
        if (isEditMode) loadPlace()
    }

    // ---------------- Event update form ----------------

    fun onNameChange(value: String) = updateField { it.copy(name = value) }
    fun onCategoryChange(value: String) = updateField { it.copy(category = value) }
    fun onLocationChange(value: String) = updateField { it.copy(location = value) }
    fun onRatingChange(value: String) = updateField { it.copy(ratingText = value) }
    fun onDescriptionChange(value: String) = updateField { it.copy(description = value) }

    fun onReset() {
        _uiState.value = initialState
    }

    /** Validasi, lalu insert (tambah) atau update (edit) ke Room lewat Repository. */
    fun onSave() {
        val validated = _uiState.value.copy(showErrors = true).validated()
        _uiState.value = validated
        if (!validated.isValid() || validated.isSaving) return

        val rating = validated.ratingText.replace(',', '.').toDouble()
        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val savedId = if (isEditMode) {
                placeRepository.getPlace(placeId)?.let { old ->
                    placeRepository.updatePlace(
                        old.copy(
                            name = validated.name.trim(),
                            category = validated.category.trim(),
                            location = validated.location.trim(),
                            rating = rating,
                            description = validated.description.trim()
                        )
                    )
                }
                placeId
            } else {
                placeRepository.addPlace(
                    validated.name.trim(), validated.category.trim(), validated.location.trim(),
                    rating, validated.description.trim()
                )
            }
            _uiState.update { it.copy(isSaving = false, savedPlaceId = savedId) }
        }
    }

    /** Dipanggil Route setelah navigasi dilakukan, agar event tidak terulang. */
    fun onSaveHandled() {
        _uiState.update { it.copy(savedPlaceId = null) }
    }

    // ---------------- Logic internal ----------------

    private fun loadPlace() {
        viewModelScope.launch {
            val place = placeRepository.getPlace(placeId)
            initialState = if (place == null) {
                PlaceFormUiState(isEditMode = true, isNotFound = true)
            } else {
                PlaceFormUiState(
                    isEditMode = true,
                    name = place.name,
                    category = place.category,
                    location = place.location,
                    ratingText = place.rating.toString(),
                    description = place.description
                )
            }
            _uiState.value = initialState
        }
    }

    private fun updateField(change: (PlaceFormUiState) -> PlaceFormUiState) {
        _uiState.update { change(it).validated() }
    }

    /** Hitung pesan error; error wajib isi baru ditampilkan setelah tombol Simpan ditekan. */
    private fun PlaceFormUiState.validated(): PlaceFormUiState {
        val rating = ratingText.replace(',', '.').toDoubleOrNull()
        val descError = if (description.length > 200) "Deskripsi maksimal 200 karakter" else null
        if (!showErrors) {
            return copy(
                nameError = null, categoryError = null, locationError = null,
                ratingError = null, descriptionError = descError
            )
        }
        return copy(
            nameError = when {
                name.isBlank() -> "Nama tempat wajib diisi"
                name.trim().length < 3 -> "Nama minimal 3 karakter"
                else -> null
            },
            categoryError = if (category.isBlank()) "Kategori wajib diisi" else null,
            locationError = if (location.isBlank()) "Lokasi wajib diisi" else null,
            ratingError = when {
                ratingText.isBlank() -> "Rating wajib diisi"
                rating == null -> "Rating harus berupa angka"
                rating !in 1.0..5.0 -> "Rating harus antara 1.0 dan 5.0"
                else -> null
            },
            descriptionError = descError
        )
    }

    private fun PlaceFormUiState.isValid(): Boolean =
        listOf(nameError, categoryError, locationError, ratingError, descriptionError).all { it == null }
}
