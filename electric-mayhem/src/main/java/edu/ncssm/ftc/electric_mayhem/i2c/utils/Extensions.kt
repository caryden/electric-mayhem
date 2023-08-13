package edu.ncssm.ftc.electric_mayhem.i2c.utils

infix fun Byte.shr(bits: Int): Byte {
    return (this.toInt() shr bits).toByte()
}
infix fun Byte.shl(bits: Int): Byte {
    return ((this.toInt() shl bits) and 0xFF).toByte()
}
infix fun Short.shr(bits: Int): Short {
    return (this.toInt() shr bits).toShort()
}
infix fun Short.shl(bits: Int): Short {
    return ((this.toInt() shl bits) and 0xFFFF).toShort()
}

