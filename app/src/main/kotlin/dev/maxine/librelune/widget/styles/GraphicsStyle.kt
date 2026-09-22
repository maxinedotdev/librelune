package dev.maxine.librelune.widget.styles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import androidx.glance.layout.width
import dev.maxine.librelune.data.WidgetSettings
import dev.maxine.librelune.moon.MoonState
import dev.maxine.librelune.widget.MoonGlyph
import dev.maxine.librelune.widget.MoonRotatedBitmapFactory
import kotlin.math.roundToInt

/**
 * Renders one of eight pre-baked moon drawables, chosen by the quantized
 * [MoonState.phase].
 *
 * Because the artwork is bucketed into octants, this style can land on a
 * slightly different terminator position than the continuous, curve-drawn
 * [LineStyle] near octant boundaries. Making the two agree is future work;
 * see [dev.maxine.librelune.widget.MoonGlyph].
 */
@Composable
fun GraphicsStyle(state: MoonState, settings: WidgetSettings, clickAction: Action) {
    val context = LocalContext.current
    val size = LocalSize.current
    val compact = size.width <= 120.dp || size.height <= 120.dp
    val hasAnyText = settings.showPhaseName || settings.showIllumination ||
        settings.showDaysToFull || settings.showDaysToNew
    val iconPadding = settings.iconPaddingDp.coerceIn(0, 24).dp
    val diameterPct = settings.moonDiameterPct.coerceIn(40, 100)
    val minDimension = if (size.width < size.height) size.width else size.height
    val moonDiameter = minDimension * (diameterPct / 100f)
    val paddedDiameter = moonDiameter - (iconPadding * 2)
    val moonImageDiameter = if (paddedDiameter > 0.dp) paddedDiameter else 1.dp
    val density = context.resources.displayMetrics.density
    // Keep payload conservative for RemoteViews transport while preserving quality.
    val moonBitmapSizePx = ((moonImageDiameter.value * density).roundToInt())
        .coerceIn(128, 260)

    val drawableRes = MoonGlyph.drawableRes(state.phase, settings.hemisphere)
    val moonBitmap = remember(drawableRes, state.wobbleDeg, moonBitmapSizePx) {
        MoonRotatedBitmapFactory.render(
            context = context,
            drawableRes = drawableRes,
            sizePx = moonBitmapSizePx,
            wobbleDeg = state.wobbleDeg,
        )
    }

    val layout = moonLayout(
        state = state,
        hemisphere = settings.hemisphere,
        widgetWidth = size.width,
        moonRadius = moonImageDiameter / 2,
    )

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .clickable(clickAction),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            provider = ImageProvider(moonBitmap),
            contentDescription = state.phase.displayName,
            contentScale = ContentScale.Fit,
            modifier = GlanceModifier
                .size(moonImageDiameter),
        )

        if (hasAnyText && layout.darkRegionWidth > 0.dp) {
            GlanceWidgetTextColumn(
                state = state,
                settings = settings,
                compact = compact,
                layout = layout,
                styles = WidgetTextStyles(
                    phaseName = widgetTextStyle(0xFFFFFFFF, if (compact) 9.sp else 11.sp),
                    illumination = widgetTextStyle(0xFFFFFFFF, if (compact) 9.sp else 10.sp, alpha = 0.8f),
                    days = widgetTextStyle(0xFFFFFFFF, 9.sp, alpha = 0.7f),
                ),
            )
        }
    }
}
