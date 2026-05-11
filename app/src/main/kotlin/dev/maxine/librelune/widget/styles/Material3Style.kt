package dev.maxine.librelune.widget.styles

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.GlanceTheme
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
import dev.maxine.librelune.data.WidgetSettings
import dev.maxine.librelune.moon.MoonPhase
import dev.maxine.librelune.moon.MoonState

@Composable
fun Material3Style(state: MoonState, settings: WidgetSettings, clickAction: Action) {
    val size = LocalSize.current
    val compact = size.width <= 120.dp || size.height <= 120.dp

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .cornerRadius(20.dp)
            .padding(if (compact) 8.dp else 12.dp)
            .clickable(clickAction),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = GlanceModifier
                    .background(GlanceTheme.colors.secondaryContainer)
                    .cornerRadius(if (compact) 14.dp else 20.dp)
                    .padding(if (compact) 6.dp else 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = materialGlyph(state.phase),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSecondaryContainer,
                        fontSize = if (compact) 28.sp else 36.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                )
            }
            if (!compact && settings.showPhaseName) {
                Spacer(GlanceModifier.height(6.dp))
                Text(
                    text = state.phase.displayName,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
            if (settings.showIllumination) {
                Box(
                    modifier = GlanceModifier
                        .padding(top = if (compact) 4.dp else 6.dp)
                        .background(GlanceTheme.colors.primaryContainer)
                        .cornerRadius(999.dp)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = "${state.illuminationPct}%",
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontSize = if (compact) 9.sp else 10.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                }
            }
            if (!compact && settings.showDaysToFull) {
                Text(
                    text = "Full in ${state.daysToFull.toInt()}d",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 9.sp,
                    ),
                )
            }
            if (!compact && settings.showDaysToNew) {
                Text(
                    text = "New in ${state.daysToNew.toInt()}d",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 9.sp,
                    ),
                )
            }
        }
    }
}

private fun materialGlyph(phase: MoonPhase): String = when (phase) {
    MoonPhase.NEW -> "●"
    MoonPhase.WAXING_CRESCENT -> "◔"
    MoonPhase.FIRST_QUARTER -> "◐"
    MoonPhase.WAXING_GIBBOUS -> "◕"
    MoonPhase.FULL -> "○"
    MoonPhase.WANING_GIBBOUS -> "◕"
    MoonPhase.THIRD_QUARTER -> "◑"
    MoonPhase.WANING_CRESCENT -> "◔"
}
