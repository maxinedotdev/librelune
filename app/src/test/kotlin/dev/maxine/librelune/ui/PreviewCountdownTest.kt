package dev.maxine.librelune.ui

import dev.maxine.librelune.moon.SYNODIC_MONTH_DAYS
import kotlin.test.Test
import kotlin.test.assertEquals

class PreviewCountdownTest {
    @Test
    fun `preview countdown retains wrapping and event boundaries`() {
        val fractions = listOf(
            0.0,
            Float.MIN_VALUE.toDouble(),
            0.25,
            Math.nextDown(0.5),
            0.5,
            Math.nextUp(0.5),
            0.75,
            Math.nextDown(1.0),
            1.0,
        )
        for (current in fractions) {
            for (target in listOf(0.0, 0.5)) {
                val expected = if (target >= current) {
                    target - current
                } else {
                    1.0 - (current - target)
                }
                assertEquals(
                    expected * SYNODIC_MONTH_DAYS,
                    daysUntilTarget(current, target),
                    "current=$current, target=$target",
                )
            }
        }
    }
}
