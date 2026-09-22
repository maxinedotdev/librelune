package dev.maxine.librelune.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import dev.maxine.librelune.data.Hemisphere
import dev.maxine.librelune.widget.styles.litSideOnRight
import kotlin.math.cos
import kotlin.math.abs

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

        val litRight = litSideOnRight(normalized, hemisphere)

        // Build the lit-side outline as ONE continuous closed path so the
        // terminator curve and the half-circle arc share real path joins
        // (round join) instead of butting two stroke caps together, which
        // previously produced a visible seam at top/bottom.
        val terminatorHalfWidth = radius * abs(1.0 - (2.0 * illumination)).toFloat()
        val terminatorBulgesRight = if (illumination <= 0.5) litRight else !litRight

        // Trace the terminator from the BOTTOM of the moon to the TOP, then
        // follow the disc edge back down the illuminated side. Using start
        // angles that match the current path point avoids implicit connector
        // segments before either arc.
        //   terminatorBulgesRight -> 90 + sweep -180 (bottom -> right -> top)
        //   terminatorBulgesLeft  -> 90 + sweep +180 (bottom -> left  -> top)
        //   litRight              -> 270 + sweep +180 (top -> right -> bottom)
        //   litLeft               -> 270 + sweep -180 (top -> left -> bottom)
        val circleStart = 270f
        val circleSweep = if (litRight) 180f else -180f

        val path = Path().apply {
            moveTo(cx, cy + radius)
            if (terminatorHalfWidth <= 0.001f) {
                lineTo(cx, cy - radius)
            } else {
                val terminator = RectF(
                    cx - terminatorHalfWidth,
                    cy - radius,
                    cx + terminatorHalfWidth,
                    cy + radius,
                )
                val terminatorSweep = if (terminatorBulgesRight) -180f else 180f
                arcTo(terminator, 90f, terminatorSweep, false)
            }
            arcTo(circle, circleStart, circleSweep, false)
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
