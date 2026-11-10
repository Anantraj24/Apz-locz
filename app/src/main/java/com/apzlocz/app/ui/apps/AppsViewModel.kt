package com.apzlocz.app.ui.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apzlocz.app.core.apps.AppInfo
import com.apzlocz.app.core.apps.InstalledAppsRepository
import com.apzlocz.app.core.datastore.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppsTab {
    ALL,
    LOCKED
}

data class AppsUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedTab: AppsTab = AppsTab.ALL,
    val showSystemApps: Boolean = false,
    val apps: List<AppInfo> = emptyList(),
    val totalAppsCount: Int = 0,
    val lockedAppsCount: Int = 0,
    val errorMessage: String? = null
)

class AppsViewModel(
    private val installedAppsRepository: InstalledAppsRepository,
    private val settingsRepository: SettingsRepository,
    externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope = externalScope ?: viewModelScope

    private val searchQueryFlow = MutableStateFlow("")
    private val selectedTabFlow = MutableStateFlow(AppsTab.ALL)
    private val isLoadingFlow = MutableStateFlow(true)
    private val errorFlow = MutableStateFlow<String?>(null)

    val uiState: StateFlow<AppsUiState> = combine(
        installedAppsRepository.installedAppsFlow,
        settingsRepository.settingsFlow,
        searchQueryFlow,
        selectedTabFlow,
        isLoadingFlow
    ) { allApps: List<AppInfo>, settings, query: String, tab: AppsTab, loading: Boolean ->
        val totalCount = allApps.size
        val lockedCount = allApps.count { it.isLocked }

        val filteredByTab = when (tab) {
            AppsTab.ALL -> allApps
            AppsTab.LOCKED -> allApps.filter { it.isLocked }
        }

        val filteredByQuery = if (query.isBlank()) {
            filteredByTab
        } else {
            val trimmed = query.trim()
            filteredByTab.filter { app ->
                app.appName.contains(trimmed, ignoreCase = true) ||
                    app.packageName.contains(trimmed, ignoreCase = true)
            }
        }

        AppsUiState(
            isLoading = loading && allApps.isEmpty(),
            searchQuery = query,
            selectedTab = tab,
            showSystemApps = settings.showSystemApps,
            apps = filteredByQuery,
            totalAppsCount = totalCount,
            lockedAppsCount = lockedCount,
            errorMessage = errorFlow.value
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppsUiState(isLoading = true)
    )

    init {
        refresh()
    }

    fun refresh() {
        scope.launch {
            try {
                isLoadingFlow.value = true
                errorFlow.value = null
                installedAppsRepository.refresh()
            } catch (e: Exception) {
                errorFlow.value = e.message ?: "Failed to scan installed apps"
            } finally {
                isLoadingFlow.value = false
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQueryFlow.value = newQuery
    }

    fun onTabSelected(tab: AppsTab) {
        selectedTabFlow.value = tab
    }

    fun onToggleLock(packageName: String) {
        scope.launch {
            try {
                installedAppsRepository.toggleLock(packageName)
            } catch (e: Exception) {
                errorFlow.value = "Failed to update lock state: ${e.message}"
            }
        }
    }

    fun onToggleShowSystemApps(show: Boolean) {
        scope.launch {
            installedAppsRepository.setShowSystemApps(show)
        }
    }

    class Factory(
        private val installedAppsRepository: InstalledAppsRepository,
        private val settingsRepository: SettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AppsViewModel(installedAppsRepository, settingsRepository) as T
        }
    }
}
