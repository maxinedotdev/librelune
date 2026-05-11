package dev.maxine.librelune.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import dev.maxine.librelune.data.Hemisphere
import kotlin.math.cos
import kotlin.math.max

object MoonLineBitmapFactory {
    fun render(
        phaseFraction: Double,
        hemisphere: Hemisphere,
        sizePx: Int,
        strokePx: Float,
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = strokePx
            color = 0xFFE8EEF9.toInt()
        }

        val pad = max(strokePx * 2f, sizePx * 0.08f)
        val radius = (sizePx / 2f) - pad
        val cx = sizePx / 2f
        val cy = sizePx / 2f

        val normalized = ((phaseFraction % 1.0) + 1.0) % 1.0
        val illumination = (1.0 - cos(2.0 * Math.PI * normalized)) * 0.5

        if (illumination <= 0.001) {
            return bitmap
        }

        val litRight = when (hemisphere) {
            Hemisphere.NORTHERN -> normalized < 0.5
            Hemisphere.SOUTHERN -> normalized >= 0.5
        }

        val circle = RectF(cx - radius, cy - radius, cx + radius, cy + radius)

        if (illumination >= 0.999) {
            canvas.drawOval(circle, paint)
            return bitmap
        }

        if (litRight) {
            canvas.drawArc(circle, -90f, 180f, false, paint)
        } else {
            canvas.drawArc(circle, 90f, 180f, false, paint)
        }

        val xOffset = radius * (1.0 - (2.0 * illumination)).toFloat()
        val ctrlX = if (litRight) cx + xOffset else cx - xOffset

        val terminator = Path().apply {
            moveTo(cx, cy - radius)
            quadTo(ctrlX, cy, cx, cy + radius)
        }
        canvas.drawPath(terminator, paint)

        return bitmap
    }
}
