package dev.maxine.librelune.data

enum class WidgetStyle { LINE, MATERIAL3, GRAPHICS }
enum class Hemisphere { NORTHERN, SOUTHERN }

data class WidgetSettings(
    val style: WidgetStyle = WidgetStyle.MATERIAL3,
    val showPhaseName: Boolean = true,
    val showIllumination: Boolean = true,
    val showDaysToFull: Boolean = false,
    val showDaysToNew: Boolean = false,
    val hemisphere: Hemisphere = Hemisphere.NORTHERN,
)
