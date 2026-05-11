package dev.maxine.librelune.widget

import dev.maxine.librelune.R
import dev.maxine.librelune.data.Hemisphere
import dev.maxine.librelune.moon.MoonPhase

object MoonLineGlyph {
    fun drawableRes(phase: MoonPhase, hemisphere: Hemisphere): Int {
        val visualPhase = when (hemisphere) {
            Hemisphere.NORTHERN -> phase
            Hemisphere.SOUTHERN -> phase.forSouthernView()
        }

        return when (visualPhase) {
            MoonPhase.NEW -> R.drawable.line_moon_new
            MoonPhase.WAXING_CRESCENT -> R.drawable.line_moon_waxing_crescent
            MoonPhase.FIRST_QUARTER -> R.drawable.line_moon_first_quarter
            MoonPhase.WAXING_GIBBOUS -> R.drawable.line_moon_waxing_gibbous
            MoonPhase.FULL -> R.drawable.line_moon_full
            MoonPhase.WANING_GIBBOUS -> R.drawable.line_moon_waning_gibbous
            MoonPhase.THIRD_QUARTER -> R.drawable.line_moon_third_quarter
            MoonPhase.WANING_CRESCENT -> R.drawable.line_moon_waning_crescent
        }
    }

    private fun MoonPhase.forSouthernView(): MoonPhase = when (this) {
        MoonPhase.WAXING_CRESCENT -> MoonPhase.WANING_CRESCENT
        MoonPhase.WANING_CRESCENT -> MoonPhase.WAXING_CRESCENT
        MoonPhase.FIRST_QUARTER -> MoonPhase.THIRD_QUARTER
        MoonPhase.THIRD_QUARTER -> MoonPhase.FIRST_QUARTER
        MoonPhase.WAXING_GIBBOUS -> MoonPhase.WANING_GIBBOUS
        MoonPhase.WANING_GIBBOUS -> MoonPhase.WAXING_GIBBOUS
        MoonPhase.NEW,
        MoonPhase.FULL,
        -> this
    }
}
