package org.firstinspires.ftc.teamcode.pedroPathing.procedures;

import com.pedropathing.tuning.autotune.Display;
import com.pedropathing.tuning.autotune.Inputs;
import com.pedropathing.tuning.autotune.Procedure;

public class SubsytemTestStuff extends Procedure {
    public SubsytemTestStuff() {
        super("SubsystemTesting", "A way to make Subsystems?");
    }
    static Inputs.Field<String>[] names;
    @Override
    public void run() throws InterruptedException {
        Inputs motorCount = inputs("Mecanum Motor Names", "Enter the names in HardwareMap of your subsystem motor.");
        Inputs.Field<Integer> count = motorCount.i("Motor Count");
        awaitInputs(motorCount);

        Inputs motorNames = inputs("Mecanum Motor Names", "Enter the names in HardwareMap of your subsystem motor.");
        for (int i = 0; i > count.get(); i++){
            names[i] = motorNames.s("Motor " + i + " Name");
        }
        awaitInputs(motorNames);

        confirmation("Motor Directions", "Each motor will spin, one at a time. After each one, you will enter whether it spun forward or reversed. You may use the interactive diagram to see which wheel should be spinning and which direction is forward.");


    }
    private Direction testMotor(Display.FourWheelBot.Wheel wheel, String displayName, String hardwareName) throws InterruptedException {
        final boolean[] correctMotor = new boolean[1];
        final Direction[] direction = new Direction[1];

        withDisplay(Display.fourWheelBot(wheel, false), () -> {
            runOpMode(new SpinMotor(displayName, hardwareName));

            Inputs inputs = inputs(displayName, "Determine the " + displayName.toLowerCase() + " motor direction.");
            Inputs.Field<Boolean> correctMotorField = inputs.b("Did the " + displayName.toLowerCase() + " motor spin?").withDefault(true);
            Inputs.Field<Direction> directionField = inputs.e("Which way did the motor spin?", Direction.class);
            awaitInputs(inputs);

            correctMotor[0] = correctMotorField.get();
            direction[0] = directionField.get();
        });

        if (!correctMotor[0])
            abort("The wrong motor spun. Check that your motors are plugged into the correct ports, and that they are configured correctly. Then, try again.");

        return direction[0];
    }
}
