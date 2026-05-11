package dev.maxine.librelune.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.DrawableRes

object MoonRotatedBitmapFactory {
    /**
     * Decodes the moon PNG at its native pixel size (without density-based upscaling
     * that would otherwise produce multi-megabyte intermediate bitmaps on high-DPI
     * devices), down-scales to a square [sizePx], and bakes a rotation by [wobbleDeg]
     * degrees around the center. Returns a transparent-bg bitmap suitable for
     * ImageProvider in Glance widgets (which can't apply runtime rotation).
     */
    fun render(
        context: Context,
        @DrawableRes drawableRes: Int,
        sizePx: Int,
        wobbleDeg: Float,
    ): Bitmap {
        // inScaled=false: decode raw pixels (1024x1024 source), not density-scaled
        // (which on xxxhdpi becomes 4096x4096 / ~64MB and exceeds RemoteViews limits).
        val decodeOptions = BitmapFactory.Options().apply {
            inScaled = false
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val source = BitmapFactory.decodeResource(context.resources, drawableRes, decodeOptions)
            ?: return Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)

        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { isFilterBitmap = true }

        val center = sizePx / 2f
        canvas.save()
        if (wobbleDeg != 0f) {
            canvas.rotate(wobbleDeg, center, center)
        }
        val srcRect = Rect(0, 0, source.width, source.height)
        val dstRect = Rect(0, 0, sizePx, sizePx)
        canvas.drawBitmap(source, srcRect, dstRect, paint)
        canvas.restore()
        source.recycle()
        return bitmap
    }
}
