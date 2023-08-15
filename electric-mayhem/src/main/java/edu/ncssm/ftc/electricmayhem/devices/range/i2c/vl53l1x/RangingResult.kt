package edu.ncssm.ftc.electricmayhem.devices.range.i2c.vl53l1x

/**
 * Data class representing the result of a ranging operation.
 *
 * @property range The distance measured by the ranging operation, in millimeters.
 * @property roi The region of interest for the ranging operation. Defaults to `VL53L1X.VL53L1X_DEFAULT_ROI`.
 * @property sequence The sequence number of the ranging operation. Defaults to `0`.
 */
data class RangingResult(val range : Int, val roi : ROI = VL53L1X.VL53L1X_DEFAULT_ROI, val sequence : Int = 0) {
    /**
     * Returns a string representation of the `RangingResult` object.
     *
     * @return A string in the format "RangingResult(sequence=sequence range=range mm, roi=roi.name)".
     */
    override fun toString(): String {
        return "RangingResult(sequence=$sequence range=$range mm, roi=${roi.name})"
    }
}
