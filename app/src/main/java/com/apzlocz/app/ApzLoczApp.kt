package com.apzlocz.app

import android.app.Application
import com.apzlocz.app.core.di.AppContainer
import com.apzlocz.app.core.di.DefaultAppContainer

/**
 * Main application class for Apz Locz.
 * Hosts manual Dependency Injection via [AppContainer].
 */
class ApzLoczApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
