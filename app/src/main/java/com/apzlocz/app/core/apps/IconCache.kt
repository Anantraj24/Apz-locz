package com.apzlocz.app.core.apps

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * In-memory LRU cache for application icons to avoid reading from PackageManager
 * repeatedly during list rendering and scrolling.
 *
 * Adheres strictly to the architectural constraint: no Coil or Glide.
 */
class IconCache(
    maxEntries: Int = 150
) {
    companion object {
        private const val TAG = "IconCache"
        private const val ICON_SIZE_PX = 144
    }

    private val cache = object : LruCache<String, Bitmap>(maxEntries) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return 1
        }
    }

    fun get(packageName: String): Bitmap? {
        return cache.get(packageName)
    }

    fun put(packageName: String, bitmap: Bitmap) {
        cache.put(packageName, bitmap)
    }

    fun clear() {
        cache.evictAll()
    }

    /**
     * Loads an application icon on the IO dispatcher, converts it to a standard
     * sized Bitmap, and caches it in memory.
     */
    suspend fun loadIcon(
        packageName: String,
        packageManager: PackageManager
    ): Bitmap? = withContext(Dispatchers.IO) {
        get(packageName)?.let { return@withContext it }

        try {
            val drawable = packageManager.getApplicationIcon(packageName)
            val bitmap = drawableToBitmap(drawable)
            put(packageName, bitmap)
            bitmap
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load icon for $packageName: ${e.message}")
            null
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            val bmp = drawable.bitmap
            if (bmp.width == ICON_SIZE_PX && bmp.height == ICON_SIZE_PX) {
                return bmp
            }
        }

        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else ICON_SIZE_PX
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else ICON_SIZE_PX

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}

