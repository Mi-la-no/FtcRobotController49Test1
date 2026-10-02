package org.firstinspires.ftc.teamcode.Subsystems.Robot;

import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.ParallelCommandGroup;
import com.arcrobotics.ftclib.command.Robot;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.button.Button;
import com.arcrobotics.ftclib.command.button.GamepadButton;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Subsystems.Drive.DriveCommand.DefaultDriveCommand;
import org.firstinspires.ftc.teamcode.Subsystems.Drive.DriveCommand.SlowModeCommand;
import org.firstinspires.ftc.teamcode.Subsystems.Drive.DriveSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.Intake.IntakeState;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeCommands.ActuateIntakeCommand;
import org.firstinspires.ftc.teamcode.Subsystems.Scoring.ScoringCommands.SpinScoringIntakeCommand;
import org.firstinspires.ftc.teamcode.Subsystems.Scoring.ScoringCommands.SpinShooterCommand;
import org.firstinspires.ftc.teamcode.Subsystems.Scoring.ScoringIntakeSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.Scoring.ScoringSubsystem;

public class MyRobot extends Robot {
    //DriveSubsystem drive;
    Intake intake;
    ScoringSubsystem shooter;
    ScoringIntakeSubsystem scoringIntake;
    DefaultDriveCommand defaultDriveCommand;
    SlowModeCommand slowModeCommand;

    public LinearOpMode opMode;

    public HardwareMap hardwareMap;
    public Telemetry telemetry;

    public GamepadEx driver;
    public GamepadEx operator;

    public enum TeleopMode {
        RED,
        BLUE
    }
    public MyRobot (LinearOpMode opMode, TeleopMode mode) {
        this.opMode = opMode;
        this.hardwareMap = opMode.hardwareMap;
        this.telemetry = opMode.telemetry;
        this.driver = new GamepadEx(opMode.gamepad1);
        this.operator = new GamepadEx(opMode.gamepad2);
        initTele(mode);
    }

    public void initTele (TeleopMode mode){
        if (mode == TeleopMode.RED){
            //drive = new DriveSubsystem(this);
            intake = new Intake(this);
            shooter = new ScoringSubsystem(this);
            scoringIntake = new ScoringIntakeSubsystem(this);

//            defaultDriveCommand = new DefaultDriveCommand(drive,
//                    driver::getLeftX,
//                    driver::getLeftY,
//                    driver::getRightX
//            );
//
//            slowModeCommand = new SlowModeCommand(drive,
//                    driver::getLeftX,
//                    driver::getLeftY,
//                    driver::getRightX
//            );

            //CommandScheduler.getInstance().setDefaultCommand(drive, defaultDriveCommand);
            CommandScheduler.getInstance().setDefaultCommand(shooter, new SpinShooterCommand(shooter, ScoringSubsystem.ShooterState.FORWARD));
            Button driverIntakeForward = new GamepadButton(driver, GamepadKeys.Button.DPAD_UP);
            Button driverIntakeBackward = new GamepadButton(driver, GamepadKeys.Button.DPAD_DOWN);
            Button driverSlowMode = new GamepadButton(driver, GamepadKeys.Button.RIGHT_BUMPER);
            Button shooterStart = new GamepadButton(driver, GamepadKeys.Button.Y);
            Button shooterReverse = new GamepadButton(driver, GamepadKeys.Button.A);
            Button shooterStop = new GamepadButton(driver, GamepadKeys.Button.X);

            /*driverSlowMode
                    .whenPressed(slowModeCommand)
                    .whenReleased(defaultDriveCommand);*/

            driverIntakeForward
                    .whenPressed(new ActuateIntakeCommand(intake, IntakeState.FORWARD))
                    .whenReleased(new ActuateIntakeCommand(intake, IntakeState.INIT));

            driverIntakeBackward
                    .whenPressed(new ActuateIntakeCommand(intake, IntakeState.BACKWARD))
                    .whenReleased(new ActuateIntakeCommand(intake, IntakeState.INIT));

            shooterStart
                    .whenPressed(
                            new ParallelCommandGroup
                                    (
                                            new SpinScoringIntakeCommand(scoringIntake, ScoringIntakeSubsystem.ScoringIntakeState.SHOOT),
                                            new ActuateIntakeCommand(intake, IntakeState.BACKWARD)
                                    ));

            shooterReverse
                    .whenPressed(new SpinShooterCommand(shooter, ScoringSubsystem.ShooterState.BACKWARD));

            shooterStop
                    .whenPressed(new ParallelCommandGroup
                            (
                                    new SpinScoringIntakeCommand(scoringIntake, ScoringIntakeSubsystem.ScoringIntakeState.INIT),
                                    new ActuateIntakeCommand(intake, IntakeState.BACKWARD)
                            ));
        }
    }

}
