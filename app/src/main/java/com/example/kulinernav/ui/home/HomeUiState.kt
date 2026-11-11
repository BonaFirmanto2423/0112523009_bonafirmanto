package com.example.kulinernav.ui.home

import com.example.kulinernav.data.Place
import com.example.kulinernav.data.settings.SortOrder

/**
 * Screen UI state untuk Home (immutable).
 * Semua yang perlu ditampilkan Home ada di sini; screen hanya merender.
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val query: String = "",
    val selectedCategory: String? = null,
    val categories: List<String> = emptyList(),
    val places: List<Place> = emptyList(),
    val totalCount: Int = 0,
    val sortOrder: SortOrder = SortOrder.NAME_ASC
) {
    /** Data berhasil dimuat tetapi hasil filter kosong. */
    val isEmpty: Boolean
        get() = !isLoading && errorMessage == null && places.isEmpty()

    val hasActiveFilter: Boolean
        get() = query.isNotBlank() || selectedCategory != null
}
