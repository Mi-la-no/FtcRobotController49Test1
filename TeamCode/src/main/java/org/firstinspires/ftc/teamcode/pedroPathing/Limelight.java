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

    private Limelight3A limelight;

    // =========================
    // SETTINGS
    // =========================

    // Test 1: 95% confidence
    private static final double MIN_CONFIDENCE = 0.95;

    // Test 2: Must stay valid for 5 seconds
    private static final double REQUIRED_TIME = 5.0;

    // Test 3: Width / Height ratio
    // Starting range - we can adjust this after testing.
    private static final double MIN_RATIO = 0.65;
    private static final double MAX_RATIO = 1.35;

    private ElapsedTime pollenTimer = new ElapsedTime();

    private boolean countingPollen = false;
    private boolean pollenConfirmed = false;

    @Override
    public void runOpMode() throws InterruptedException {

        // Connect to Limelight
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        // Pipeline 0 = Neural Detector
        limelight.pipelineSwitch(0);

        // Start receiving Limelight data
        limelight.start();

        telemetry.addLine("Limelight connected!");
        telemetry.addLine("Pipeline 0 = Neural Detector");
        telemetry.addLine("Press PLAY to begin.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            LLResult result = limelight.getLatestResult();

            // These are just for telemetry
            double bestConfidence = 0;
            double bestWidth = 0;
            double bestHeight = 0;
            double bestRatio = 0;

            boolean validPollenCandidate = false;

            if (result != null && result.isValid()) {

                telemetry.addData(
                        "TX",
                        "%.2f degrees",
                        result.getTx()
                );

                telemetry.addData(
                        "TY",
                        "%.2f degrees",
                        result.getTy()
                );

                List<LLResultTypes.DetectorResult> detectorResults =
                        result.getDetectorResults();

                telemetry.addData(
                        "Detections",
                        detectorResults.size()
                );

                // Look through all detected objects
                for (LLResultTypes.DetectorResult detector : detectorResults) {

                    String className = detector.getClassName();
                    double confidence = detector.getConfidence();

                    telemetry.addData(
                            "Object",
                            "%s | Confidence: %.1f%%",
                            className,
                            confidence * 100.0
                    );

                    telemetry.addData(
                            "Area",
                            "%.3f",
                            detector.getTargetArea()
                    );

                    // Only evaluate objects classified as pollen
                    if (!className.equalsIgnoreCase("pollen")) {
                        continue;
                    }

                    // =========================
                    // TEST 1
                    // CONFIDENCE >= 95%
                    // =========================

                    if (confidence < MIN_CONFIDENCE) {
                        continue;
                    }

                    // =========================
                    // GET BOUNDING BOX
                    // =========================

                    List<List<Double>> corners =
                            detector.getTargetCorners();

                    if (corners == null || corners.size() < 4) {
                        continue;
                    }

                    double minX = Double.MAX_VALUE;
                    double maxX = -Double.MAX_VALUE;
                    double minY = Double.MAX_VALUE;
                    double maxY = -Double.MAX_VALUE;

                    for (List<Double> corner : corners) {

                        if (corner == null || corner.size() < 2) {
                            continue;
                        }

                        double x = corner.get(0);
                        double y = corner.get(1);

                        minX = Math.min(minX, x);
                        maxX = Math.max(maxX, x);

                        minY = Math.min(minY, y);
                        maxY = Math.max(maxY, y);
                    }

                    double width = maxX - minX;
                    double height = maxY - minY;

                    if (width <= 0 || height <= 0) {
                        continue;
                    }

                    // =========================
                    // TEST 3
                    // WIDTH / HEIGHT RATIO
                    // =========================

                    double ratio = width / height;

                    // Save values for telemetry
                    bestConfidence = confidence;
                    bestWidth = width;
                    bestHeight = height;
                    bestRatio = ratio;

                    // Reject shapes that are too wide or too tall
                    if (ratio < MIN_RATIO || ratio > MAX_RATIO) {
                        continue;
                    }

                    // Passed Test 1 + Test 3
                    validPollenCandidate = true;

                    break;
                }

                // =========================
                // TEST 2
                // 5 SECOND TIMER
                // =========================

                if (validPollenCandidate) {

                    // First valid detection
                    if (!countingPollen) {

                        countingPollen = true;
                        pollenConfirmed = false;
                        pollenTimer.reset();
                    }

                    double elapsed = pollenTimer.seconds();

                    // Has it stayed valid for 5 seconds?
                    if (elapsed >= REQUIRED_TIME) {
                        pollenConfirmed = true;
                    }

                } else {

                    // Something failed:
                    // confidence, shape, object type, etc.
                    //
                    // Reset the timer.
                    countingPollen = false;
                    pollenConfirmed = false;
                    pollenTimer.reset();
                }

            } else {

                // No valid Limelight result
                countingPollen = false;
                pollenConfirmed = false;
                pollenTimer.reset();

                telemetry.addLine("No valid Limelight result");
            }

            // =========================
            // TELEMETRY
            // =========================

            telemetry.addData(
                    "Best Confidence",
                    "%.1f%%",
                    bestConfidence * 100.0
            );

            telemetry.addData(
                    "Width",
                    "%.1f",
                    bestWidth
            );

            telemetry.addData(
                    "Height",
                    "%.1f",
                    bestHeight
            );

            telemetry.addData(
                    "W/H Ratio",
                    "%.2f",
                    bestRatio
            );

            telemetry.addData(
                    "Confidence Requirement",
                    ">= %.0f%%",
                    MIN_CONFIDENCE * 100.0
            );

            telemetry.addData(
                    "Ratio Requirement",
                    "%.2f - %.2f",
                    MIN_RATIO,
                    MAX_RATIO
            );

            if (countingPollen) {

                telemetry.addData(
                        "Pollen Timer",
                        "%.2f / %.2f seconds",
                        pollenTimer.seconds(),
                        REQUIRED_TIME
                );

            } else {

                telemetry.addData(
                        "Pollen Timer",
                        "RESET"
                );
            }

            telemetry.addData(
                    "POLLEN CONFIRMED",
                    pollenConfirmed ? "YES" : "NO"
            );

            telemetry.update();
        }

        limelight.stop();
    }
}