package com.apzlocz.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.apzlocz.app.BuildConfig

/**
 * Dynamically registered BroadcastReceiver for Intent.ACTION_SCREEN_OFF.
 * Clears in-memory unlock state per docs/02_SRS.md §4 (ON_PHONE_LOCK relock mode).
 * Zero background polling or timers.
 */
class ScreenOffReceiver(
    private val onScreenOffCallback: () -> Unit
) : BroadcastReceiver() {

    companion object {
        private const val TAG = "ScreenOffReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_SCREEN_OFF) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Screen off event detected. Resetting all in-memory unlock states.")
            }
            onScreenOffCallback()
        }
    }
}

