package com.example.kulinernav

import android.app.Application
import com.example.kulinernav.di.AppContainer

/** Application class: tempat AppContainer (Database + Repository) dibuat. */
class KulinerApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
