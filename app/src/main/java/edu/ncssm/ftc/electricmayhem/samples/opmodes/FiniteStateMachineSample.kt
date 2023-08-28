package edu.ncssm.ftc.electricmayhem.samples.opmodes

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import edu.ncssm.ftc.electricmayhem.samples.subsystems.RobotWithFSM

class FiniteStateMachineSample : LinearOpMode() {
    override fun runOpMode() {
       RobotWithFSM(hardwareMap, gamepad1, gamepad2).use { robot ->
           waitForStart()
           robot.start()

           while (opModeIsActive()) {
               telemetry.addData("Robot State", robot.behaviorController.currentState.toString())
               telemetry.update()
           }
       }
    }
}