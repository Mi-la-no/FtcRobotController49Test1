package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.control.PredictiveBrakingCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.ftc.localization.constants.ThreeWheelIMUConstants;
import com.pedropathing.ftc.localization.constants.TwoWheelConstants;
import com.pedropathing.paths.PathConstraints;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
        public static FollowerConstants followerConstants = new FollowerConstants()
                .centripetalScaling(0.00049)
                .predictiveBrakingCoefficients(new PredictiveBrakingCoefficients(0.1, 0.14545534333112456, 0.0018298820244976623))
                .headingPIDFCoefficients(new PIDFCoefficients(1.025, 0, 0.02, 0.01))
                .forwardZeroPowerAcceleration(-30.843474753693915)
                .lateralZeroPowerAcceleration(-47.80266113804416)
                .translationalPIDFCoefficients(new PIDFCoefficients(.35, 0, 0.02, 0.01))
                .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.025, 0, 0.001, 0.6, 0.02));

        public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

        public static PinpointConstants localizerConstants = new PinpointConstants()
                .forwardPodY(3.326888437346213)
                .strafePodX(0.927495520884591)
                .distanceUnit(DistanceUnit.INCH)
                .hardwareMapName("pinpoint")
                .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
                .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
                .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        public static MecanumConstants driveConstants = new MecanumConstants()
                .maxPower(1)
                .rightFrontMotorName("rf")
                .rightRearMotorName("rr")
                .leftRearMotorName("lr")
                .leftFrontMotorName("lf")
                .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
                .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
                .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
                .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
                .xVelocity(78.50367700772021)
                .yVelocity(61.65460541492372);


        public static Follower createFollower(HardwareMap hardwareMap) {
            return new FollowerBuilder(followerConstants, hardwareMap)
                    .pathConstraints(pathConstraints)
                    .mecanumDrivetrain(driveConstants)
                    .pinpointLocalizer(localizerConstants)
                    .build();
        }
    }

