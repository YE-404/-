package com.myproject.systemdemo.domain;

public class TaskFlow {
    private Integer id;
    private Integer userId;
    private Integer taskId;
    private String taskName;
    private String position;
    private String picture;
    private double minValue;
    private double maxValue;
    private double zeroLine;
    private double valueRange;
    private String taskContent;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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

    public double getZeroLine() {
        return zeroLine;
    }

    public void setZeroLine(double zeroLine) {
        this.zeroLine = zeroLine;
    }

    public double getValueRange() {
        return valueRange;
    }

    public void setValueRange(double valueRange) {
        this.valueRange = valueRange;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    @Override
    public String toString() {
        return "TaskFlow{" +
                "id=" + id +
                ", taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", position='" + position + '\'' +
                ", picture='" + picture + '\'' +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", zeroLine=" + zeroLine +
                ", valueRange=" + valueRange +
                ", taskContent='" + taskContent + '\'' +
                '}';
    }
}
