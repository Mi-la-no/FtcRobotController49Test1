package org.firstinspires.ftc.teamcode.Subsystems.Intake;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Subsystems.Robot.MyRobot;

public class Intake extends SubsystemBase{
    public DcMotor IntakeFront;
    public CRServo IntakeServo;
    public double INIT = 0;
    public double FORWARD = 1;
    public double BACKWARD = -1;

    public enum IntakeState{
        INIT,
        FORWARD,
        BACKWARD
    }

    public Intake (MyRobot robot){
        this.IntakeFront = robot.hardwareMap.get(DcMotor.class, "IntakeFront");
        this.IntakeServo = robot.hardwareMap.get(CRServo.class, "IntakeServo");
        setState(IntakeState.INIT);
    }

    public void setState (IntakeState state) {
        double vel = 0;
        switch (state) {
            case INIT:
                vel = INIT;
                break;
            case FORWARD:
                vel = FORWARD;
                break;
            case BACKWARD:
                vel = BACKWARD;
                break;
        }
        IntakeFront.setPower(vel);
        IntakeServo.setPower(vel);
    }
}
