package com.apzlocz.app

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apzlocz.app.ui.theme.DarkOneUiColors
import com.apzlocz.app.ui.theme.LightOneUiColors
import com.apzlocz.app.ui.theme.OneUiMotion
import com.apzlocz.app.ui.theme.OneUiShapes
import com.apzlocz.app.ui.theme.OneUiSpacing
import com.apzlocz.app.ui.theme.OneUiTypography
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests validating that the Design System tokens strictly adhere
 * to docs/04_DESIGN_SYSTEM.md.
 */
class OneUiThemeTest {

    @Test
    fun testLightColorTokens() {
        assertEquals(Color(0xFFF4F4F6), LightOneUiColors.background)
        assertEquals(Color(0xFFFFFFFF), LightOneUiColors.surface)
        assertEquals(Color(0xFF3E91FF), LightOneUiColors.accent)
        assertFalse(LightOneUiColors.isDark)
    }

    @Test
    fun testDarkColorTokens() {
        // True black for AMOLED panels per docs/04
        assertEquals(Color(0xFF000000), DarkOneUiColors.background)
        assertEquals(Color(0xFF1C1C1E), DarkOneUiColors.surface)
        assertEquals(Color(0xFF4C9CFF), DarkOneUiColors.accent)
        assertTrue(DarkOneUiColors.isDark)
    }

    @Test
    fun testTypographyTokens() {
        val type = OneUiTypography()
        assertEquals(34.sp, type.display.fontSize)
        assertEquals(28.sp, type.titleLarge.fontSize)
        assertEquals(20.sp, type.title.fontSize)
        assertEquals(16.sp, type.body.fontSize)
        assertEquals(14.sp, type.label.fontSize)
        assertEquals(12.sp, type.caption.fontSize)
    }

    @Test
    fun testMotionTokens() {
        assertEquals(120, OneUiMotion.DurationFast)
        assertEquals(220, OneUiMotion.DurationMedium)
        assertEquals(350, OneUiMotion.DurationSlow)
    }

    @Test
    fun testSpacingAndTouchTargets() {
        val spacing = OneUiSpacing()
        assertEquals(48.dp, spacing.minTouchTarget)
        assertEquals(72.dp, spacing.pinKeySize)
        assertEquals(16.dp, spacing.screenPadding)
    }
}

