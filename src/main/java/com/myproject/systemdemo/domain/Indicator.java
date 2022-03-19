package com.myproject.systemdemo.domain;

public class Indicator {
    private Integer id;
    private String class_;
    private double confidence;
    private String result;
    private String units;
    private String time;
    private String saveUrl;

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getSaveUrl() {
        return saveUrl;
    }

    public void setSaveUrl(String saveUrl) {
        this.saveUrl = saveUrl;
    }

    @Override
    public String toString() {
        return "Indicator{" +
                "id=" + id +
                ", class_='" + class_ + '\'' +
                ", confidence=" + confidence +
                ", result='" + result + '\'' +
                ", units='" + units + '\'' +
                ", time='" + time + '\'' +
                ", saveUrl='" + saveUrl + '\'' +
                '}';
    }

    public String toStringLog() {
        return "Indicator{" +
                ", class_='" + class_ + '\'' +
                ", confidence=" + confidence +
                '}';
    }
    public String toStringPointLog() {
        return "Indicator{" +
                ", class_='" + class_ + '\'' +
                ", confidence=" + confidence +
                ", result='" + result + '\'' +
                ", units='" + units + '\'' +
                '}';
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClass_() {
        return class_;
    }

    public void setClass_(String class_) {
        this.class_ = class_;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

