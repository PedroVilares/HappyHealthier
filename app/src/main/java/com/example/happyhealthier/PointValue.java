package com.example.happyhealthier;

public class PointValue {
    long xValue;
    double yValue;

    public PointValue() {
    }

    public PointValue(long xValue, double yValue) {
        this.xValue = xValue;
        this.yValue = yValue;
    }

    public long getxValue() {
        return xValue;
    }

    public double getyValue() {
        return yValue;
    }
}