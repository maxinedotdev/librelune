package dev.maxine.librelune.widget.styles

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
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
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.maxine.librelune.data.WidgetSettings
import dev.maxine.librelune.moon.MoonPhase
import dev.maxine.librelune.moon.MoonState

@Composable
fun LineStyle(state: MoonState, settings: WidgetSettings, clickAction: Action) {
    val size = LocalSize.current
    val compact = size.width <= 120.dp || size.height <= 120.dp

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFF0B1220)))
            .cornerRadius(16.dp)
            .padding(if (compact) 6.dp else 8.dp)
            .clickable(clickAction),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = lineGlyph(state.phase),
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = if (compact) 34.sp else 46.sp,
                ),
            )
            if (!compact && settings.showPhaseName) {
                Spacer(GlanceModifier.height(4.dp))
                Text(
                    text = state.phase.displayName,
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
            if (!compact && settings.showIllumination) {
                Text(
                    text = "${state.illuminationPct}%",
                    style = TextStyle(
                        color = ColorProvider(Color.White.copy(alpha = 0.7f)),
                        fontSize = 10.sp,
                    ),
                )
            }
            if (!compact && settings.showDaysToFull) {
                val days = state.daysToFull.toInt()
                Text(
                    text = "Full in ${days}d",
                    style = TextStyle(
                        color = ColorProvider(Color.White.copy(alpha = 0.6f)),
                        fontSize = 9.sp,
                    ),
                )
            }
            if (!compact && settings.showDaysToNew) {
                val days = state.daysToNew.toInt()
                Text(
                    text = "New in ${days}d",
                    style = TextStyle(
                        color = ColorProvider(Color.White.copy(alpha = 0.6f)),
                        fontSize = 9.sp,
                    ),
                )
            }
        }
    }
}

private fun lineGlyph(phase: MoonPhase): String = when (phase) {
    MoonPhase.NEW -> "●"
    MoonPhase.WAXING_CRESCENT -> "◔"
    MoonPhase.FIRST_QUARTER -> "◑"
    MoonPhase.WAXING_GIBBOUS -> "◕"
    MoonPhase.FULL -> "○"
    MoonPhase.WANING_GIBBOUS -> "◕"
    MoonPhase.THIRD_QUARTER -> "◐"
    MoonPhase.WANING_CRESCENT -> "◔"
}
