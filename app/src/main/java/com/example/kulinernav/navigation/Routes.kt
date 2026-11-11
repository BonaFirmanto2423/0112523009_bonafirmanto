package com.example.kulinernav.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe routes untuk Navigation Compose.
 * Argument dibaca di NavHost dengan backStackEntry.toRoute<T>().
 */

/** Halaman daftar tempat kuliner. */
@Serializable
object Home

/** Halaman detail, menerima id tempat. */
@Serializable
data class Detail(val id: Int)

/**
 * Halaman form tambah/edit.
 * id = NEW_PLACE_ID (-1) berarti mode tambah; selain itu mode edit.
 */
@Serializable
data class PlaceForm(val id: Int = NEW_PLACE_ID)

/** Halaman pengaturan (preference DataStore). */
@Serializable
object Settings

const val NEW_PLACE_ID = -1
