package edu.ncssm.ftc.electric_mayhem.devices.range.i2c.vl53l1x

/**
 * Enum representing the distance modes of the VL53L1X sensor.
 */
enum class DistanceMode {
    /**
     * Short range mode: For distances up to 1.3 meters.
     * This mode has the highest precision and the shortest measurement time.
     */
    SHORT,

    /**
     * Long range mode: For distances up to 4 meters.
     * This mode has lower precision but can measure longer distances.
     */
    LONG
}