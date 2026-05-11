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
