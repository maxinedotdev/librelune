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

        return MoonState(
            phase = phase,
            illuminationPct = (illumination.fraction * 100.0).roundToInt().coerceIn(0, 100),
            ageDays = (illumination.phase * 29.530588853).coerceAtLeast(0.0),
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
