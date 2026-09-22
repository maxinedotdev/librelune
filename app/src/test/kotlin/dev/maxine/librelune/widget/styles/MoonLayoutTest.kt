package dev.maxine.librelune.widget.styles

import androidx.compose.ui.unit.dp
import dev.maxine.librelune.data.Hemisphere
import dev.maxine.librelune.moon.MoonPhase
import dev.maxine.librelune.moon.MoonState
import dev.maxine.librelune.moon.SYNODIC_MONTH_DAYS
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MoonLayoutTest {
    @Test
    fun `northern waxing crescent uses full terminator depth for dark region width`() {
        val layout = moonLayout(
            state = MoonState(
                phase = MoonPhase.WAXING_CRESCENT,
                illuminationPct = 10,
                ageDays = SYNODIC_MONTH_DAYS * 0.1,
                daysToFull = 0.0,
                daysToNew = 0.0,
            ),
            hemisphere = Hemisphere.NORTHERN,
            widgetWidth = 200.dp,
            moonRadius = 50.dp,
        )

        assertTrue(layout.textOnLeft)
        assertEquals(140.dp, layout.darkRegionWidth)
    }

    @Test
    fun `southern waxing crescent mirrors full terminator depth onto the opposite side`() {
        val layout = moonLayout(
            state = MoonState(
                phase = MoonPhase.WAXING_CRESCENT,
                illuminationPct = 10,
                ageDays = SYNODIC_MONTH_DAYS * 0.1,
                daysToFull = 0.0,
                daysToNew = 0.0,
            ),
            hemisphere = Hemisphere.SOUTHERN,
            widgetWidth = 200.dp,
            moonRadius = 50.dp,
        )

        assertFalse(layout.textOnLeft)
        assertEquals(140.dp, layout.darkRegionWidth)
    }
}
