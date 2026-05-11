package dev.maxine.librelune.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import dev.maxine.librelune.data.Hemisphere
import kotlin.math.cos

object MoonLineBitmapFactory {
    fun render(
        phaseFraction: Double,
        hemisphere: Hemisphere,
        sizePx: Int,
        strokePx: Float,
        wobbleDeg: Float = 0f,
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = strokePx
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            color = 0xFFE8EEF9.toInt()
        }

        // Keep the stroke center one half-stroke from bounds so outer edge can touch container.
        val pad = strokePx * 0.5f
        val radius = (sizePx / 2f) - pad
        val cx = sizePx / 2f
        val cy = sizePx / 2f

        val normalized = ((phaseFraction % 1.0) + 1.0) % 1.0
        val illumination = (1.0 - cos(2.0 * Math.PI * normalized)) * 0.5

        if (illumination <= 0.001) {
            return bitmap
        }

        val circle = RectF(cx - radius, cy - radius, cx + radius, cy + radius)

        if (illumination >= 0.999) {
            canvas.drawOval(circle, paint)
            return bitmap
        }

        val litRight = when (hemisphere) {
            Hemisphere.NORTHERN -> normalized < 0.5
            Hemisphere.SOUTHERN -> normalized >= 0.5
        }

        // Build the lit-side outline as ONE continuous closed path so the
        // terminator curve and the half-circle arc share real path joins
        // (round join) instead of butting two stroke caps together, which
        // previously produced a visible seam at top/bottom.
        val xOffset = radius * (1.0 - (2.0 * illumination)).toFloat()
        val ctrlX = if (litRight) cx + xOffset else cx - xOffset

        // After the terminator the current point is at the BOTTOM of the
        // moon, so the arc must also start at the bottom (90deg) and sweep
        // along the lit side back to the top. Sweep direction picks the side:
        //   litRight -> sweep -180 (bottom -> right -> top)
        //   litLeft  -> sweep +180 (bottom -> left  -> top)
        // Using forceMoveTo=false with a matching start point avoids an
        // implicit straight line being added before the arc.
        val arcSweep = if (litRight) -180f else 180f

        val path = Path().apply {
            moveTo(cx, cy - radius)
            quadTo(ctrlX, cy, cx, cy + radius)
            arcTo(circle, 90f, arcSweep, false)
        }
        if (wobbleDeg != 0f) {
            canvas.save()
            canvas.rotate(wobbleDeg, cx, cy)
            canvas.drawPath(path, paint)
            canvas.restore()
        } else {
            canvas.drawPath(path, paint)
        }

        return bitmap
    }
}
