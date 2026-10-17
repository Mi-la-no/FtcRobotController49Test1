package org.firstinspires.ftc.teamcode.AutoPaths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
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

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

public class Autopath1 {


    @Autonomous(name = "AutoPath", group = "Autonomous")
    public class AutoPath extends LinearOpMode {

        private Follower follower;

        private final PoseFactory poseFactory = PoseFactory.degrees();

        private final Pose start = poseFactory.of(9.2958, 35.5139, 0);
        private final Pose path1 = poseFactory.of(58.0809, 10.7982, 86);
        private final Pose path1Control1 = poseFactory.of(26.9142, 24.3423, 0);
        private final Pose point2Start = poseFactory.of(58.0809, 10.7982, 86);
        private final Pose point2 = poseFactory.of(8.5831, 23.9573, 270);
        private final Pose point3Start = poseFactory.of(8.5831, 23.9573, 270);
        private final Pose point3 = poseFactory.of(8.7859, 8.739, 270);
        private final Pose point4Start = poseFactory.of(8.7859, 8.739, 270);
        private final Pose point4 = poseFactory.of(59.3186, 128.9011, 270);
        private final Pose point4Control1 = poseFactory.of(10.7296, 73.4216, 0);
        private final Pose point5Start = poseFactory.of(59.3186, 128.9011, 270);
        private final Pose point5 = poseFactory.of(6.0654, 103.5948, 180);
        private final Pose point6Start = poseFactory.of(6.0654, 103.5948, 180);
        private final Pose point6 = poseFactory.of(59.0637, 128.5825, 270);
        private final Pose point7Start = poseFactory.of(59.0637, 128.5825, 270);
        private final Pose point7 = poseFactory.of(5.6487, 103.5074, 180);

        // Autonomous routine
        public Command autoRoutine() {
            return sequential(
                    follow(follower, path1()),
                    follow(follower, path2()),
                    follow(follower, path3()),
                    follow(follower, path4()),
                    follow(follower, path5()),
                    follow(follower, path6()),
                    follow(follower, path7())
            );
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
            return Paths.curve(start, path1Control1, path1).linear(start, path1);
        }

        public Path path2() {
            return Paths.line(point2Start, point2).linear(point2Start, point2);
        }

        public Path path3() {
            return Paths.line(point3Start, point3).linear(point3Start, point3);
        }

        public Path path4() {
            return Paths.curve(point4Start, point4Control1, point4).linear(point4Start, point4);
        }

        public Path path5() {
            return Paths.line(point5Start, point5).linear(point5Start, point5);
        }

        public Path path6() {
            return Paths.line(point6Start, point6).linear(point6Start, point6);
        }

        public Path path7() {
            return Paths.line(point7Start, point7).linear(point7Start, point7);
        }
    }
    }
