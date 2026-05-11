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
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.maxine.librelune.data.WidgetSettings
import dev.maxine.librelune.moon.MoonState
import dev.maxine.librelune.widget.MoonLineBitmapFactory

@Composable
fun LineStyle(state: MoonState, settings: WidgetSettings, clickAction: Action) {
    val size = LocalSize.current
    val compact = size.width <= 120.dp || size.height <= 120.dp
    val lineColor = ColorProvider(Color(0xFFE8EEF9))
    val iconPadding = settings.iconPaddingDp.coerceIn(0, 24).dp
    val lineStroke = settings.lineStrokeDp.coerceIn(1, 8).toFloat()
    val diameterPct = settings.moonDiameterPct.coerceIn(40, 100)
    val minDimension = if (size.width < size.height) size.width else size.height
    val moonDiameter = minDimension * (diameterPct / 100f)
    val edgeTouch = diameterPct >= 100
    val effectiveIconPadding = if (edgeTouch) 0.dp else iconPadding
    val phaseFraction = ((state.ageDays % SYNODIC_MONTH_DAYS) + SYNODIC_MONTH_DAYS) % SYNODIC_MONTH_DAYS / SYNODIC_MONTH_DAYS
    val strokePx = if (compact) lineStroke * 1.8f else lineStroke * 2.2f
    val moonBitmap = remember(phaseFraction, settings.hemisphere, strokePx, compact) {
        MoonLineBitmapFactory.render(
            phaseFraction = phaseFraction,
            hemisphere = settings.hemisphere,
            sizePx = if (compact) 360 else 420,
            strokePx = strokePx,
        )
    }

    val hasAnyText = settings.showPhaseName || settings.showIllumination ||
        settings.showDaysToFull || settings.showDaysToNew
    // Only reserve room for text if the widget is meaningfully wider than tall
    // AND something will actually be shown. Otherwise the moon takes the full
    // available space without text overlapping its outline.
    val hasTextRoom = hasAnyText && size.width > size.height + 24.dp

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(if (edgeTouch) 0.dp else if (compact) 2.dp else 4.dp)
            .clickable(clickAction),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = GlanceModifier
                    .size(moonDiameter)
                    .padding(effectiveIconPadding),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    provider = ImageProvider(moonBitmap),
                    contentDescription = state.phase.displayName,
                    contentScale = ContentScale.Fit,
                    modifier = GlanceModifier.fillMaxSize(),
                )
            }

            if (hasTextRoom) {
                Column(
                    modifier = GlanceModifier
                        .fillMaxHeight()
                        .padding(start = 8.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (settings.showPhaseName) {
                        Text(
                            text = state.phase.shortName,
                            style = TextStyle(
                                color = lineColor,
                                fontSize = if (compact) 11.sp else 13.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                    }
                    if (settings.showIllumination) {
                        Text(
                            text = "${state.illuminationPct}%",
                            style = TextStyle(
                                color = ColorProvider(Color(0xFFAEB9CC)),
                                fontSize = if (compact) 10.sp else 11.sp,
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
        }
    }
}

private const val SYNODIC_MONTH_DAYS = 29.530588853
