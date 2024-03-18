package edu.ncssm.ftc.electricmayhem.core.util

import kotlin.math.abs

infix fun Double.epsilonEquals(d: Double): Boolean {
    val epsilon = 1e-10
    return abs(d - this) <= epsilon
}