package dev.maxine.librelune.data

enum class WidgetStyle { LINE, GRAPHICS }
enum class Hemisphere { NORTHERN, SOUTHERN }

data class WidgetSettings(
    val style: WidgetStyle = WidgetStyle.LINE,
    val showPhaseName: Boolean = true,
    val showIllumination: Boolean = true,
    val showDaysToFull: Boolean = false,
    val showDaysToNew: Boolean = false,
    val hemisphere: Hemisphere = Hemisphere.NORTHERN,
    val iconPaddingDp: Int = 6,
)
