package dev.maxine.librelune.widget.styles

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.maxine.librelune.data.WidgetSettings
import dev.maxine.librelune.moon.MoonState

/** Per-style typography for the text column rendered over the dark side. */
internal data class WidgetTextStyles(
    val phaseName: TextStyle,
    val illumination: TextStyle,
    val days: TextStyle,
)

@Composable
internal fun GlanceWidgetTextColumn(
    state: MoonState,
    settings: WidgetSettings,
    compact: Boolean,
    layout: MoonLayout,
    styles: WidgetTextStyles,
) {
    Row(modifier = GlanceModifier.fillMaxSize()) {
        if (!layout.textOnLeft) {
            Spacer(modifier = GlanceModifier.defaultWeight())
        }
        Box(
            modifier = GlanceModifier
                .width(layout.darkRegionWidth)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            Column {
                if (settings.showPhaseName) {
                    Text(text = state.phase.shortName, style = styles.phaseName)
                }
                if (settings.showIllumination) {
                    Text(text = "${state.illuminationPct}%", style = styles.illumination)
                }
                if (settings.showDaysToFull) {
                    Text(text = "F+${state.daysToFull.toInt()}d", style = styles.days)
                }
                if (settings.showDaysToNew) {
                    Text(text = "N+${state.daysToNew.toInt()}d", style = styles.days)
                }
            }
        }
        if (layout.textOnLeft) {
            Spacer(modifier = GlanceModifier.defaultWeight())
        }
    }
}

internal fun widgetTextStyle(
    color: Long,
    fontSize: TextUnit,
    fontWeight: FontWeight? = null,
    alpha: Float = 1f,
): TextStyle = TextStyle(
    color = ColorProvider(
        if (alpha == 1f) Color(color) else Color(color).copy(alpha = alpha),
    ),
    fontSize = fontSize,
    fontWeight = fontWeight,
)
