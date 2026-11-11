package com.apzlocz.app.core.apps

import com.apzlocz.app.core.datastore.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

/**
 * Repository providing the list of installed applications combined with
 * the persistent lock state and system app visibility preference.
 */
interface InstalledAppsRepository {
    val installedAppsFlow: Flow<List<AppInfo>>
    suspend fun refresh()
    suspend fun toggleLock(packageName: String): Boolean
    suspend fun setShowSystemApps(show: Boolean)
}

class DefaultInstalledAppsRepository(
    private val appScanner: AppScanner,
    private val settingsRepository: SettingsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : InstalledAppsRepository {

    private val rawAppsFlow = MutableStateFlow<List<RawAppInfo>>(emptyList())

    override val installedAppsFlow: Flow<List<AppInfo>> = combine(
        rawAppsFlow,
        settingsRepository.settingsFlow
    ) { rawApps, settings ->
        val lockedSet = settings.lockedPackages
        val showSystem = settings.showSystemApps

        rawApps
            .filter { raw ->
                if (!showSystem && raw.isSystemApp && !lockedSet.contains(raw.packageName)) {
                    false
                } else {
                    true
                }
            }
            .map { raw ->
                AppInfo(
                    packageName = raw.packageName,
                    appName = raw.appName,
                    isSystemApp = raw.isSystemApp,
                    isLocked = lockedSet.contains(raw.packageName)
                )
            }
            .sortedBy { it.appName.lowercase() }
    }

    override suspend fun refresh() = withContext(ioDispatcher) {
        val apps = appScanner.scanInstalledApps()
        rawAppsFlow.value = apps
    }

    override suspend fun toggleLock(packageName: String): Boolean {
        return settingsRepository.togglePackageLocked(packageName)
    }

    override suspend fun setShowSystemApps(show: Boolean) {
        settingsRepository.setShowSystemApps(show)
    }
}

