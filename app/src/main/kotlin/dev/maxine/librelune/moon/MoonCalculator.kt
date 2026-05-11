package dev.maxine.librelune.moon

import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.roundToInt
import org.shredzone.commons.suncalc.MoonIllumination
import org.shredzone.commons.suncalc.MoonPosition

class MoonCalculator(
    private val zoneId: ZoneId = ZoneId.systemDefault(),
    private val wobbleEnabled: Boolean = false,
    private val latitudeDeg: Double = 0.0,
    private val longitudeDeg: Double = 0.0,
) {
    fun now(now: ZonedDateTime = ZonedDateTime.now(zoneId)): MoonState {
        val latitude = latitudeDeg.coerceIn(-90.0, 90.0)
        val longitude = longitudeDeg.coerceIn(-180.0, 180.0)

        // Geocentric illumination (no observer correction) drives the phase
        // classification: toggling the wobble option must NOT change which phase
        // bucket the moon falls into.
        val illumination = MoonIllumination.compute()
            .on(now)
            .execute()

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

        // Compute synodic age (0..29.53d) from a robust pair:
        //   - illumination.fraction      : 0..1 illuminated disk fraction
        //   - sign of illumination.angle : negative => waxing, positive => waning
        // ageDays = waxingHalf when waxing, else (synodic - waxingHalf).
        val synodicDays = 29.530588853
        val fraction = illumination.fraction.coerceIn(0.0, 1.0)
        val waxingHalfDays = (acos(1.0 - 2.0 * fraction) / PI) * (synodicDays / 2.0)
        val isWaning = illumination.angle > 0.0
        val ageDays = (if (isWaning) synodicDays - waxingHalfDays else waxingHalfDays)
            .coerceIn(0.0, synodicDays)
        val phase = MoonPhase.fromAgeDays(ageDays)

        val wobbleDeg = if (wobbleEnabled) {
            val topoIllumination = MoonIllumination.compute()
                .on(now)
                .at(latitude, longitude)
                .execute()
            val moonPosition = MoonPosition.compute()
                .on(now)
                .at(latitude, longitude)
                .execute()

            // Observer-facing orientation of the bright limb (zenith angle):
            // MoonIllumination.angle - MoonPosition.parallacticAngle
            (topoIllumination.angle - moonPosition.parallacticAngle).toFloat()
        } else {
            0f
        }

        return MoonState(
            phase = phase,
            illuminationPct = (illumination.fraction * 100.0).roundToInt().coerceIn(0, 100),
            ageDays = ageDays,
            daysToFull = Duration.between(now, nextFull).toHours().toDouble() / 24.0,
            daysToNew = Duration.between(now, nextNew).toHours().toDouble() / 24.0,
            wobbleDeg = wobbleDeg,
        )
    }
}

data class MoonState(
    val phase: MoonPhase,
    val illuminationPct: Int,
    val ageDays: Double,
    val daysToFull: Double,
    val daysToNew: Double,
    val wobbleDeg: Float = 0f,
)
