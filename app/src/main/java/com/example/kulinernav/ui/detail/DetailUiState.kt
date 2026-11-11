package com.example.kulinernav.ui.detail

import com.example.kulinernav.data.Place

/** Screen UI state untuk Detail (immutable). */
data class DetailUiState(
    val placeId: Int,
    val isLoading: Boolean = true,
    val place: Place? = null,
    val showDeleteDialog: Boolean = false,
    val isDeleted: Boolean = false
) {
    /** Data sudah dibaca dari Room tetapi id tidak ada (misalnya Detail(999)). */
    val isNotFound: Boolean
        get() = !isLoading && place == null && !isDeleted
}
