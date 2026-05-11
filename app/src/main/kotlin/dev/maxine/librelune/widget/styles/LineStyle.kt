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
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
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
    val phaseFraction = ((state.ageDays % SYNODIC_MONTH_DAYS) + SYNODIC_MONTH_DAYS) % SYNODIC_MONTH_DAYS / SYNODIC_MONTH_DAYS
    val moonBitmap = remember(phaseFraction, settings.hemisphere) {
        MoonLineBitmapFactory.render(
            phaseFraction = phaseFraction,
            hemisphere = settings.hemisphere,
            sizePx = if (compact) 360 else 420,
            strokePx = if (compact) 4f else 5f,
        )
    }

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0x14101521)))
            .cornerRadius(14.dp)
            .padding(if (compact) 6.dp else 8.dp)
            .clickable(clickAction),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                provider = ImageProvider(moonBitmap),
                contentDescription = state.phase.displayName,
                modifier = GlanceModifier
                    .width(if (compact) 74.dp else 84.dp)
                    .padding(iconPadding),
            )
            if (!compact && settings.showPhaseName) {
                Spacer(GlanceModifier.height(4.dp))
                Text(
                    text = state.phase.displayName,
                    style = TextStyle(
                        color = lineColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
            if (!compact && settings.showIllumination) {
                Text(
                    text = "${state.illuminationPct}%",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFAEB9CC)),
                        fontSize = 10.sp,
                    ),
                )
            }
            if (!compact && settings.showDaysToFull) {
                val days = state.daysToFull.toInt()
                Text(
                    text = "Full in ${days}d",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF8D99AE)),
                        fontSize = 9.sp,
                    ),
                )
            }
            if (!compact && settings.showDaysToNew) {
                val days = state.daysToNew.toInt()
                Text(
                    text = "New in ${days}d",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF8D99AE)),
                        fontSize = 9.sp,
                    ),
                )
            }
        }
    }
}

private const val SYNODIC_MONTH_DAYS = 29.530588853
