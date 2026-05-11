package dev.maxine.librelune.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import dev.maxine.librelune.data.Hemisphere
import kotlin.math.cos

object MoonGraphicsBitmapFactory {
    fun render(
        phaseFraction: Double,
        hemisphere: Hemisphere,
        sizePx: Int,
        wobbleDeg: Float = 0f,
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val pad = 0.5f
        val radius = (sizePx / 2f) - pad
        val cx = sizePx / 2f
        val cy = sizePx / 2f
        val circle = RectF(cx - radius, cy - radius, cx + radius, cy + radius)

        val darkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = Color.argb(255, 12, 18, 33)
        }
        val litPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = Color.argb(255, 243, 247, 255)
        }

        val normalized = ((phaseFraction % 1.0) + 1.0) % 1.0
        val illumination = (1.0 - cos(2.0 * Math.PI * normalized)) * 0.5

        val litRight = when (hemisphere) {
            Hemisphere.NORTHERN -> normalized < 0.5
            Hemisphere.SOUTHERN -> normalized >= 0.5
        }

        if (wobbleDeg != 0f) {
            canvas.save()
            canvas.rotate(wobbleDeg, cx, cy)
        }

        // Draw full dark disk, then paint the lit shape over it.
        canvas.drawOval(circle, darkPaint)

        if (illumination >= 0.999) {
            canvas.drawOval(circle, litPaint)
        } else if (illumination > 0.001) {
            val xOffset = radius * (1.0 - (2.0 * illumination)).toFloat()
            val ctrlX = if (litRight) cx + xOffset else cx - xOffset
            val arcSweep = if (litRight) -180f else 180f

            val litPath = Path().apply {
                moveTo(cx, cy - radius)
                quadTo(ctrlX, cy, cx, cy + radius)
                arcTo(circle, 90f, arcSweep, false)
                close()
            }
            canvas.drawPath(litPath, litPaint)
        }

        if (wobbleDeg != 0f) {
            canvas.restore()
        }

        return bitmap
    }
}
