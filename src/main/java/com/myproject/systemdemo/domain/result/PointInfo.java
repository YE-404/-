package com.myproject.systemdemo.domain.result;

public class PointInfo {
    double value;
    Integer x;
    Integer y;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "PointInfo{" +
                "value=" + value +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
