package com.example.kulinernav.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.kulinernav.KulinerApp
import com.example.kulinernav.ui.detail.DetailViewModel
import com.example.kulinernav.ui.form.PlaceFormViewModel
import com.example.kulinernav.ui.home.HomeViewModel
import com.example.kulinernav.ui.settings.SettingsViewModel

/**
 * Factory untuk semua ViewModel. Repository diambil dari AppContainer,
 * sehingga ViewModel menerima Repository (bukan DAO) lewat constructor.
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(kulinerApp().container.placeRepository, kulinerApp().container.settingsRepository)
        }
        initializer {
            DetailViewModel(createSavedStateHandle(), kulinerApp().container.placeRepository)
        }
        initializer {
            PlaceFormViewModel(createSavedStateHandle(), kulinerApp().container.placeRepository)
        }
        initializer {
            SettingsViewModel(kulinerApp().container.settingsRepository)
        }
    }
}

/** Mengambil instance KulinerApp dari CreationExtras. */
fun CreationExtras.kulinerApp(): KulinerApp =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KulinerApp
