package edu.ncssm.ftc.electricmayhem.core.sensors.data

sealed class ButtonState {
    companion object {
        fun fromBoolean(value: Boolean) : ButtonState {
            return if (value) Pressed else Released
        }
    }
    data object Pressed : ButtonState()
    data object Released : ButtonState()
}