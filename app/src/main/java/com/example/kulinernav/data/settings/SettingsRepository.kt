package com.example.kulinernav.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/** Satu file DataStore "settings" untuk seluruh aplikasi. */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/** Pilihan urutan daftar; key disimpan sebagai String di DataStore. */
enum class SortOrder(val key: String, val label: String) {
    NAME_ASC("name_asc", "Nama A–Z"),
    RATING_DESC("rating_desc", "Rating tertinggi"),
    NEWEST("newest", "Terbaru ditambahkan");

    companion object {
        fun fromKey(key: String?): SortOrder = entries.firstOrNull { it.key == key } ?: NAME_ASC
    }
}

/** Kumpulan preference pengguna yang dibaca dari DataStore. */
data class UserSettings(
    val darkMode: Boolean = false,
    val sortOrder: SortOrder = SortOrder.NAME_ASC
)

/**
 * Menyimpan 2 preference dengan Preferences DataStore:
 * 1) dark_mode  (Boolean) -> tema gelap/terang
 * 2) sort_order (String)  -> urutan daftar di Home
 */
class SettingsRepository(private val context: Context) {

    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val SORT_ORDER = stringPreferencesKey("sort_order")
    }

    val settings: Flow<UserSettings> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            UserSettings(
                darkMode = prefs[Keys.DARK_MODE] ?: false,
                sortOrder = SortOrder.fromKey(prefs[Keys.SORT_ORDER])
            )
        }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DARK_MODE] = enabled }
    }

    suspend fun setSortOrder(order: SortOrder) {
        context.dataStore.edit { it[Keys.SORT_ORDER] = order.key }
    }
}
