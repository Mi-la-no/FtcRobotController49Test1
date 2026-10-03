package org.firstinspires.ftc.teamcode.Subsystems.Drive;

import static com.pedropathing.api.Paths.line;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Subsystems.Robot.MyRobot;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

public class DriveSubsystem extends SubsystemBase {
    private MyRobot robot;
    private Follower follower;
    public boolean fieldOriented = true;
    public boolean autoDriving;
    public ElapsedTime autoTimer = new ElapsedTime();

    public DriveSubsystem(MyRobot robot){
        follower = Constants.create(robot.hardwareMap);
    }

    public void setDrivePower(double leftX, double leftY, double rightX){
        if (autoDriving) {
            if (Math.max(Math.max(Math.abs(leftX), Math.abs(leftY)), Math.abs(rightX)) > 0.2
                    || autoTimer.seconds() >= 4) {
                autoDriving = false;
            } else {
                follower.update();
                if (follower.isBusy()) return;
                autoDriving = false;
            }
        }
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
    public boolean isReady() { return follower != null; }

    public void driveTo(Pose target) {
        if (target == null || follower == null) return;
        Pose start = follower.pose();
        if (start.distance(target) < 0.5) follower.hold(target);
        else follower.follow(line(start, target).linear(start.heading(), target.heading()));
        autoTimer.reset();
        autoDriving = true;
    }

}
