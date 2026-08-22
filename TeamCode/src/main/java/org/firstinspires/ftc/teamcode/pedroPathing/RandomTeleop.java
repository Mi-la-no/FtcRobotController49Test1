package org.firstinspires.ftc.teamcode.pedroPathing;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp
public class RandomTeleop extends LinearOpMode {
    //Field Oriented

    // 1. Retrieve the robot's current heading from the IMU
    public IMU imu;

    public Follower follower;
    double robotHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

    // 2. Read raw joystick inputs from gamepad
    double y = -gamepad1.left_stick_y; // Remember, Y stick is reversed on gamepads
    double x = gamepad1.left_stick_x;
    double rx = gamepad1.right_stick_x; // Used for turning

    // 3. Rotate the joystick vector by the negative robot heading
    double rotX = x * Math.cos(-robotHeading) - y * Math.sin(-robotHeading);
    double rotY = x * Math.sin(-robotHeading) + y * Math.cos(-robotHeading);

    // 4. Denominator is the largest motor power (absolute value) to maintain proportionality
    double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1.0);

    // 5. Apply the rotated vector to the standard mecanum motor power formulas
    double frontLeftPower = (rotY + rotX + rx) / denominator;
    double backLeftPower = (rotY - rotX + rx) / denominator;
    double frontRightPower = (rotY - rotX - rx) / denominator;
    double backRightPower = (rotY + rotX - rx) / denominator;

// 6. Set power to the hardware motors

    @Override
    public void runOpMode() throws InterruptedException {
        follower = Constants.createFollower(hardwareMap);
        follower.update();

        waitForStart();
        follower.startTeleopDrive();
        while(isStarted()){
            follower.setTeleOpDrive(
                    -gamepad1.left_stick_y,
                    -gamepad1.left_stick_x,
                    -gamepad1.right_stick_x,
                    false
            );
        }
    }



}
