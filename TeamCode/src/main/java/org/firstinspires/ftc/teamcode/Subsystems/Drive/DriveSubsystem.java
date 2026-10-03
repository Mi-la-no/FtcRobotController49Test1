package org.firstinspires.ftc.teamcode.Subsystems.Drive;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Subsystems.Robot.MyRobot;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

public class DriveSubsystem extends SubsystemBase {
    private MyRobot robot;
    private Follower follower;
    public boolean fieldOriented = false;

    public DriveSubsystem(MyRobot robot){
        follower = Constants.create(robot.hardwareMap);
    }

    public void setDrivePower(double leftX, double leftY, double rightX){
        if (fieldOriented) {
            DrivePowers powers = ManualDrive.fieldCentric(
                    leftY,
                    -leftX,
                    -rightX,
                    follower.pose().heading()
            );

            follower.manual(powers);
            follower.update();
        } else {
            follower.manual(
                    leftY,
                    -leftX,
                    -rightX
            );

            follower.update();
        }
    }

    public void setFieldOriented(boolean fieldOriented) {
        this.fieldOriented = fieldOriented;
    }
}
