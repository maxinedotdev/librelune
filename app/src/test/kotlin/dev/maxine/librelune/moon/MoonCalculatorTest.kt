package dev.maxine.librelune.moon

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
    fun `wobble uses normalized bright limb angle instead of 25 degree clamp`() {
        val now = ZonedDateTime.of(2024, 4, 23, 12, 0, 0, 0, ZoneOffset.UTC)
        val latitude = 52.5
        val longitude = 13.4
        val calculator = MoonCalculator(
            zoneId = ZoneOffset.UTC,
            wobbleEnabled = true,
            latitudeDeg = latitude,
            longitudeDeg = longitude,
        )

        val result = calculator.now(now)
        val raw = -(MoonIllumination.compute()
            .on(now)
            .at(latitude, longitude)
            .execute()
            .angle - MoonPosition.compute()
            .on(now)
            .at(latitude, longitude)
            .execute()
            .parallacticAngle).toFloat()
        val expected = raw.let { angle ->
            val wrapped = angle % 360f
            when {
                wrapped <= -180f -> wrapped + 360f
                wrapped > 180f -> wrapped - 360f
                else -> wrapped
            }
        }.coerceIn(-90f, 90f)

        assertEquals(expected, result.wobbleDeg, 0.001f)
        assertTrue(result.wobbleDeg > 25f)
    }
}
