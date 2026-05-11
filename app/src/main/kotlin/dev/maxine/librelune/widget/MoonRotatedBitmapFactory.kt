package dev.maxine.librelune.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap

object MoonRotatedBitmapFactory {
    /**
     * Loads a moon PNG by [drawableRes], scales it square to [sizePx], and bakes a
     * rotation by [wobbleDeg] degrees around the center. Returns a transparent-bg bitmap
     * suitable for ImageProvider in Glance widgets (which can't apply runtime rotation).
     */
    fun render(
        context: Context,
        @DrawableRes drawableRes: Int,
        sizePx: Int,
        wobbleDeg: Float,
    ): Bitmap {
        val source = context.resources.getDrawable(drawableRes, context.theme).toBitmap(sizePx, sizePx)

        if (wobbleDeg == 0f) return source

        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { isFilterBitmap = true }

        val matrix = Matrix().apply {
            postRotate(wobbleDeg, sizePx / 2f, sizePx / 2f)
        }
        canvas.drawBitmap(source, matrix, paint)
        return bitmap
    }
}
