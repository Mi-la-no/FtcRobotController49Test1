package org.firstinspires.ftc.teamcode.AutoPaths;

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
public class MYautopath {
    @Autonomous(name = "AutoPath", group = "Autonomous")
    public class AutoPath extends LinearOpMode {

        private Follower follower;

        private final PoseFactory poseFactory = PoseFactory.degrees();

        private final Pose start = poseFactory.of(3.5391, 34.4711, 180);
        private final Pose path1 = poseFactory.of(60.4022, 34.2169, 90);
        private final Pose point2 = poseFactory.of(8.4226, 6.7832, -102.0676);
        private final Pose point2Control1 = poseFactory.of(15.9405, 43.0374, 0);
        private final Pose point3 = poseFactory.of(56.5519, 34.2169, 88.7872);
        private final Pose point3Control1 = poseFactory.of(56.2296, 7.6505, 0);
        private final Pose point4 = poseFactory.of(58.477, 97.9881, 106.8223);
        private final Pose point4Control1 = poseFactory.of(74, 47, 0);
        private final Pose point5 = poseFactory.of(58.7177, 20.0188, -89.8232);
        private final Pose point6 = poseFactory.of(57.9957, 108.5766, -89.5329);

        // Autonomous routine
        public Command autoRoutine() {
            return sequential(
                    follow(follower, path1()),
                    follow(follower, path2()),
                    follow(follower, path3()),
                    follow(follower, path4()),
                    follow(follower, path5()),
                    follow(follower, path6())
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
            return Paths.line(start, path1).linear(start, path1);
        }

        public Path path2() {
            return Paths.curve(path1, point2Control1, point2).tangent();
        }

        public Path path3() {
            return Paths.curve(point2, point3Control1, point3).tangent();
        }

        public Path path4() {
            return Paths.curve(point3, point4Control1, point4).tangent();
        }

        public Path path5() {
            return Paths.line(point4, point5).tangent();
        }

        public Path path6() {
            return Paths.line(point5, point6).reverseTangent();
        }
    }
}
