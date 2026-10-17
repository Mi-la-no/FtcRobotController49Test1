
package org.firstinspires.ftc.teamcode.pedroPathing;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.List;

@TeleOp(name = "Limelight Testing", group = "Limelight")
public class Limelight extends LinearOpMode {

    private Limelight3A limelight;

    @Override
    public void runOpMode() {

        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        // Use the existing Limelight neural detector pipeline.
        limelight.pipelineSwitch(0);
        limelight.start();

        telemetry.addLine("Limelight ready.");
        telemetry.addLine("Press PLAY to begin.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            LLResult result = limelight.getLatestResult();

            if (result != null && result.isValid()) {

                List<LLResultTypes.DetectorResult> detections =
                        result.getDetectorResults();

                telemetry.addData("Detection Count", detections.size());

                for (int i = 0; i < detections.size(); i++) {

                    LLResultTypes.DetectorResult detection =
                            detections.get(i);

                    telemetry.addData(
                            "Object " + i,
                            "%s | Confidence: %.1f%%",
                            detection.getClassName(),
                            detection.getConfidence()
                    );
                }

            } else {
                telemetry.addLine("No valid detector result.");
            }

            telemetry.addData("Limelight Connected",
                    limelight.isConnected());

            telemetry.update();
        }

        limelight.stop();
    }
}
