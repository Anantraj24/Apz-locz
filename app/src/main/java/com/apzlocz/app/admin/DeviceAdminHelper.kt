package com.apzlocz.app.admin

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent

object DeviceAdminHelper {

    fun isDeviceAdminActive(context: Context): Boolean {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager ?: return false
        val adminComponent = ApzAdminReceiver.getComponentName(context)
        return dpm.isAdminActive(adminComponent)
    }

    fun createEnableAdminIntent(context: Context): Intent {
        val adminComponent = ApzAdminReceiver.getComponentName(context)
        return Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
            putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Activate Device Admin to prevent Apz Locz from being uninstalled without entering your security credential."
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    fun deactivateDeviceAdmin(context: Context): Boolean {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager ?: return false
        val adminComponent = ApzAdminReceiver.getComponentName(context)
        return if (dpm.isAdminActive(adminComponent)) {
            dpm.removeActiveAdmin(adminComponent)
            true
        } else {
            false
        }
    }
}
