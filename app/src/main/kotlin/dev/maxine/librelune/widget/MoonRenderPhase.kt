package dev.maxine.librelune.widget

import dev.maxine.librelune.moon.MoonPhase
import dev.maxine.librelune.moon.MoonState

object MoonRenderPhase {
    private const val SYNODIC_MONTH_DAYS = 29.530588853
    private const val OCTANT_DAYS = SYNODIC_MONTH_DAYS / 8.0

    fun fromState(state: MoonState): MoonPhase {
        val age = ((state.ageDays % SYNODIC_MONTH_DAYS) + SYNODIC_MONTH_DAYS) % SYNODIC_MONTH_DAYS

        return when {
            age < OCTANT_DAYS / 2 -> MoonPhase.NEW
            age < OCTANT_DAYS * 1.5 -> MoonPhase.WAXING_CRESCENT
            age < OCTANT_DAYS * 2.5 -> MoonPhase.FIRST_QUARTER
            age < OCTANT_DAYS * 3.5 -> MoonPhase.WAXING_GIBBOUS
            age < OCTANT_DAYS * 4.5 -> MoonPhase.FULL
            age < OCTANT_DAYS * 5.5 -> MoonPhase.WANING_GIBBOUS
            age < OCTANT_DAYS * 6.5 -> MoonPhase.THIRD_QUARTER
            age < OCTANT_DAYS * 7.5 -> MoonPhase.WANING_CRESCENT
            else -> MoonPhase.NEW
        }
    }
}
