package dev.maxine.librelune.moon

import dev.maxine.librelune.data.Hemisphere
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.shredzone.commons.suncalc.MoonIllumination
import org.shredzone.commons.suncalc.MoonPosition

class MoonCalculatorTest {
    @Test
    fun `known full moon date resolves to full-like phase`() {
        val calculator = MoonCalculator(ZoneOffset.UTC)
        val knownFullMoon = ZonedDateTime.of(2024, 4, 23, 23, 49, 0, 0, ZoneOffset.UTC)

        val result = calculator.now(knownFullMoon)

        assertEquals(MoonPhase.FULL, result.phase)
        assertTrue(result.illuminationPct >= 95)
    }

    @Test
    fun `imminent full moon is not skipped to next synodic month`() {
        val calculator = MoonCalculator(ZoneOffset.UTC)

        val result = calculator.now(ZonedDateTime.of(2024, 4, 23, 12, 0, 0, 0, ZoneOffset.UTC))

        assertTrue(result.daysToFull in 0.4..0.6, "Expected <1 day to full moon, got ${result.daysToFull}")
    }

    @Test
    fun `imminent new moon is not skipped to next synodic month`() {
        val calculator = MoonCalculator(ZoneOffset.UTC)

        val result = calculator.now(ZonedDateTime.of(2024, 4, 8, 12, 0, 0, 0, ZoneOffset.UTC))

        assertTrue(result.daysToNew in 0.2..0.3, "Expected <1 day to new moon, got ${result.daysToNew}")
    }

    @Test
    fun `wobble subtracts the artwork's canonical limb orientation`() {
        // Berlin, north hemisphere, waxing crescent: the base artwork draws the
        // lit limb on the right (90deg), so the residual tilt is the true
        // bright-limb angle minus 90deg, not the absolute angle.
        val now = ZonedDateTime.of(2024, 4, 13, 20, 0, 0, 0, ZoneOffset.UTC)
        val latitude = 52.5
        val longitude = 13.4
        val calculator = MoonCalculator(
            zoneId = ZoneOffset.UTC,
            wobbleEnabled = true,
            latitudeDeg = latitude,
            longitudeDeg = longitude,
            hemisphere = Hemisphere.NORTHERN,
        )

        val result = calculator.now(now)
        val trueLimb = -(MoonIllumination.compute()
            .on(now)
            .at(latitude, longitude)
            .execute()
            .angle - MoonPosition.compute()
            .on(now)
            .at(latitude, longitude)
            .execute()
            .parallacticAngle).toFloat()
        val expected = normalizeSignedDegrees(trueLimb - 90f).coerceIn(-90f, 90f)

        assertEquals(expected, result.wobbleDeg, 0.001f)
    }

    @Test
    fun `wobble applies only residual tilt for a near-zenith-lit gibbous moon`() {
        // Amsterdam 2026-09-22 21:00 local (19:00 UTC), waxing gibbous ~84%.
        // The lit limb is near the zenith, so the artwork needs almost no
        // rotation (~+3deg) instead of the previous ~90deg error.
        val now = ZonedDateTime.of(2026, 9, 22, 19, 0, 0, 0, ZoneOffset.UTC)
        val calculator = MoonCalculator(
            zoneId = ZoneOffset.UTC,
            wobbleEnabled = true,
            latitudeDeg = 52.3676,
            longitudeDeg = 4.9041,
            hemisphere = Hemisphere.NORTHERN,
        )

        val result = calculator.now(now)

        assertEquals(MoonPhase.WAXING_GIBBOUS, result.phase)
        assertTrue(
            kotlin.math.abs(result.wobbleDeg) < 15f,
            "Expected a small residual tilt, got ${result.wobbleDeg}",
        )
    }

    @Test
    fun `wobble mirrors the base orientation in the south hemisphere`() {
        // Sydney, south hemisphere, waning crescent: the base artwork draws the
        // lit limb on the right (90deg) for waning-south, so the residual is
        // again true limb minus 90deg.
        val now = ZonedDateTime.of(2024, 4, 13, 10, 0, 0, 0, ZoneOffset.UTC)
        val latitude = -33.87
        val longitude = 151.21
        val calculator = MoonCalculator(
            zoneId = ZoneOffset.UTC,
            wobbleEnabled = true,
            latitudeDeg = latitude,
            longitudeDeg = longitude,
            hemisphere = Hemisphere.SOUTHERN,
        )

        val result = calculator.now(now)
        val trueLimb = -(MoonIllumination.compute()
            .on(now)
            .at(latitude, longitude)
            .execute()
            .angle - MoonPosition.compute()
            .on(now)
            .at(latitude, longitude)
            .execute()
            .parallacticAngle).toFloat()
        val base = if (result.phaseFraction >= 0.5) 90f else 270f
        val expected = normalizeSignedDegrees(trueLimb - base).coerceIn(-90f, 90f)

        assertEquals(expected, result.wobbleDeg, 0.001f)
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
