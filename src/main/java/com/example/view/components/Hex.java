package com.example.view.components;

public class Hex {
    public double[] xPoints;
    public double[] yPoints;
    public double distanceToCenter;

    public Hex(double x, double y, double r, double centerX, double centerY) {

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