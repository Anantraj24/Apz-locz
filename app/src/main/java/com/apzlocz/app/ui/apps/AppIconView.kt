package com.apzlocz.app.ui.apps

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apzlocz.app.core.apps.IconCache
import com.apzlocz.app.ui.theme.OneUiTheme

@Composable
fun AppIconView(
    packageName: String,
    appName: String,
    iconCache: IconCache,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var bitmap by remember(packageName) {
        mutableStateOf<Bitmap?>(iconCache.get(packageName))
    }

    LaunchedEffect(packageName) {
        if (bitmap == null) {
            bitmap = iconCache.loadIcon(packageName, context.packageManager)
        }
    }

    val iconShape = RoundedCornerShape(12.dp)

    val currentBmp = bitmap
    if (currentBmp != null) {
        Image(
            bitmap = currentBmp.asImageBitmap(),
            contentDescription = "$appName icon",
            modifier = modifier
                .size(48.dp)
                .clip(iconShape)
        )
    } else {
        // Fallback placeholder with initial letter
        Box(
            modifier = modifier
                .size(48.dp)
                .clip(iconShape)
                .background(OneUiTheme.colors.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            val initial = appName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
            Text(
                text = initial,
                color = OneUiTheme.colors.accent,
                style = OneUiTheme.typography.body.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
        }
    }
}
