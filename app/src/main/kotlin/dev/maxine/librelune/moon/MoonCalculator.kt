package dev.maxine.librelune.moon

import dev.maxine.librelune.data.Hemisphere
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
    private val hemisphere: Hemisphere = Hemisphere.NORTHERN,
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
            .on(now)
            .phase(org.shredzone.commons.suncalc.MoonPhase.Phase.FULL_MOON)
            .execute()
            .time

        val nextNew = org.shredzone.commons.suncalc.MoonPhase.compute()
            .on(now)
            .phase(org.shredzone.commons.suncalc.MoonPhase.Phase.NEW_MOON)
            .execute()
            .time

        // Compute synodic age (0..29.53d) from a robust pair:
        //   - illumination.fraction      : 0..1 illuminated disk fraction
        //   - sign of illumination.angle : negative => waxing, positive => waning
        // ageDays = waxingHalf when waxing, else (synodic - waxingHalf).
        val fraction = illumination.fraction.coerceIn(0.0, 1.0)
        val waxingHalfDays = (acos(1.0 - 2.0 * fraction) / PI) * (SYNODIC_MONTH_DAYS / 2.0)
        val isWaning = illumination.angle > 0.0
        val ageDays = (if (isWaning) SYNODIC_MONTH_DAYS - waxingHalfDays else waxingHalfDays)
            .coerceIn(0.0, SYNODIC_MONTH_DAYS)
        val phase = MoonPhase.fromIllumination(
            fraction = illumination.fraction,
            angleDeg = illumination.angle,
        )

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
            // suncalc expresses this angle as anticlockwise-positive, while
            // Android Canvas rotation is clockwise-positive, so invert sign.
            // This yields the true clockwise-from-zenith angle of the
            // illuminated limb.
            val trueLimbDeg = -(topoIllumination.angle - moonPosition.parallacticAngle).toFloat()

            // The base artwork (and the procedural line path) already draws the
            // illuminated limb at a canonical orientation: 90deg (right) when the
            // lit side is on the right, 270deg (left) otherwise. The lit side
            // depends on waxing/waning (matching MoonState.phaseFraction < 0.5
            // for waxing) plus hemisphere. Subtract that baked-in orientation so
            // only the residual observer tilt is applied, then keep a bounded
            // decorative tilt.
            val litRight = when (hemisphere) {
                Hemisphere.NORTHERN -> !isWaning
                Hemisphere.SOUTHERN -> isWaning
            }
            val baseLimbDeg = if (litRight) 90f else 270f

            normalizeSignedDegrees(trueLimbDeg - baseLimbDeg).coerceIn(-90f, 90f)
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

private fun normalizeSignedDegrees(angle: Float): Float {
    val wrapped = angle % 360f
    return when {
        wrapped <= -180f -> wrapped + 360f
        wrapped > 180f -> wrapped - 360f
        else -> wrapped
    }
}

data class MoonState(
    val phase: MoonPhase,
    val illuminationPct: Int,
    val ageDays: Double,
    val daysToFull: Double,
    val daysToNew: Double,
    val wobbleDeg: Float = 0f,
) {
    /**
     * Position within the synodic cycle, normalized to 0..1 with 0 at new
     * moon and 0.5 at full moon. Wraps negative ages into range.
     */
    val phaseFraction: Double
        get() {
            val wrapped = ((ageDays % SYNODIC_MONTH_DAYS) + SYNODIC_MONTH_DAYS) % SYNODIC_MONTH_DAYS
            return wrapped / SYNODIC_MONTH_DAYS
        }
}
