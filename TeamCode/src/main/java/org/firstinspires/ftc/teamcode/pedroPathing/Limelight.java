
package org.firstinspires.ftc.teamcode.pedroPathing;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.List;

@TeleOp(name = "Limelight Testing", group = "Limelight")
public class Limelight extends LinearOpMode {

    private static final double MIN_CONFIDENCE = 30.0;
    private static final double REQUIRED_SECONDS = 1.25;

    private Limelight3A limelight;
    private final ElapsedTime pollenTimer = new ElapsedTime();

    private boolean timerStarted = false;
    private boolean pollenConfirmed = false;

    @Override
    public void runOpMode() {

        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        limelight.pipelineSwitch(0);
        limelight.start();

        telemetry.addLine("Limelight ready.");
        telemetry.addLine("Press PLAY to begin.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            LLResult result = limelight.getLatestResult();
            boolean validPollen = false;
            double bestConfidence = 0.0;

            if (result != null && result.isValid()) {

                List<LLResultTypes.DetectorResult> detections =
                        result.getDetectorResults();

                for (LLResultTypes.DetectorResult detection : detections) {

                    String className = detection.getClassName();
                    double confidence = detection.getConfidence();

                    if (className != null
                            && className.equalsIgnoreCase("pollen")
                            && confidence >= MIN_CONFIDENCE) {

                        validPollen = true;
                        bestConfidence = Math.max(
                                bestConfidence, confidence);
                    }
                }
            }

            if (validPollen) {

                if (!timerStarted) {
                    pollenTimer.reset();
                    timerStarted = true;
                }

                pollenConfirmed =
                        pollenTimer.seconds() >= REQUIRED_SECONDS;

            } else {
                timerStarted = false;
                pollenConfirmed = false;
            }

            telemetry.addData("Limelight Connected",
                    limelight.isConnected());
            telemetry.addData("Valid Pollen", validPollen);
            telemetry.addData("Best Confidence",
                    "%.1f%%", bestConfidence);
            telemetry.addData("Detection Time",
                    "%.2f / %.2f seconds",
                    timerStarted ? pollenTimer.seconds() : 0.0,
                    REQUIRED_SECONDS);
            telemetry.addData("POLLEN CONFIRMED",
                    pollenConfirmed ? "YES" : "NO");

            telemetry.update();
        }

        limelight.stop();
    }
}
