package dev.maxine.librelune.moon

import kotlin.test.Test
import kotlin.test.assertEquals

class MoonPhaseTest {
    @Test
    fun `octant centers retain their phases across wrapped cycles`() {
        MoonPhase.entries.forEach { phase ->
            for (cycle in -2..2) {
                val age = SYNODIC_MONTH_DAYS * (cycle + phase.ordinal / 8.0)
                assertEquals(phase, MoonPhase.fromAgeDays(age))
            }
        }
    }

    @Test
    fun `octant boundaries retain the original threshold behavior`() {
        val octant = SYNODIC_MONTH_DAYS / 8.0
        for (index in 0..7) {
            val boundary = octant * (index + 0.5)
            for (ageDays in listOf(Math.nextDown(boundary), boundary, Math.nextUp(boundary))) {
                val age = ((ageDays % SYNODIC_MONTH_DAYS) + SYNODIC_MONTH_DAYS) % SYNODIC_MONTH_DAYS
                val expected = when {
                    age < octant / 2 -> MoonPhase.NEW
                    age < octant * 1.5 -> MoonPhase.WAXING_CRESCENT
                    age < octant * 2.5 -> MoonPhase.FIRST_QUARTER
                    age < octant * 3.5 -> MoonPhase.WAXING_GIBBOUS
                    age < octant * 4.5 -> MoonPhase.FULL
                    age < octant * 5.5 -> MoonPhase.WANING_GIBBOUS
                    age < octant * 6.5 -> MoonPhase.THIRD_QUARTER
                    age < octant * 7.5 -> MoonPhase.WANING_CRESCENT
                    else -> MoonPhase.NEW
                }
                assertEquals(expected, MoonPhase.fromAgeDays(ageDays), "ageDays=$ageDays")
            }
        }
    }

    @Test
    fun `non-finite ages retain the new moon fallback`() {
        for (age in listOf(Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)) {
            assertEquals(MoonPhase.NEW, MoonPhase.fromAgeDays(age))
        }
    }
}
