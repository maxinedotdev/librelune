package dev.maxine.librelune.moon

import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.roundToInt
import org.shredzone.commons.suncalc.MoonIllumination

class MoonCalculator(
    private val zoneId: ZoneId = ZoneId.systemDefault(),
) {
    fun now(now: ZonedDateTime = ZonedDateTime.now(zoneId)): MoonState {
        val illumination = MoonIllumination.compute()
            .on(now)
            .execute()

        val phase = MoonPhase.fromLibraryPhase(illumination.closestPhase)

        val nextFull = org.shredzone.commons.suncalc.MoonPhase.compute()
            .on(now.plusDays(1))
            .phase(org.shredzone.commons.suncalc.MoonPhase.Phase.FULL_MOON)
            .execute()
            .time

        val nextNew = org.shredzone.commons.suncalc.MoonPhase.compute()
            .on(now.plusDays(1))
            .phase(org.shredzone.commons.suncalc.MoonPhase.Phase.NEW_MOON)
            .execute()
            .time

        // commons-suncalc's MoonIllumination.phase is an angle in degrees:
        // -180 = new moon (start), 0 = full moon, +180 = new moon (end of cycle).
        // Map it to an age in days within the synodic month [0, 29.530588853].
        val ageDays = ((illumination.phase + 180.0) / 360.0 * 29.530588853)
            .coerceIn(0.0, 29.530588853)

        return MoonState(
            phase = phase,
            illuminationPct = (illumination.fraction * 100.0).roundToInt().coerceIn(0, 100),
            ageDays = ageDays,
            daysToFull = Duration.between(now, nextFull).toHours().toDouble() / 24.0,
            daysToNew = Duration.between(now, nextNew).toHours().toDouble() / 24.0,
        )
    }
}

data class MoonState(
    val phase: MoonPhase,
    val illuminationPct: Int,
    val ageDays: Double,
    val daysToFull: Double,
    val daysToNew: Double,
)
