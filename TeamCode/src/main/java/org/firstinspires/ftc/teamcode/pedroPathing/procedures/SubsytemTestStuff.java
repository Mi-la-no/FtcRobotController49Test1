package org.firstinspires.ftc.teamcode.pedroPathing.procedures;

import com.pedropathing.tuning.autotune.Inputs;
import com.pedropathing.tuning.autotune.Procedure;

public class SubsytemTestStuff extends Procedure {
    public SubsytemTestStuff() {
        super("SubsystemTesting", "A way to make Subsystems?");
    }

    @Override
    public void run() throws InterruptedException {
        Inputs motorNames = inputs("Mecanum Motor Names", "Enter the names in HardwareMap of your subsystem motor.");
        Inputs.Field<String> motorName = motorNames.s("Motor Count");
        awaitInputs(motorNames);
        


    }
}
