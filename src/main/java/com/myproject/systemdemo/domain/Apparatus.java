package com.myproject.systemdemo.domain;

public class Apparatus {
    private Integer id;
    private Integer taskId;
    private Integer userId;
    private String taskName;
    private String taskContent;
    private String result;
    private String time;
    private String saveUrl;

    public Apparatus(){}

    public Apparatus(Integer userId,String taskName,String taskContent,String result){
        this.userId = userId;
        this.taskName = taskName;
        this.taskContent = taskContent;
        this.result = result;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSaveUrl() {
        return saveUrl;
    }

    public void setSaveUrl(String saveUrl) {
        this.saveUrl = saveUrl;
    }

    @Override
    public String toString() {
        return "Apparatus{" +
                "id=" + id +
                ", taskId=" + taskId +
                ", userId=" + userId +
                ", taskName='" + taskName + '\'' +
                ", taskContent='" + taskContent + '\'' +
                ", result='" + result + '\'' +
                ", time='" + time + '\'' +
                ", saveUrl='" + saveUrl + '\'' +
                '}';
    }
}
