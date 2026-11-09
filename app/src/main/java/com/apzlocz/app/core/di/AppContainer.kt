package com.apzlocz.app.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.apzlocz.app.core.datastore.DataStoreSettingsRepository
import com.apzlocz.app.core.datastore.SettingsRepository
import com.apzlocz.app.core.security.AndroidKeyStoreCryptoEngine
import com.apzlocz.app.core.security.CredentialManager
import com.apzlocz.app.core.security.CryptoEngine
import com.apzlocz.app.core.security.DefaultCredentialManager
import com.apzlocz.app.core.security.DefaultLockoutManager
import com.apzlocz.app.core.security.LockoutManager

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "apz_locz_preferences")

interface AppContainer {
    val settingsRepository: SettingsRepository
    val cryptoEngine: CryptoEngine
    val credentialManager: CredentialManager
    val lockoutManager: LockoutManager
}

class DefaultAppContainer(
    private val context: Context
) : AppContainer {

    override val settingsRepository: SettingsRepository by lazy {
        DataStoreSettingsRepository(context.dataStore)
    }

    override val cryptoEngine: CryptoEngine by lazy {
        AndroidKeyStoreCryptoEngine()
    }

    override val credentialManager: CredentialManager by lazy {
        DefaultCredentialManager(
            settingsRepository = settingsRepository,
            cryptoEngine = cryptoEngine
        )
    }

    override val lockoutManager: LockoutManager by lazy {
        DefaultLockoutManager(
            settingsRepository = settingsRepository
        )
    }
}

