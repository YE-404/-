package com.myproject.systemdemo.domain;

public class ClockInfo {

    double minValue;
    double maxValue;
    double minAngle;
    double maxAngle;
    double angleRange;
    double circleX;
    double circleY;

    public ClockInfo() {
    }

    public ClockInfo(double minAngle, double maxAngle, double angleRange) {
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
        this.angleRange = angleRange;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getMinAngle() {
        return minAngle;
    }

    public void setMinAngle(double minAngle) {
        this.minAngle = minAngle;
    }

    public double getMaxAngle() {
        return maxAngle;
    }

    public void setMaxAngle(double maxAngle) {
        this.maxAngle = maxAngle;
    }

    public double getAngleRange() {
        return angleRange;
    }

    public void setAngleRange(double angleRange) {
        this.angleRange = angleRange;
    }

    public double getCircleX() {
        return circleX;
    }

    public void setCircleX(double circleX) {
        this.circleX = circleX;
    }

    public double getCircleY() {
        return circleY;
    }

    public void setCircleY(double circleY) {
        this.circleY = circleY;
    }

    @Override
    public String toString() {
        return "ClockInfo{" +
                "minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", minAngle=" + minAngle +
                ", maxAngle=" + maxAngle +
                ", angleRange=" + angleRange +
                ", circleX=" + circleX +
                ", circleY=" + circleY +
                '}';
    }
}
