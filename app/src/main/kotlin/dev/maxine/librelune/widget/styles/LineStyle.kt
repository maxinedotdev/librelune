package dev.maxine.librelune.widget.styles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.clickable
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
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.maxine.librelune.data.Hemisphere
import dev.maxine.librelune.data.WidgetSettings
import dev.maxine.librelune.moon.MoonState
import dev.maxine.librelune.widget.MoonLineBitmapFactory
import kotlin.math.cos

@Composable
fun LineStyle(state: MoonState, settings: WidgetSettings, clickAction: Action) {
    val size = LocalSize.current
    val compact = size.width <= 120.dp || size.height <= 120.dp
    val lineColor = ColorProvider(Color(0xFFE8EEF9))
    val iconPadding = settings.iconPaddingDp.coerceIn(0, 24).dp
    val lineStroke = settings.lineStrokeDp.coerceIn(1, 8).toFloat()
    val diameterPct = settings.moonDiameterPct.coerceIn(40, 100)
    val hasAnyText = settings.showPhaseName || settings.showIllumination ||
        settings.showDaysToFull || settings.showDaysToNew

    // Moon keeps the full minimum-dimension diameter regardless of text.
    val minDimension = if (size.width < size.height) size.width else size.height
    val moonDiameter = minDimension * (diameterPct / 100f)
    val edgeTouch = diameterPct >= 100
    val effectiveIconPadding = if (edgeTouch) 0.dp else iconPadding

    val phaseFraction = ((state.ageDays % SYNODIC_MONTH_DAYS) + SYNODIC_MONTH_DAYS) % SYNODIC_MONTH_DAYS / SYNODIC_MONTH_DAYS
    val strokePx = if (compact) lineStroke * 1.8f else lineStroke * 2.2f
    val moonBitmap = remember(phaseFraction, settings.hemisphere, strokePx, compact, state.wobbleDeg) {
        MoonLineBitmapFactory.render(
            phaseFraction = phaseFraction,
            hemisphere = settings.hemisphere,
            sizePx = if (compact) 360 else 420,
            strokePx = strokePx,
            wobbleDeg = state.wobbleDeg,
        )
    }

    // Place the text on the DARK side of the moon (the empty half of the
    // square bounding box), so it never overlaps the lit-side curve. Mirrors
    // exactly how the renderer chooses lit side: north waxing -> right lit,
    // north waning -> left lit (and inverted in the southern hemisphere).
    val normalized = ((phaseFraction % 1.0) + 1.0) % 1.0
    val litRight = when (settings.hemisphere) {
        Hemisphere.NORTHERN -> normalized < 0.5
        Hemisphere.SOUTHERN -> normalized >= 0.5
    }

    // Centre the text horizontally within the dark region:
    // from the widget border on the dark side to the terminator's apex
    // (the deepest point of the curve, at vertical mid-height). The
    // quadratic bezier midpoint at t=0.5 is (cx + 0.5*xOffset, cy), with
    // xOffset = r * (1 - 2*illum). Sign follows the lit side.
    val illumination = (state.illuminationPct.coerceIn(0, 100) / 100f)
    val moonRadius = moonDiameter / 2
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
            .padding(if (edgeTouch) 0.dp else if (compact) 2.dp else 4.dp)
            .clickable(clickAction),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            provider = ImageProvider(moonBitmap),
            contentDescription = state.phase.displayName,
            contentScale = ContentScale.Fit,
            modifier = GlanceModifier
                .size(moonDiameter)
                .padding(effectiveIconPadding),
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
                                    color = lineColor,
                                    fontSize = if (compact) 10.sp else 12.sp,
                                    fontWeight = FontWeight.Medium,
                                ),
                            )
                        }
                        if (settings.showIllumination) {
                            Text(
                                text = "${state.illuminationPct}%",
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFFAEB9CC)),
                                    fontSize = if (compact) 9.sp else 11.sp,
                                ),
                            )
                        }
                        if (settings.showDaysToFull) {
                            val days = state.daysToFull.toInt()
                            Text(
                                text = "F+${days}d",
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFF8D99AE)),
                                    fontSize = if (compact) 9.sp else 10.sp,
                                ),
                            )
                        }
                        if (settings.showDaysToNew) {
                            val days = state.daysToNew.toInt()
                            Text(
                                text = "N+${days}d",
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFF8D99AE)),
                                    fontSize = if (compact) 9.sp else 10.sp,
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
