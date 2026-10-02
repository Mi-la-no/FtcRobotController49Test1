package org.firstinspires.ftc.teamcode.Subsystems.Scoring;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.Subsystems.Robot.MyRobot;

@Configurable
public class ScoringIntakeSubsystem extends SubsystemBase {
    public DcMotor IntakeBack;
    public double INIT = 0;
    public static double SHOOT = -1;


    public enum ScoringIntakeState{
        INIT,
        SHOOT
    }

    public ScoringIntakeSubsystem(MyRobot robot){
        this.IntakeBack = robot.hardwareMap.get(DcMotor.class, "IntakeBack");
    }


    public void setScoringIntakeState (ScoringIntakeState state) {
        double vel = 0;
        switch (state) {
            case INIT:
                vel = INIT;
                break;
            case SHOOT:
                vel = SHOOT;
        }
        IntakeBack.setPower(vel);
    }
}
