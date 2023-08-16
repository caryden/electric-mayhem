package edu.ncssm.ftc.electricmayhem.samples.opmodes

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import edu.ncssm.ftc.electricmayhem.samples.subsystems.Robot

class FiniteStateMachineSample : LinearOpMode() {
    override fun runOpMode() {
       Robot(hardwareMap, gamepad1, gamepad2).use { robot ->
           waitForStart()
           robot.start()
           while (opModeIsActive()) {
               telemetry.addData("Robot State", robot.stateMachine.currentState.toString())
               telemetry.update()
           }
       }
    }
}