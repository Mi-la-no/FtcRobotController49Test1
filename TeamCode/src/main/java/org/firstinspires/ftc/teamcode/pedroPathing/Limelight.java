
package org.firstinspires.ftc.teamcode.pedroPathing;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.List;

@Autonomous(name = "Pollen Search Auto", group = "Autonomous")
public class Limelight extends LinearOpMode {

    // CHANGE these names to match your Robot Configuration.
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    private Limelight3A limelight;
    private IMU imu;

    // Leave a safety margin before the 30-second period ends.
    private static final double AUTO_LIMIT_SECONDS = 26.0;

    // Search settings
    private static final double SCAN_SAMPLE_SECONDS = 0.40;
    private static final double DRIVE_POWER = 0.25;
    private static final double DRIVE_STEP_SECONDS = 1.20;

    // Turn settings
    private static final double TURN_KP = 0.012;
    private static final double MAX_TURN_POWER = 0.25;
    private static final double MIN_TURN_POWER = 0.10;
    private static final double TURN_TOLERANCE_DEGREES = 3.0;

    private final ElapsedTime autoTimer = new ElapsedTime();

    @Override
    public void runOpMode() {

        // Initialize mecanum motors.
        frontLeft = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRight = hardwareMap.get(DcMotor.class, "front_right_drive");
        backLeft = hardwareMap.get(DcMotor.class, "back_left_drive");
        backRight = hardwareMap.get(DcMotor.class, "back_right_drive");

        // Standard mecanum setup: reverse the left motors.
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Initialize the Control Hub IMU.
        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot orientation =
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.UP,
                        RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
                );

        imu.initialize(new IMU.Parameters(orientation));

        // Use your existing Limelight neural detector pipeline.
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();

        telemetry.addLine("Pollen Search Auto Ready");
        telemetry.addLine("Check motor names and IMU orientation.");
        telemetry.addLine("Press PLAY to begin.");
        telemetry.update();

        waitForStart();

        if (!opModeIsActive()) {
            stopDrive();
            limelight.stop();
            return;
        }

        imu.resetYaw();
        autoTimer.reset();

        // Four headings relative to the robot's starting direction.
        double[] headings = {0.0, 90.0, 180.0, -90.0};

        while (opModeIsActive()
                && autoTimer.seconds() < AUTO_LIMIT_SECONDS) {

            double bestHeading = 0.0;
            double bestScore = -1.0;
            boolean scanSuccessful = true;

            // Scan four directions and measure visible Pollen.
            for (double heading : headings) {

                if (!opModeIsActive()
                        || autoTimer.seconds() >= AUTO_LIMIT_SECONDS) {
                    scanSuccessful = false;
                    break;
                }

                if (!turnToHeading(heading)) {
                    scanSuccessful = false;
                    break;
                }

                // Allow the camera view to settle after turning.
                sleep(100);

                double score = samplePollenScore();

                telemetry.addData("Scanned Heading", heading);
                telemetry.addData("Pollen Score", "%.2f", score);
                telemetry.update();

                if (score > bestScore) {
                    bestScore = score;
                    bestHeading = heading;
                }
            }

            stopDrive();

            if (!scanSuccessful) {
                break;
            }

            if (!opModeIsActive()
                    || autoTimer.seconds() >= AUTO_LIMIT_SECONDS) {
                break;
            }

            // Face the direction with the largest average Pollen count.
            if (!turnToHeading(bestHeading)) {
                break;
            }

            // If nothing was detected, take a shorter exploratory step.
            // Otherwise move toward the most promising visible cluster.
            double stepSeconds =
                    bestScore > 0.0 ? DRIVE_STEP_SECONDS : 0.80;

            driveForwardFor(stepSeconds);

            stopDrive();

            telemetry.addData("Chosen Heading", bestHeading);
            telemetry.addData("Best Pollen Score", "%.2f", bestScore);
            telemetry.addData("Autonomous Time", "%.1f s",
                    autoTimer.seconds());
            telemetry.update();
        }

        stopDrive();
        limelight.stop();
    }

    // Average the number of Pollen detections during a short scan.
    private double samplePollenScore() {

        ElapsedTime sampleTimer = new ElapsedTime();

        double totalCount = 0.0;
        int samples = 0;

        while (opModeIsActive()
                && sampleTimer.seconds() < SCAN_SAMPLE_SECONDS
                && autoTimer.seconds() < AUTO_LIMIT_SECONDS) {

            totalCount += countPollen();
            samples++;

            sleep(50);
        }

        if (samples == 0) {
            return 0.0;
        }

        return totalCount / samples;
    }

    // Count only detections whose class is Pollen.
    // The Limelight's configured threshold remains in effect.
    private int countPollen() {

        LLResult result = limelight.getLatestResult();

        if (result == null || !result.isValid()) {
            return 0;
        }

        List<LLResultTypes.DetectorResult> detections =
                result.getDetectorResults();

        int count = 0;

        for (LLResultTypes.DetectorResult detection : detections) {

            String className = detection.getClassName();

            if (className != null
                    && className.equalsIgnoreCase("pollen")) {
                count++;
            }
        }

        return count;
    }

    // Turn to a heading using IMU feedback.
    private boolean turnToHeading(double targetHeading) {

        ElapsedTime turnTimer = new ElapsedTime();

        while (opModeIsActive()
                && autoTimer.seconds() < AUTO_LIMIT_SECONDS
                && turnTimer.seconds() < 2.5) {

            double currentHeading =
                    imu.getRobotYawPitchRollAngles()
                            .getYaw(AngleUnit.DEGREES);

            double error =
                    normalizeDegrees(targetHeading - currentHeading);

            if (Math.abs(error) <= TURN_TOLERANCE_DEGREES) {
                stopDrive();
                return true;
            }

            double correction =
                    Range.clip(
                            error * TURN_KP,
                            -MAX_TURN_POWER,
                            MAX_TURN_POWER
                    );

            if (Math.abs(correction) < MIN_TURN_POWER) {
                correction = Math.copySign(MIN_TURN_POWER, correction);
            }

            // Negative sign matches the standard mecanum mixing below
            // when the left motors are reversed.
            driveRobotCentric(0.0, 0.0, -correction);

            sleep(10);
        }

        stopDrive();

        return false;
    }

    // Standard mecanum wheel-power mixing.
    private void driveRobotCentric(
            double forward,
            double strafeRight,
            double turn) {

        double fl = forward + strafeRight + turn;
        double fr = forward - strafeRight - turn;
        double bl = forward - strafeRight + turn;
        double br = forward + strafeRight - turn;

        double max = Math.max(
                1.0,
                Math.max(
                        Math.max(Math.abs(fl), Math.abs(fr)),
                        Math.max(Math.abs(bl), Math.abs(br))
                )
        );

        frontLeft.setPower(fl / max);
        frontRight.setPower(fr / max);
        backLeft.setPower(bl / max);
        backRight.setPower(br / max);
    }

    // Move forward for a short, bounded time.
    private void driveForwardFor(double seconds) {

        ElapsedTime moveTimer = new ElapsedTime();

        while (opModeIsActive()
                && moveTimer.seconds() < seconds
                && autoTimer.seconds() < AUTO_LIMIT_SECONDS) {

            driveRobotCentric(DRIVE_POWER, 0.0, 0.0);

            sleep(10);
        }

        stopDrive();
    }

    private double normalizeDegrees(double angle) {

        while (angle > 180.0) {
            angle -= 360.0;
        }

        while (angle <= -180.0) {
            angle += 360.0;
        }

        return angle;
    }

    private void stopDrive() {

        if (frontLeft != null) frontLeft.setPower(0.0);
        if (frontRight != null) frontRight.setPower(0.0);
        if (backLeft != null) backLeft.setPower(0.0);
        if (backRight != null) backRight.setPower(0.0);
    }
}
