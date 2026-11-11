package com.apzlocz.app.core.apps

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build

/**
 * Raw data extracted from PackageManager before applying user preferences and lock states.
 */
data class RawAppInfo(
    val packageName: String,
    val appName: String,
    val isSystemApp: Boolean
)

/**
 * Abstraction for scanning installed applications.
 * Enables clean unit testing on standard JVM environments.
 */
interface AppScanner {
    suspend fun scanInstalledApps(): List<RawAppInfo>
}

/**
 * Android implementation that queries launcher activities via Intent filters.
 * Complies with Android 11+ package visibility (<queries>) without requiring QUERY_ALL_PACKAGES.
 */
class PackageManagerAppScanner(
    private val context: Context
) : AppScanner {

    override suspend fun scanInstalledApps(): List<RawAppInfo> {
        val packageManager = context.packageManager
        val ownPackageName = context.packageName

        val mainIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(
                mainIntent,
                PackageManager.ResolveInfoFlags.of(0L)
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentActivities(mainIntent, 0)
        }

        return resolveInfoList
            .mapNotNull { resolveInfo ->
                val pkgName = resolveInfo.activityInfo?.packageName ?: return@mapNotNull null
                if (pkgName == ownPackageName) return@mapNotNull null

                val appLabel = try {
                    resolveInfo.loadLabel(packageManager).toString()
                } catch (e: Exception) {
                    pkgName
                }

                val isSystem = try {
                    val appInfo = resolveInfo.activityInfo.applicationInfo
                    (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                } catch (e: Exception) {
                    false
                }

                RawAppInfo(
                    packageName = pkgName,
                    appName = appLabel,
                    isSystemApp = isSystem
                )
            }
            .distinctBy { it.packageName }
    }
}

