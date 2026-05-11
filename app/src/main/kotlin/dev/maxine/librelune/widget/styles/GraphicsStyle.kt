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
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.maxine.librelune.data.WidgetSettings
import dev.maxine.librelune.moon.MoonState
import dev.maxine.librelune.widget.MoonGlyph
import dev.maxine.librelune.widget.MoonRenderPhase

@Composable
fun GraphicsStyle(state: MoonState, settings: WidgetSettings, clickAction: Action) {
    val size = LocalSize.current
    val compact = size.width <= 120.dp || size.height <= 120.dp
    val renderPhase = MoonRenderPhase.fromState(state)
    val iconPadding = settings.iconPaddingDp.coerceIn(0, 24).dp
    val diameterPct = settings.moonDiameterPct.coerceIn(40, 100)
    val minDimension = if (size.width < size.height) size.width else size.height
    val moonDiameter = minDimension * (diameterPct / 100f)
    val effectiveIconPadding = if (diameterPct >= 100) 0.dp else iconPadding

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(18.dp)
            .clickable(clickAction),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            provider = ImageProvider(MoonGlyph.drawableRes(renderPhase, settings.hemisphere)),
            contentDescription = state.phase.displayName,
            modifier = GlanceModifier
                .size(moonDiameter)
                .padding(effectiveIconPadding),
        )

        // Overlay text in the bottom-left corner
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(bottom = 8.dp, start = 8.dp),
            contentAlignment = Alignment.BottomStart,
        ) {
            Column {
                if (settings.showPhaseName) {
                    Text(
                        text = state.phase.displayName,
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
                        text = if (compact) "F+${state.daysToFull.toInt()}d" else "Full in ${state.daysToFull.toInt()}d",
                        style = TextStyle(
                            color = ColorProvider(Color.White.copy(alpha = 0.7f)),
                            fontSize = 9.sp,
                        ),
                    )
                }
                if (settings.showDaysToNew) {
                    Text(
                        text = if (compact) "N+${state.daysToNew.toInt()}d" else "New in ${state.daysToNew.toInt()}d",
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
