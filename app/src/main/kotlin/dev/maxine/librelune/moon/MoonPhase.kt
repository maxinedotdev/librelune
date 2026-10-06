package dev.maxine.librelune.moon

/** Length of one mean synodic month (new moon to new moon), in days. */
internal const val SYNODIC_MONTH_DAYS = 29.530588853

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
        private const val OCTANT_DAYS = SYNODIC_MONTH_DAYS / 8.0

        fun fromAgeDays(ageDays: Double): MoonPhase {
            val age = ((ageDays % SYNODIC_MONTH_DAYS) + SYNODIC_MONTH_DAYS) % SYNODIC_MONTH_DAYS

            return entries.firstOrNull { age < OCTANT_DAYS * (it.ordinal + 0.5) } ?: NEW
        }

        fun fromAstronomy(phaseAngleDeg: Double, fraction: Double): MoonPhase {
            val f = fraction.coerceIn(0.0, 1.0)

            if (f <= 0.02) return NEW
            if (f >= 0.98) return FULL

            val isWaning = phaseAngleDeg >= 180.0
            val isQuarterBand = f in 0.48..0.52

            return when {
                isQuarterBand && isWaning -> THIRD_QUARTER
                isQuarterBand && !isWaning -> FIRST_QUARTER
                f < 0.5 && isWaning -> WANING_CRESCENT
                f < 0.5 && !isWaning -> WAXING_CRESCENT
                f > 0.5 && isWaning -> WANING_GIBBOUS
                else -> WAXING_GIBBOUS
            }
        }
    }
}
