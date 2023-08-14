package edu.ncssm.ftc.electricmayhem.samples.subsystems

import com.qualcomm.robotcore.hardware.HardwareMap
import subsystems.Subsystem

class Robot(hardwareMap: HardwareMap) : Subsystem() {
    val turret = Turret(hardwareMap, "turretMotor")
    init {
        subsystems.add(turret)
    }

}

