package edu.ncssm.ftc.electric_mayhem.devices.range.i2c.vl53l0x

enum class RangeSequenceStep(val value: Int) {
    TCC(0x10),  // Target CentreCheck
    MSRC(0x04),  // Minimum Signal Rate Check
    DSS(0x28),  // Dynamic SPAD selection
    PRE_RANGE(0x40),
    FINAL_RANGE(0x80)
}