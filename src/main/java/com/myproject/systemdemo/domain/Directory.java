package com.myproject.systemdemo.domain;

public class Directory {
    String trainPicDirectory;
    String testPicDirectory;
    String trainXmlDirectory;
    String testXmlDirectory;
    String weightDirectory;
    String cfgDirectory;
    String darknetDirectory;
    int serialNumber;
    String weightSaveDirectory;

    public void init(String trainPicDirectory,String testPicDirectory,String trainXmlDirectory,String testXmlDirectory,
                     String weightDirectory,String cfgDirectory, String darknetDirectory,int serialNumber, String weightSaveDirectory){
        this.trainPicDirectory = trainPicDirectory;
        this.testPicDirectory = testPicDirectory;
        this.trainXmlDirectory = trainXmlDirectory;
        this.testXmlDirectory = testXmlDirectory;
        this.weightDirectory = weightDirectory;
        this.cfgDirectory = cfgDirectory;
        this.darknetDirectory = darknetDirectory;
        this.serialNumber = serialNumber;
        this.weightSaveDirectory = weightSaveDirectory;
    }

    public String getWeightSaveDirectory() {
        return weightSaveDirectory;
    }

    public void setWeightSaveDirectory(String weightSaveDirectory) {
        this.weightSaveDirectory = weightSaveDirectory;
    }

    public String getDarknetDirectory() {
        return darknetDirectory;
    }

    public void setDarknetDirectory(String darknetDirectory) {
        this.darknetDirectory = darknetDirectory;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getTrainPicDirectory() {
        return trainPicDirectory;
    }

    public void setTrainPicDirectory(String trainPicDirectory) {
        this.trainPicDirectory = trainPicDirectory;
    }

    public String getTestPicDirectory() {
        return testPicDirectory;
    }

    public void setTestPicDirectory(String testPicDirectory) {
        this.testPicDirectory = testPicDirectory;
    }

    public String getTrainXmlDirectory() {
        return trainXmlDirectory;
    }

    public void setTrainXmlDirectory(String trainXmlDirectory) {
        this.trainXmlDirectory = trainXmlDirectory;
    }

    public String getTestXmlDirectory() {
        return testXmlDirectory;
    }

    public void setTestXmlDirectory(String testXmlDirectory) {
        this.testXmlDirectory = testXmlDirectory;
    }

    public String getWeightDirectory() {
        return weightDirectory;
    }

    public void setWeightDirectory(String weightDirectory) {
        this.weightDirectory = weightDirectory;
    }

    public String getCfgDirectory() {
        return cfgDirectory;
    }

    public void setCfgDirectory(String cfgDirectory) {
        this.cfgDirectory = cfgDirectory;
    }

    @Override
    public String toString() {
        return "Directory{" +
                "trainPicDirectory='" + trainPicDirectory + '\'' +
                ", testPicDirectory='" + testPicDirectory + '\'' +
                ", trainXmlDirectory='" + trainXmlDirectory + '\'' +
                ", testXmlDirectory='" + testXmlDirectory + '\'' +
                ", weightDirectory='" + weightDirectory + '\'' +
                ", cfgDirectory='" + cfgDirectory + '\'' +
                ", darknetDirectory='" + darknetDirectory + '\'' +
                ", serialNumber=" + serialNumber +
                ", weightSaveDirectory='" + weightSaveDirectory + '\'' +
                '}';
    }
}
