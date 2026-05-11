package dev.maxine.librelune.moon

enum class MoonPhase(val displayName: String) {
    NEW("New Moon"),
    WAXING_CRESCENT("Waxing Crescent"),
    FIRST_QUARTER("First Quarter"),
    WAXING_GIBBOUS("Waxing Gibbous"),
    FULL("Full Moon"),
    WANING_GIBBOUS("Waning Gibbous"),
    THIRD_QUARTER("Third Quarter"),
    WANING_CRESCENT("Waning Crescent");

    companion object {
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
    }
}
