package dev.maxine.librelune.widget

import dev.maxine.librelune.moon.MoonPhase
import dev.maxine.librelune.moon.MoonState

object MoonRenderPhase {
    fun fromState(state: MoonState): MoonPhase {
        return state.phase
    }
}
