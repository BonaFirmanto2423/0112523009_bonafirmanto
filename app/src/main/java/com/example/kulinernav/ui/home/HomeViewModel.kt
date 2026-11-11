package com.example.kulinernav.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kulinernav.data.Place
import com.example.kulinernav.data.PlaceRepository
import com.example.kulinernav.data.settings.SettingsRepository
import com.example.kulinernav.data.settings.SortOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * State holder untuk Home.
 * Data dibaca dari Room lewat PlaceRepository (Flow), urutan dibaca dari DataStore
 * lewat SettingsRepository. ViewModel tidak pernah memanggil DAO secara langsung.
 */
class HomeViewModel(
    private val placeRepository: PlaceRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var allPlaces: List<Place> = emptyList()
    private var observeJob: Job? = null

    init {
        observePlaces()
    }

    // ---------------- Event dari UI ----------------

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query).applyFilter(allPlaces) }
    }

    /** Klik lagi kategori yang sama = batal pilih. */
    fun onCategorySelected(category: String?) {
        _uiState.update { state ->
            val newCategory = if (state.selectedCategory == category) null else category
            state.copy(selectedCategory = newCategory).applyFilter(allPlaces)
        }
    }

    fun onResetFilter() {
        _uiState.update { it.copy(query = "", selectedCategory = null).applyFilter(allPlaces) }
    }

    /** Mengubah urutan daftar; disimpan ke DataStore sehingga tetap sama setelah app dibuka lagi. */
    fun onSortOrderChange(order: SortOrder) {
        viewModelScope.launch { settingsRepository.setSortOrder(order) }
    }

    fun onRetry() {
        observePlaces()
    }

    // ---------------- Logic internal ----------------

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observePlaces() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            settingsRepository.settings
                .map { it.sortOrder }
                .distinctUntilChanged()
                // setiap urutan berubah, ganti ke query Room yang sesuai
                .flatMapLatest { order -> placeRepository.observePlaces(order).map { order to it } }
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Gagal memuat data: ${e.message}")
                    }
                }
                .collect { (order, places) ->
                    allPlaces = places
                    _uiState.update { it.copy(isLoading = false, sortOrder = order).applyFilter(places) }
                }
        }
    }

    private fun HomeUiState.applyFilter(source: List<Place>): HomeUiState {
        val categories = source.map { it.category }.distinct().sorted()
        val category = selectedCategory?.takeIf { it in categories }
        val keyword = query.trim()
        val result = source.filter { place ->
            val matchCategory = category == null || place.category == category
            val matchQuery = keyword.isEmpty() ||
                place.name.contains(keyword, ignoreCase = true) ||
                place.location.contains(keyword, ignoreCase = true)
            matchCategory && matchQuery
        }
        return copy(
            categories = categories,
            selectedCategory = category,
            places = result,
            totalCount = source.size
        )
    }
}
