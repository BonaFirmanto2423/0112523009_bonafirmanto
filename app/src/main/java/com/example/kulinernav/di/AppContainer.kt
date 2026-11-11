package com.example.kulinernav.di

import android.content.Context
import com.example.kulinernav.data.PlaceRepository
import com.example.kulinernav.data.local.KulinerDatabase
import com.example.kulinernav.data.settings.SettingsRepository

/**
 * Dependency injection manual: membuat Database dan Repository satu kali
 * untuk seluruh aplikasi. Tidak ada instance database yang dibuat di Composable.
 */
class AppContainer(context: Context) {

    private val database: KulinerDatabase = KulinerDatabase.getInstance(context)

    val placeRepository: PlaceRepository = PlaceRepository(database.placeDao())

    val settingsRepository: SettingsRepository = SettingsRepository(context.applicationContext)
}
