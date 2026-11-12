package com.apzlocz.app.admin

import android.app.admin.DeviceAdminReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import com.apzlocz.app.BuildConfig

/**
 * DeviceAdminReceiver enabling uninstall protection per docs/00_PROJECT_OVERVIEW.md FR-07.
 * When active, Android prevents standard uninstallation until deactivated.
 */
class ApzAdminReceiver : DeviceAdminReceiver() {

    companion object {
        private const val TAG = "ApzAdminReceiver"

        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context, ApzAdminReceiver::class.java)
        }
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Device Admin enabled for Apz Locz uninstall protection.")
        }
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Device Admin disabled for Apz Locz.")
        }
    }
}
