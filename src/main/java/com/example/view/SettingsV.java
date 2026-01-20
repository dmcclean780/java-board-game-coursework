package com.example.view;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class SettingsV {
    
    @FXML
    private Canvas hexCanvas;

    private double r = 75; // hex radius

    private static class Hex {
        double[] xPoints;
        double[] yPoints;
        double distanceToCenter;

        Hex(double x, double y, double r, double centerX, double centerY) {

            xPoints = new double[6];
            yPoints = new double[6];
            for (int i = 0; i < 6; i++) {
                double angleRad = Math.toRadians(60 * i);
                xPoints[i] = x + r * Math.cos(angleRad);
                yPoints[i] = y + r * Math.sin(angleRad);
            }
            double dx = x - centerX;
            double dy = y - centerY;
            distanceToCenter = Math.sqrt(dx * dx + dy * dy);
        }
    }
    private final List<Hex> hexes = new ArrayList<>();
    private WritableImage staticBackground;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Label volumeValueLabel;

    @FXML
    private ComboBox<String> languageCombo;

    @FXML
    private ComboBox<String> textSizeCombo;

    @FXML
    private ComboBox<String> resolutionCombo;

    @FXML
    public void initialize() {
        createHexGrid();
        drawStaticBackground();
        startHexSpiralAnimation();

        // Show integer volume value (0–100)
        volumeValueLabel.textProperty().bind(
            volumeSlider.valueProperty().asString("%.0f")
        );

        // Add language options
        languageCombo.getItems().addAll("English", "Spanish", "French", "German");

        // Add text size options
        textSizeCombo.getItems().addAll("Small", "Medium", "Large");

        // Add resolution options
        resolutionCombo.getItems().addAll("1280x720", "1920x1080", "2560x1440", "3840x2160");

        // Optionally select defaults
        languageCombo.setValue("English");
        textSizeCombo.setValue("Medium");
        resolutionCombo.setValue("1920x1080");
    }

    // Layout of hex grid for the background
    private void createHexGrid() {
        double width = hexCanvas.getWidth();
        double height = hexCanvas.getHeight();

        double hexWidth = Math.sqrt(3) * r;
        double hexHeight = 2 * r;
        double horizSpacing = hexWidth;
        double vertSpacing = hexHeight;

        int cols = (int) Math.ceil(width / horizSpacing) + 2;
        int rows = (int) Math.ceil(height / vertSpacing) + 2;

        double centerX = width / 2;
        double centerY = height / 2;

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                double x = col * horizSpacing;
                double y = row * vertSpacing;
                if (col % 2 == 1) y += vertSpacing / 2;

                hexes.add(new Hex(x, y, r, centerX, centerY));
            }
        }
    }

    // Draw the laid out hexes
    private void drawStaticBackground() {
        double width = hexCanvas.getWidth();
        double height = hexCanvas.getHeight();

        // Draw once to a WritableImage
        staticBackground = new WritableImage((int) width, (int) height);
        GraphicsContext tempGC = new Canvas(width, height).getGraphicsContext2D();

        // Fill background black
        tempGC.setFill(Color.BLACK);
        tempGC.fillRect(0, 0, width, height);

        // Draw hex outlines (static)
        tempGC.setStroke(Color.web("#444"));
        tempGC.setLineWidth(3);
        for (Hex hex : hexes) {
            tempGC.strokePolygon(hex.xPoints, hex.yPoints, 6);
        }

        tempGC.getCanvas().snapshot(null, staticBackground);
    }

    // Gradual colour changes of all hexes
    private void startHexSpiralAnimation() {
        GraphicsContext gc = hexCanvas.getGraphicsContext2D();

        final long startNanoTime = System.nanoTime();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                double t = (now - startNanoTime) / 3e9; // seconds

                // Draw static background
                gc.drawImage(staticBackground, 0, 0);

                // Animate hex fill
                for (Hex hex : hexes) {
                    double opacity = 0.6 + 0.4 * Math.sin(hex.distanceToCenter / 80 - t * 0.5);
                    opacity = Math.max(0, Math.min(1, opacity));
                    gc.setFill(Color.rgb(85, 85, 85, opacity));
                    gc.fillPolygon(hex.xPoints, hex.yPoints, 6);
                }
            }
        }.start();
    }

}