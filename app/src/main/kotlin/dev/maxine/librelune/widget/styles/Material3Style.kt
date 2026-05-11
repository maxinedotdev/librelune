package dev.maxine.librelune.widget.styles

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.clickable
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
import dev.maxine.librelune.moon.MoonState
import dev.maxine.librelune.widget.MoonGlyph

@Composable
fun Material3Style(state: MoonState, settings: WidgetSettings, clickAction: Action) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.primaryContainer)
            .padding(12.dp)
            .clickable(clickAction),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                provider = ImageProvider(MoonGlyph.drawableRes(state.phase)),
                contentDescription = state.phase.displayName,
                modifier = GlanceModifier.size(56.dp),
            )
            if (settings.showPhaseName) {
                Spacer(GlanceModifier.height(6.dp))
                Text(
                    text = state.phase.displayName,
                    style = TextStyle(
                        color = GlanceTheme.colors.onPrimaryContainer,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
            if (settings.showIllumination) {
                Text(
                    text = "${state.illuminationPct}%",
                    style = TextStyle(
                        color = GlanceTheme.colors.onPrimaryContainer,
                        fontSize = 10.sp,
                    ),
                )
            }
            if (settings.showDaysToFull) {
                Text(
                    text = "Full in ${state.daysToFull.toInt()}d",
                    style = TextStyle(
                        color = GlanceTheme.colors.onPrimaryContainer,
                        fontSize = 10.sp,
                    ),
                )
            }
            if (settings.showDaysToNew) {
                Text(
                    text = "New in ${state.daysToNew.toInt()}d",
                    style = TextStyle(
                        color = GlanceTheme.colors.onPrimaryContainer,
                        fontSize = 10.sp,
                    ),
                )
            }
        }
    }
}
