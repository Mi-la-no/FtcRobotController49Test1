package org.firstinspires.ftc.teamcode.pedroPathing;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class RandomTeleop {
    //Field Oriented

    // 1. Retrieve the robot's current heading from the IMU
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
frontLeft.setPower(frontLeftPower);
backLeft.setPower(backLeftPower);
frontRight.setPower(frontRightPower);
backRight.setPower(backRightPower);

}
