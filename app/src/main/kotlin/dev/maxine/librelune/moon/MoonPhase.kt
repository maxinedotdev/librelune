package dev.maxine.librelune.moon

enum class MoonPhase(val displayName: String, val shortName: String) {
    NEW("New Moon", "New"),
    WAXING_CRESCENT("Waxing Crescent", "WxCr"),
    FIRST_QUARTER("First Quarter", "1/4"),
    WAXING_GIBBOUS("Waxing Gibbous", "WxGi"),
    FULL("Full Moon", "Full"),
    WANING_GIBBOUS("Waning Gibbous", "WnGi"),
    THIRD_QUARTER("Third Quarter", "3/4"),
    WANING_CRESCENT("Waning Crescent", "WnCr");

    companion object {
        private const val SYNODIC_MONTH_DAYS = 29.530588853
        private const val OCTANT_DAYS = SYNODIC_MONTH_DAYS / 8.0

        fun fromLibraryPhase(phase: org.shredzone.commons.suncalc.MoonPhase.Phase): MoonPhase {
            return when (phase) {
                org.shredzone.commons.suncalc.MoonPhase.Phase.NEW_MOON -> NEW
                org.shredzone.commons.suncalc.MoonPhase.Phase.WAXING_CRESCENT -> WAXING_CRESCENT
                org.shredzone.commons.suncalc.MoonPhase.Phase.FIRST_QUARTER -> FIRST_QUARTER
                org.shredzone.commons.suncalc.MoonPhase.Phase.WAXING_GIBBOUS -> WAXING_GIBBOUS
                org.shredzone.commons.suncalc.MoonPhase.Phase.FULL_MOON -> FULL
                org.shredzone.commons.suncalc.MoonPhase.Phase.WANING_GIBBOUS -> WANING_GIBBOUS
                org.shredzone.commons.suncalc.MoonPhase.Phase.LAST_QUARTER -> THIRD_QUARTER
                org.shredzone.commons.suncalc.MoonPhase.Phase.WANING_CRESCENT -> WANING_CRESCENT
            }
        }

        fun fromAgeDays(ageDays: Double): MoonPhase {
            val age = ((ageDays % SYNODIC_MONTH_DAYS) + SYNODIC_MONTH_DAYS) % SYNODIC_MONTH_DAYS

            return when {
                age < OCTANT_DAYS / 2 -> NEW
                age < OCTANT_DAYS * 1.5 -> WAXING_CRESCENT
                age < OCTANT_DAYS * 2.5 -> FIRST_QUARTER
                age < OCTANT_DAYS * 3.5 -> WAXING_GIBBOUS
                age < OCTANT_DAYS * 4.5 -> FULL
                age < OCTANT_DAYS * 5.5 -> WANING_GIBBOUS
                age < OCTANT_DAYS * 6.5 -> THIRD_QUARTER
                age < OCTANT_DAYS * 7.5 -> WANING_CRESCENT
                else -> NEW
            }
        }
    }
}
