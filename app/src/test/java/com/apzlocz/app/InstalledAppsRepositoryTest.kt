package com.apzlocz.app

import com.apzlocz.app.core.apps.AppScanner
import com.apzlocz.app.core.apps.DefaultInstalledAppsRepository
import com.apzlocz.app.core.apps.RawAppInfo
import com.apzlocz.app.ui.apps.AppsTab
import com.apzlocz.app.ui.apps.AppsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InstalledAppsRepositoryTest {

    private class FakeAppScanner(
        val apps: MutableList<RawAppInfo> = mutableListOf()
    ) : AppScanner {
        override suspend fun scanInstalledApps(): List<RawAppInfo> = apps
    }

    private lateinit var fakeScanner: FakeAppScanner
    private lateinit var fakeSettingsRepo: FakeSettingsRepository
    private lateinit var repository: DefaultInstalledAppsRepository

    @Before
    fun setUp() {
        fakeScanner = FakeAppScanner(
            mutableListOf(
                RawAppInfo("com.whatsapp", "WhatsApp", isSystemApp = false),
                RawAppInfo("com.android.settings", "Settings", isSystemApp = true),
                RawAppInfo("com.instagram.android", "Instagram", isSystemApp = false),
                RawAppInfo("com.sec.android.gallery3d", "Gallery", isSystemApp = true)
            )
        )
        fakeSettingsRepo = FakeSettingsRepository()
        repository = DefaultInstalledAppsRepository(
            appScanner = fakeScanner,
            settingsRepository = fakeSettingsRepo,
            ioDispatcher = Dispatchers.Unconfined
        )
    }

    @Test
    fun testDefaultStateFiltersSystemAppsUnlessLocked() = runTest {
        repository.refresh()

        val apps = repository.installedAppsFlow.first()
        // Only WhatsApp and Instagram should be visible when showSystemApps is false and none are locked
        assertEquals(2, apps.size)
        assertEquals("Instagram", apps[0].appName)
        assertEquals("WhatsApp", apps[1].appName)
        assertFalse(apps[0].isLocked)
        assertFalse(apps[1].isLocked)
    }

    @Test
    fun testLockedSystemAppIsVisibleEvenWhenShowSystemAppsIsFalse() = runTest {
        fakeSettingsRepo.setLockedPackages(setOf("com.sec.android.gallery3d"))
        repository.refresh()

        val apps = repository.installedAppsFlow.first()
        assertEquals(3, apps.size)
        assertTrue(apps.any { it.packageName == "com.sec.android.gallery3d" && it.isLocked })
    }

    @Test
    fun testEnablingShowSystemAppsIncludesAllSystemApps() = runTest {
        repository.refresh()
        fakeSettingsRepo.setShowSystemApps(true)

        val apps = repository.installedAppsFlow.first()
        assertEquals(4, apps.size)
        assertTrue(apps.any { it.packageName == "com.android.settings" })
        assertTrue(apps.any { it.packageName == "com.sec.android.gallery3d" })
    }

    @Test
    fun testToggleLockUpdatesLockedStateInRepository() = runTest {
        repository.refresh()

        val isLockedNow = repository.toggleLock("com.whatsapp")
        assertTrue(isLockedNow)

        val apps = repository.installedAppsFlow.first()
        val whatsapp = apps.first { it.packageName == "com.whatsapp" }
        assertTrue(whatsapp.isLocked)

        val unlockedNow = repository.toggleLock("com.whatsapp")
        assertFalse(unlockedNow)

        val updatedApps = repository.installedAppsFlow.first()
        assertFalse(updatedApps.first { it.packageName == "com.whatsapp" }.isLocked)
    }

    @Test
    fun testViewModelFiltersByTabAndSearchQuery() = runTest {
        repository.refresh()
        fakeSettingsRepo.setLockedPackages(setOf("com.whatsapp"))

        val vmScope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        try {
            val viewModel = AppsViewModel(
                installedAppsRepository = repository,
                settingsRepository = fakeSettingsRepo,
                externalScope = vmScope
            )

            // Tab: ALL
            val stateAll = viewModel.uiState.first { !it.isLoading }
            assertEquals(2, stateAll.apps.size)
            assertEquals(1, stateAll.lockedAppsCount)

            // Tab: LOCKED
            viewModel.onTabSelected(AppsTab.LOCKED)
            val stateLocked = viewModel.uiState.first { it.selectedTab == AppsTab.LOCKED }
            assertEquals(1, stateLocked.apps.size)
            assertEquals("com.whatsapp", stateLocked.apps[0].packageName)

            // Search: "insta"
            viewModel.onTabSelected(AppsTab.ALL)
            viewModel.onSearchQueryChange("insta")
            val stateSearch = viewModel.uiState.first { it.searchQuery == "insta" }
            assertEquals(1, stateSearch.apps.size)
            assertEquals("Instagram", stateSearch.apps[0].appName)
        } finally {
            vmScope.cancel()
        }
    }
}
