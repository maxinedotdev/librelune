package dev.maxine.librelune.moon

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
    }
}
