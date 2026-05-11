package dev.maxine.librelune.widget.styles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.maxine.librelune.data.Hemisphere
import dev.maxine.librelune.data.WidgetSettings
import dev.maxine.librelune.moon.MoonState
import dev.maxine.librelune.widget.MoonGlyph
import dev.maxine.librelune.widget.MoonRenderPhase
import dev.maxine.librelune.widget.MoonRotatedBitmapFactory
import kotlin.math.roundToInt
import kotlin.math.cos

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

    val renderPhase = MoonRenderPhase.fromState(state)
    val drawableRes = MoonGlyph.drawableRes(renderPhase, settings.hemisphere)
    val moonBitmap = remember(drawableRes, state.wobbleDeg, moonBitmapSizePx) {
        MoonRotatedBitmapFactory.render(
            context = context,
            drawableRes = drawableRes,
            sizePx = moonBitmapSizePx,
            wobbleDeg = state.wobbleDeg,
        )
    }

    val phaseFraction = ((state.ageDays % SYNODIC_MONTH_DAYS) + SYNODIC_MONTH_DAYS) % SYNODIC_MONTH_DAYS / SYNODIC_MONTH_DAYS
    val normalized = ((phaseFraction % 1.0) + 1.0) % 1.0
    val litRight = when (settings.hemisphere) {
        Hemisphere.NORTHERN -> normalized < 0.5
        Hemisphere.SOUTHERN -> normalized >= 0.5
    }
    val illumination = (state.illuminationPct.coerceIn(0, 100) / 100f)
    val moonRadius = moonImageDiameter / 2
    val sideSign = if (litRight) 1f else -1f
    val curveApexOffset = moonRadius * (0.5f * (1f - 2f * illumination)) * sideSign
    val wobbleCos = cos(Math.toRadians(state.wobbleDeg.toDouble())).toFloat()
    val moonCenterX = size.width / 2
    val curveApexX = moonCenterX + (curveApexOffset * wobbleCos)
    val leftSpace = curveApexX.coerceIn(0.dp, size.width)
    val rightSpace = (size.width - curveApexX).coerceIn(0.dp, size.width)
    val textOnLeft = leftSpace >= rightSpace
    val darkRegionWidth = if (textOnLeft) leftSpace else rightSpace

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

        if (hasAnyText && darkRegionWidth > 0.dp) {
            Row(modifier = GlanceModifier.fillMaxSize()) {
                if (!textOnLeft) {
                    Spacer(modifier = GlanceModifier.defaultWeight())
                }
                Box(
                    modifier = GlanceModifier
                        .width(darkRegionWidth)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column {
                        if (settings.showPhaseName) {
                            Text(
                                text = state.phase.shortName,
                                style = TextStyle(
                                    color = ColorProvider(Color.White),
                                    fontSize = if (compact) 9.sp else 11.sp,
                                ),
                            )
                        }
                        if (settings.showIllumination) {
                            Text(
                                text = "${state.illuminationPct}%",
                                style = TextStyle(
                                    color = ColorProvider(Color.White.copy(alpha = 0.8f)),
                                    fontSize = if (compact) 9.sp else 10.sp,
                                ),
                            )
                        }
                        if (settings.showDaysToFull) {
                            Text(
                                text = "F+${state.daysToFull.toInt()}d",
                                style = TextStyle(
                                    color = ColorProvider(Color.White.copy(alpha = 0.7f)),
                                    fontSize = 9.sp,
                                ),
                            )
                        }
                        if (settings.showDaysToNew) {
                            Text(
                                text = "N+${state.daysToNew.toInt()}d",
                                style = TextStyle(
                                    color = ColorProvider(Color.White.copy(alpha = 0.7f)),
                                    fontSize = 9.sp,
                                ),
                            )
                        }
                    }
                }
                if (textOnLeft) {
                    Spacer(modifier = GlanceModifier.defaultWeight())
                }
            }
        }
    }
}

private const val SYNODIC_MONTH_DAYS = 29.530588853
