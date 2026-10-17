package org.firstinspires.ftc.teamcode;

import static com.pedropathing.api.Paths.*;
import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Subsystems.Intake.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Scoring.ScoringIntakeSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.Scoring.ScoringSubsystem;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous(name = "AutoPath", group = "Autonomous")
public class AutoPath extends LinearOpMode {

    private Follower follower;
    private ScoringSubsystem shooter;
    private Intake intake;
    private ScoringIntakeSubsystem scoringIntake;

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(59.8315, 9.7647, 90);
    private final Pose path1 = poseFactory.of(8.5831, 23.9573, 270);
    private final Pose point2Start = poseFactory.of(8.5831, 23.9573, 270);
    private final Pose point2 = poseFactory.of(8.7859, 8.739, 270);
    private final Pose point3Start = poseFactory.of(8.7859, 8.739, 270);
    private final Pose point3 = poseFactory.of(59.3186, 128.9011, 270);
    private final Pose point3Control1 = poseFactory.of(10.7296, 73.4216, 0);
    private final Pose point4Start = poseFactory.of(59.3186, 128.9011, 270);
    private final Pose point4 = poseFactory.of(6.0654, 103.5948, 180);
    private final Pose point5Start = poseFactory.of(6.0654, 103.5948, 180);
    private final Pose point5 = poseFactory.of(59.0637, 128.5825, 270);
    private final Pose point6Start = poseFactory.of(59.0637, 128.5825, 270);
    private final Pose point6 = poseFactory.of(5.6487, 103.5074, 180);

    // Autonomous routine
    public Command autoRoutine() {
        return sequential(
                shootStart(),
                intakeBalls(),
                waitMs(500),
                shootBalls(),
                waitMs(1000),
                stopShoot(),
                follow(follower, path1()),
                follow(follower, path2())
                //follow(follower, path3()),
                //follow(follower, path4()),
                //follow(follower, path5()),
                //follow(follower, path6())
        );
    }

    private Command shootStart() {
        return Command.build()
                .setExecute(() -> {
                    shooter.setShooterState(ScoringSubsystem.ShooterState.FORWARD);
                })
                .setDone(() -> {
                    return true;
                })
                .requiring(shooter);
    }
    private Command intakeBalls() {
        return Command.build()
                .setExecute(() -> {
                    intake.setState(Intake.IntakeState.BACKWARD);
                })
                .setDone(() -> {
                    return true;
                })
                .requiring(intake);
    }

    private Command shootBalls(){
        return Command.build()
                .setExecute(() -> {
                    scoringIntake.setScoringIntakeState(ScoringIntakeSubsystem.ScoringIntakeState.SHOOT);
                })
                .setDone(() -> {
                    return true;
                })
                .requiring(scoringIntake);
    }

    private Command stopShoot(){
        return Command.build()
                .setExecute(() -> {
                    scoringIntake.setScoringIntakeState(ScoringIntakeSubsystem.ScoringIntakeState.INIT);
                })
                .setDone(() -> {
                    return true;
                })
                .requiring(scoringIntake);
    }


    @Override
    public void runOpMode() {
        Scheduler.reset();
        follower = Constants.create(hardwareMap);
        follower.setPose(start);
        follower.update();

        waitForStart();
        schedule(autoRoutine());

        while (opModeIsActive()) {
            follower.update();
            Scheduler.execute();

            telemetry.addData("x", follower.pose().x());
            telemetry.addData("y", follower.pose().y());
            telemetry.addData("heading", follower.pose().heading());

            if (follower.currentPath() != null) {
                telemetry.addData("Current path distance remaining", follower.distanceToEndpoint());
                telemetry.addData("Path number", follower.pathIndex());
            }

            telemetry.update();
        }
    }

    public Path path1() {
        return Paths.line(start, path1).linear(start, path1);
    }

    public Path path2() {
        return Paths.line(point2Start, point2).linear(point2Start, point2);
    }



    /*public Path path3() {
        return Paths.curve(point2, point3Control1, point3).tangent();
    }

    /*public Path path4() {
        return Paths.curve(point3, point4Control1, point4).tangent();
    }

    public Path path5() {
        return Paths.line(point4, point5).tangent();
    }

    public Path path6() {
        return Paths.line(point5, point6).reverseTangent();
    }*/
}

