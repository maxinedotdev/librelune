package dev.maxine.librelune.moon

import dev.maxine.librelune.data.Hemisphere
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MoonCalculatorTest {
    @Test
    fun `known full moon date resolves to full-like phase`() {
        val calculator = MoonCalculator(ZoneOffset.UTC)
        val knownFullMoon = ZonedDateTime.of(2024, 4, 23, 23, 49, 0, 0, ZoneOffset.UTC)

        val result = calculator.now(knownFullMoon)

        assertEquals(MoonPhase.FULL, result.phase)
        assertTrue(result.illuminationPct >= 95)
        assertTrue(result.phaseFraction in 0.49..0.51)
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
    fun `wobble uses observer coordinates and stays within artwork limits`() {
        val now = ZonedDateTime.of(2024, 4, 13, 20, 0, 0, 0, ZoneOffset.UTC)
        val calculator = MoonCalculator(
            zoneId = ZoneOffset.UTC,
            wobbleEnabled = true,
            latitudeDeg = 52.5,
            longitudeDeg = 13.4,
            hemisphere = Hemisphere.NORTHERN,
        )

        val result = calculator.now(now)
        assertTrue(result.wobbleDeg in -90f..90f)
        assertTrue(kotlin.math.abs(result.wobbleDeg) > 0.01f)
    }

    @Test
    fun `wobble stays within artwork limits for a waxing gibbous moon`() {
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
        assertTrue(result.wobbleDeg in -90f..90f)
    }

    @Test
    fun `wobble remains bounded in the south hemisphere`() {
        val now = ZonedDateTime.of(2024, 4, 13, 10, 0, 0, 0, ZoneOffset.UTC)
        val calculator = MoonCalculator(
            zoneId = ZoneOffset.UTC,
            wobbleEnabled = true,
            latitudeDeg = -33.87,
            longitudeDeg = 151.21,
            hemisphere = Hemisphere.SOUTHERN,
        )

        val result = calculator.now(now)
        assertTrue(result.wobbleDeg in -90f..90f)
        assertTrue(kotlin.math.abs(result.wobbleDeg) > 0.01f)
    }

    @Test
    fun `horizontal frame wobble matches reference angles`() {
        data class Case(val time: String, val latitude: Double, val longitude: Double, val north: Float, val south: Float)

        // Reference angles from the previous bright-limb/parallactic-angle formulas.
        val cases = listOf(
            Case("2024-04-01T00:00Z", 52.5, 13.4, -36.096344f, 90f),
            Case("2024-04-13T12:00Z", -33.87, 151.21, 90f, -54.06665f),
            Case("2024-04-08T01:00Z", 0.0, 0.0, -55.570618f, 90f),
            Case("2024-04-23T10:00Z", 0.0, 180.0, -75.7387f, 90f),
            Case("2024-04-15T02:00Z", 90.0, -180.0, -7.0262146f, 90f),
            Case("2024-04-30T11:00Z", -90.0, 180.0, -90f, 10.786774f),
            Case("2024-04-20T13:00Z", 89.999, -179.999, -24.896347f, 90f),
            Case("2024-04-10T15:00Z", -100.0, 220.0, -90f, 24.218506f),
        )

        for (case in cases) {
            for (hemisphere in Hemisphere.entries) {
                val result = MoonCalculator(
                    zoneId = ZoneOffset.UTC,
                    wobbleEnabled = true,
                    latitudeDeg = case.latitude,
                    longitudeDeg = case.longitude,
                    hemisphere = hemisphere,
                ).now(ZonedDateTime.parse(case.time))
                val expected = if (hemisphere == Hemisphere.NORTHERN) case.north else case.south

                assertEquals(expected, result.wobbleDeg, 0.001f, "$case, $hemisphere")
            }
        }
    }

    @Test
    fun `enabling wobble leaves other moon state unchanged`() {
        val now = ZonedDateTime.of(2024, 4, 13, 20, 0, 0, 0, ZoneOffset.UTC)
        val disabled = MoonCalculator(ZoneOffset.UTC, latitudeDeg = 52.5, longitudeDeg = 13.4).now(now)
        val enabled = MoonCalculator(
            ZoneOffset.UTC,
            wobbleEnabled = true,
            latitudeDeg = 52.5,
            longitudeDeg = 13.4,
        ).now(now)

        assertEquals(0f, disabled.wobbleDeg)
        assertEquals(disabled, enabled.copy(wobbleDeg = 0f))
    }
}
