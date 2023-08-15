package edu.ncssm.ftc.electric_mayhem.core.sensors.data

data class SensorData<T>(val value : T, val timestamp : Long = System.nanoTime())
