package dev.maxine.librelune.widget

import dev.maxine.librelune.R
import dev.maxine.librelune.moon.MoonPhase

object MoonGlyph {
    fun drawableRes(phase: MoonPhase): Int = when (phase) {
        MoonPhase.NEW -> R.drawable.moon_new
        MoonPhase.WAXING_CRESCENT -> R.drawable.moon_waxing_crescent
        MoonPhase.FIRST_QUARTER -> R.drawable.moon_first_quarter
        MoonPhase.WAXING_GIBBOUS -> R.drawable.moon_waxing_gibbous
        MoonPhase.FULL -> R.drawable.moon_full
        MoonPhase.WANING_GIBBOUS -> R.drawable.moon_waning_gibbous
        MoonPhase.THIRD_QUARTER -> R.drawable.moon_third_quarter
        MoonPhase.WANING_CRESCENT -> R.drawable.moon_waning_crescent
    }
}
