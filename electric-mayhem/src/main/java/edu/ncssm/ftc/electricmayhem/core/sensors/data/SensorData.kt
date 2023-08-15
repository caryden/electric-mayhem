package edu.ncssm.ftc.electricmayhem.core.sensors.data

data class SensorData<T>(val value : T, val timestamp : Long = System.nanoTime())
