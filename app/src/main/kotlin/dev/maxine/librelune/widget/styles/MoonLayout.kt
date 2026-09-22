package dev.maxine.librelune.widget.styles

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.maxine.librelune.data.Hemisphere
import dev.maxine.librelune.moon.MoonState
import kotlin.math.cos

/**
 * Horizontal placement of the text column over the dark half of the moon.
 *
 * [textOnLeft] mirrors exactly how the renderer chooses the lit side:
 * north waxing -> right lit, north waning -> left lit, inverted in the
 * southern hemisphere.
 *
 * [darkRegionWidth] spans from the widget border on the dark side to the
 * terminator's apex (the deepest point of the curve, at vertical
 * mid-height), so the text never overlaps the lit curve. The line renderer
 * draws the terminator as a half-ellipse whose horizontal apex offset is
 * r * (1 - 2*illum) with the sign following the lit side.
 */
internal data class MoonLayout(
    val textOnLeft: Boolean,
    val darkRegionWidth: Dp,
)

/**
 * Which side of the moon row the illuminated limb sits on, given the
 * normalized position in the synodic cycle (0 = new, 0.5 = full).
 *
 * North waxing -> right lit, north waning -> left lit; inverted in the
 * southern hemisphere.
 */
internal fun litSideOnRight(phaseFraction: Double, hemisphere: Hemisphere): Boolean =
    when (hemisphere) {
        Hemisphere.NORTHERN -> phaseFraction < 0.5
        Hemisphere.SOUTHERN -> phaseFraction >= 0.5
    }

internal fun moonLayout(
    state: MoonState,
    hemisphere: Hemisphere,
    widgetWidth: Dp,
    moonRadius: Dp,
): MoonLayout {
    val litRight = litSideOnRight(state.phaseFraction, hemisphere)
    val illumination = state.illuminationPct.coerceIn(0, 100) / 100f
    val sideSign = if (litRight) 1f else -1f
    val curveApexOffset = moonRadius * (1f - 2f * illumination) * sideSign
    val wobbleCos = cos(Math.toRadians(state.wobbleDeg.toDouble())).toFloat()
    val curveApexX = widgetWidth / 2 + (curveApexOffset * wobbleCos)

    val leftSpace = curveApexX.coerceIn(0.dp, widgetWidth)
    val rightSpace = (widgetWidth - curveApexX).coerceIn(0.dp, widgetWidth)
    val textOnLeft = leftSpace >= rightSpace

    return MoonLayout(
        textOnLeft = textOnLeft,
        darkRegionWidth = if (textOnLeft) leftSpace else rightSpace,
    )
}
