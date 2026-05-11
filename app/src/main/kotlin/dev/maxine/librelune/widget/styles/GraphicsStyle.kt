package dev.maxine.librelune.widget.styles

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.maxine.librelune.data.WidgetSettings
import dev.maxine.librelune.moon.MoonState
import dev.maxine.librelune.widget.MoonGlyph

@Composable
fun GraphicsStyle(state: MoonState, settings: WidgetSettings, clickAction: Action) {
    val size = LocalSize.current
    val compact = size.width <= 120.dp || size.height <= 120.dp

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFF05070C)))
            .cornerRadius(18.dp)
            .clickable(clickAction),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            provider = ImageProvider(MoonGlyph.drawableRes(state.phase, settings.hemisphere)),
            contentDescription = state.phase.displayName,
            modifier = GlanceModifier.fillMaxSize(),
        )

        // Overlay text in the bottom-left corner
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(bottom = 8.dp, start = 8.dp),
            contentAlignment = Alignment.BottomStart,
        ) {
            Column {
                if (!compact && settings.showPhaseName) {
                    Text(
                        text = state.phase.displayName,
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = 11.sp,
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
                if (!compact && settings.showDaysToFull) {
                    Text(
                        text = "Full in ${state.daysToFull.toInt()}d",
                        style = TextStyle(
                            color = ColorProvider(Color.White.copy(alpha = 0.7f)),
                            fontSize = 9.sp,
                        ),
                    )
                }
                if (!compact && settings.showDaysToNew) {
                    Text(
                        text = "New in ${state.daysToNew.toInt()}d",
                        style = TextStyle(
                            color = ColorProvider(Color.White.copy(alpha = 0.7f)),
                            fontSize = 9.sp,
                        ),
                    )
                }
            }
        }
    }
}
