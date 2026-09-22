package dev.maxine.librelune.widget

import dev.maxine.librelune.R
import dev.maxine.librelune.data.Hemisphere
import dev.maxine.librelune.moon.MoonPhase

/**
 * Maps a moon phase to the matching pre-rendered drawable.
 *
 * Note: this quantizes to the eight discrete [MoonPhase] buckets, whereas
 * [dev.maxine.librelune.widget.styles.LineStyle] and
 * [dev.maxine.librelune.widget.MoonLineBitmapFactory] draw the terminator
 * continuously from `MoonState.phaseFraction`. The two styles can therefore
 * differ slightly near octant boundaries. Unifying them is future work.
 */
object MoonGlyph {
    fun drawableRes(phase: MoonPhase, hemisphere: Hemisphere): Int =
        when (hemisphere) {
            Hemisphere.NORTHERN -> when (phase) {
                MoonPhase.NEW -> R.drawable.moon_new
                MoonPhase.WAXING_CRESCENT -> R.drawable.moon_waxing_crescent
                MoonPhase.FIRST_QUARTER -> R.drawable.moon_first_quarter
                MoonPhase.WAXING_GIBBOUS -> R.drawable.moon_waxing_gibbous
                MoonPhase.FULL -> R.drawable.moon_full
                MoonPhase.WANING_GIBBOUS -> R.drawable.moon_waning_gibbous
                MoonPhase.THIRD_QUARTER -> R.drawable.moon_third_quarter
                MoonPhase.WANING_CRESCENT -> R.drawable.moon_waning_crescent
            }
            Hemisphere.SOUTHERN -> when (phase) {
                MoonPhase.NEW -> R.drawable.moon_new_south
                MoonPhase.WAXING_CRESCENT -> R.drawable.moon_waxing_crescent_south
                MoonPhase.FIRST_QUARTER -> R.drawable.moon_first_quarter_south
                MoonPhase.WAXING_GIBBOUS -> R.drawable.moon_waxing_gibbous_south
                MoonPhase.FULL -> R.drawable.moon_full_south
                MoonPhase.WANING_GIBBOUS -> R.drawable.moon_waning_gibbous_south
                MoonPhase.THIRD_QUARTER -> R.drawable.moon_third_quarter_south
                MoonPhase.WANING_CRESCENT -> R.drawable.moon_waning_crescent_south
            }
        }
}
