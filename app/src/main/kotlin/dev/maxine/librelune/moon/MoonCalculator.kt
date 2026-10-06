package dev.maxine.librelune.moon

import dev.maxine.librelune.data.Hemisphere
import io.github.cosinekitty.astronomy.Aberration
import io.github.cosinekitty.astronomy.Body
import io.github.cosinekitty.astronomy.EquatorEpoch
import io.github.cosinekitty.astronomy.Observer
import io.github.cosinekitty.astronomy.Refraction
import io.github.cosinekitty.astronomy.Time
import io.github.cosinekitty.astronomy.equator
import io.github.cosinekitty.astronomy.horizon
import io.github.cosinekitty.astronomy.illumination
import io.github.cosinekitty.astronomy.moonPhase
import io.github.cosinekitty.astronomy.rotationEqdHor
import io.github.cosinekitty.astronomy.searchMoonPhase
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.math.atan2
import kotlin.math.roundToInt

private const val PHASE_SEARCH_DAYS = 35.0

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
        val time = now.toAstronomyTime()

        val phaseAngle = moonPhase(time)
        val moonIllumination = illumination(Body.Moon, time)
        val previousNew = requireNotNull(searchMoonPhase(0.0, time, -PHASE_SEARCH_DAYS)) {
            "Could not find previous new moon"
        }
        val nextFull = requireNotNull(searchMoonPhase(180.0, time, PHASE_SEARCH_DAYS)) {
            "Could not find next full moon"
        }
        val nextNew = requireNotNull(searchMoonPhase(0.0, time, PHASE_SEARCH_DAYS)) {
            "Could not find next new moon"
        }
        val ageDays = (time.ut - previousNew.ut).coerceAtLeast(0.0)
        val phase = MoonPhase.fromAstronomy(phaseAngle, moonIllumination.phaseFraction)

        val wobbleDeg = if (wobbleEnabled) {
            val observer = Observer(latitude, longitude, 0.0)
            val moon = equator(Body.Moon, time, observer, EquatorEpoch.OfDate, Aberration.Corrected)
            val sun = equator(Body.Sun, time, observer, EquatorEpoch.OfDate, Aberration.Corrected)
            // Keep geometric coordinates: refraction would change the existing tilt.
            val moonHorizon = horizon(time, observer, moon.ra, moon.dec, Refraction.None)

            // Point the horizontal frame at the Moon: x toward it, y screen-left, z screen-up.
            val moonFrame = rotationEqdHor(time, observer)
                .pivot(2, moonHorizon.azimuth)
                .pivot(1, moonHorizon.altitude)
            val sunInMoonFrame = moonFrame.rotate(sun.vec)
            val trueLimbDeg = -Math.toDegrees(atan2(sunInMoonFrame.y, sunInMoonFrame.z)).toFloat()

            // The base artwork (and the procedural line path) already draws the
            // illuminated limb at a canonical orientation: 90deg (right) when the
            // lit side is on the right, 270deg (left) otherwise. The lit side
            // depends on waxing/waning (matching MoonState.phaseFraction < 0.5
            // for waxing) plus hemisphere. Subtract that baked-in orientation so
            // only the residual observer tilt is applied, then keep a bounded
            // decorative tilt.
            val litRight = when (hemisphere) {
                Hemisphere.NORTHERN -> phaseAngle < 180.0
                Hemisphere.SOUTHERN -> phaseAngle >= 180.0
            }
            val baseLimbDeg = if (litRight) 90f else 270f

            normalizeSignedDegrees(trueLimbDeg - baseLimbDeg).coerceIn(-90f, 90f)
        } else {
            0f
        }

        return MoonState(
            phase = phase,
            illuminationPct = (moonIllumination.phaseFraction * 100.0).roundToInt().coerceIn(0, 100),
            ageDays = ageDays,
            daysToFull = nextFull.ut - time.ut,
            daysToNew = nextNew.ut - time.ut,
            wobbleDeg = wobbleDeg,
            phaseFraction = phaseAngle / 360.0,
        )
    }
}

private fun ZonedDateTime.toAstronomyTime(): Time {
    val utc = withZoneSameInstant(ZoneOffset.UTC)
    return Time(utc.year, utc.monthValue, utc.dayOfMonth, utc.hour, utc.minute, utc.second + utc.nano / 1e9)
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
    /** Astronomy Engine's phase angle normalized from 0 (new) to 0.5 (full). */
    val phaseFraction: Double =
        (((ageDays % SYNODIC_MONTH_DAYS) + SYNODIC_MONTH_DAYS) % SYNODIC_MONTH_DAYS) / SYNODIC_MONTH_DAYS,
)
