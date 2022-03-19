package com.myproject.systemdemo.thriftDemo;

public class TransmitMes {
    private String imgMessage;
    private String[] screenType;
    private String checkType;
    private String userId;
    private String indicatorCfg;
    private String indicatorWeight;
    private String insideCfg;
    private String insideWeight;

    public void init(String imgMessage, String[] screenType, String checkType, String userId, String indicatorCfg, String indicatorWeight, String insideCfg, String insideWeight) {
        this.imgMessage = imgMessage;
        this.screenType = screenType;
        this.checkType = checkType;
        this.userId = userId;
        this.indicatorCfg = indicatorCfg;
        this.indicatorWeight = indicatorWeight;
        this.insideCfg = insideCfg;
        this.insideWeight = insideWeight;
    }

    public String getIndicatorCfg() {
        return indicatorCfg;
    }

    public void setIndicatorCfg(String indicatorCfg) {
        this.indicatorCfg = indicatorCfg;
    }

    public String getIndicatorWeight() {
        return indicatorWeight;
    }

    public void setIndicatorWeight(String indicatorWeight) {
        this.indicatorWeight = indicatorWeight;
    }

    public String getInsideCfg() {
        return insideCfg;
    }

    public void setInsideCfg(String insideCfg) {
        this.insideCfg = insideCfg;
    }

    public String getInsideWeight() {
        return insideWeight;
    }

    public void setInsideWeight(String insideWeight) {
        this.insideWeight = insideWeight;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getImgMessage() {
        return imgMessage;
    }

    public void setImgMessage(String imgMessage) {
        this.imgMessage = imgMessage;
    }

    public String[] getScreenType() {
        return screenType;
    }

    public void setScreenType(String[] screenType) {
        this.screenType = screenType;
    }

    @Override
    public String toString() {
        return "TransmitMes{" +
                "imgMessage='" + imgMessage + '\'' +
                ", screenType='" + screenType + '\'' +
                ", checkType='" + checkType + '\'' +
                '}';
    }
}
