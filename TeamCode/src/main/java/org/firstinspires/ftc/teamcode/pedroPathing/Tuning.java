package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;

import org.firstinspires.ftc.teamcode.pedroPathing.procedures.MecanumTuner;
import org.firstinspires.ftc.teamcode.pedroPathing.procedures.Tests;

public class Tuning {
    // Tuners go here
    @Tuner
    public static Procedure tests() {
        return new Tests(hardwareMap -> new Mecanum(hardwareMap, Constants.driveConfig), null, null);
    }
    @Tuner
    public static Procedure mecanumTuner(){
        return new MecanumTuner();
    }
}