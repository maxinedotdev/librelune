package dev.maxine.librelune.widget.styles

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.clickable
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
import dev.maxine.librelune.data.Hemisphere
import dev.maxine.librelune.data.WidgetSettings
import dev.maxine.librelune.moon.MoonState
import dev.maxine.librelune.widget.MoonGlyph

@Composable
fun LineStyle(state: MoonState, settings: WidgetSettings, clickAction: Action) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(8.dp)
            .clickable(clickAction),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                provider = ImageProvider(MoonGlyph.drawableRes(state.phase, settings.hemisphere)),
                contentDescription = state.phase.displayName,
                modifier = GlanceModifier.size(64.dp),
            )
            if (settings.showPhaseName) {
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
            if (settings.showIllumination) {
                Text(
                    text = "${state.illuminationPct}%",
                    style = TextStyle(
                        color = ColorProvider(Color.White.copy(alpha = 0.7f)),
                        fontSize = 10.sp,
                    ),
                )
            }
            if (settings.showDaysToFull) {
                val days = state.daysToFull.toInt()
                Text(
                    text = "Full in ${days}d",
                    style = TextStyle(
                        color = ColorProvider(Color.White.copy(alpha = 0.6f)),
                        fontSize = 9.sp,
                    ),
                )
            }
            if (settings.showDaysToNew) {
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
