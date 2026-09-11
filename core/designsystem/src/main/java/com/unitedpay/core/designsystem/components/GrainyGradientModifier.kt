package com.unitedpay.core.designsystem.components

import android.graphics.Bitmap
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.unitedpay.core.designsystem.theme.UnitedAtmosphericGrainyGradient
import kotlin.random.Random

/**
 * Creates an ultra-lightweight, tileable procedural grain noise bitmap.
 * The noise is pre-rendered once and cached in memory to ensure 60fps smooth scrolling.
 */
private object GrainNoiseCache {
    private var cachedImageBitmap: androidx.compose.ui.graphics.ImageBitmap? = null

    fun getOrCreateGrain(size: Int = 128, density: Float = 0.35f): androidx.compose.ui.graphics.ImageBitmap {
        cachedImageBitmap?.let { return it }

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(size * size)
        val rng = Random(42) // Fixed seed for stable, flicker-free texture

        for (i in pixels.indices) {
            if (rng.nextFloat() < density) {
                // Subtle white and dark micro-dots with low alpha (4% - 8%)
                val isLight = rng.nextBoolean()
                val alpha = rng.nextInt(8, 22) // 3% to 8% opacity
                val c = if (isLight) 255 else 0
                pixels[i] = (alpha shl 24) or (c shl 16) or (c shl 8) or c
            } else {
                pixels[i] = 0 // Transparent
            }
        }

        bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
        val imageBitmap = bitmap.asImageBitmap()
        cachedImageBitmap = imageBitmap
        return imageBitmap
    }
}

/**
 * Modifier that renders the reference image's atmospheric grainy royal-blue-to-white gradient:
 * - Flows continuously from status bar through header and down below the Quick Actions card
 * - Overlaid with procedural frosted micro-grain texture for true iOS acrylic depth
 */
fun Modifier.grainyGradientBackground(
    gradientBrush: Brush = UnitedAtmosphericGrainyGradient,
    enableGrain: Boolean = true
): Modifier = composed {
    val grainBitmap = remember { if (enableGrain) GrainNoiseCache.getOrCreateGrain() else null }

    this.drawBehind {
        // 1. Draw the primary atmospheric gradient
        drawRect(brush = gradientBrush)

        // 2. Tile the procedural frosted grain texture seamlessly across the canvas
        grainBitmap?.let { grain ->
            val tileW = grain.width
            val tileH = grain.height
            val canvasW = size.width.toInt()
            val canvasH = size.height.toInt()

            var y = 0
            while (y < canvasH) {
                var x = 0
                while (x < canvasW) {
                    val w = minOf(tileW, canvasW - x)
                    val h = minOf(tileH, canvasH - y)
                    drawImage(
                        image = grain,
                        srcOffset = IntOffset.Zero,
                        srcSize = IntSize(w, h),
                        dstOffset = IntOffset(x, y),
                        dstSize = IntSize(w, h),
                        alpha = 0.65f
                    )
                    x += tileW
                }
                y += tileH
            }
        }
    }
}
