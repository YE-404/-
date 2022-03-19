package com.myproject.systemdemo.domain;

import java.io.PrintWriter;

public class Log {
    private Integer id;
    private Integer userId;
    private String username;
    private String createDate;
    private String changeDate;
    private String fileSize;
    private String savePath;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", createDate='" + createDate + '\'' +
                ", changeDate='" + changeDate + '\'' +
                ", fileSize=" + fileSize +
                ", savePath='" + savePath + '\'' +
                '}';
    }
}
