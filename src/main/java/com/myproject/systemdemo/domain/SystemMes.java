package com.myproject.systemdemo.domain;

public class SystemMes {
    private Integer id;
    private Integer visitorVolume;
    private Integer userVolume;
    private Integer systemLogVolume;
    private Integer documentVolume;
    private Integer weekOfYear;



    public  String[] historyTime = new String[3];
    public  String[] logMessage = new String[3];
    public  String[] username = new String[3];

    public static int[] thisWeekLogin = new int[7];

    public Integer getWeekOfYear() {
        return weekOfYear;
    }

    public void setWeekOfYear(Integer weekOfYear) {
        this.weekOfYear = weekOfYear;
    }

    public Integer getDocumentVolume() {
        return documentVolume;
    }

    public void setDocumentVolume(Integer documentVolume) {
        this.documentVolume = documentVolume;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVisitorVolume() {
        return visitorVolume;
    }

    public void setVisitorVolume(Integer visitorVolume) {
        this.visitorVolume = visitorVolume;
    }

    public Integer getUserVolume() {
        return userVolume;
    }

    public void setUserVolume(Integer userVolume) {
        this.userVolume = userVolume;
    }

    public Integer getSystemLogVolume() {
        return systemLogVolume;
    }

    public void setSystemLogVolume(Integer systemLogVolume) {
        this.systemLogVolume = systemLogVolume;
    }

    @Override
    public String toString() {
        return "System{" +
                "id=" + id +
                ", visitorVolume=" + visitorVolume +
                ", userVolume=" + userVolume +
                ", systemLogVolume=" + systemLogVolume +
                '}';
    }
}
