package com.myproject.systemdemo.domain;

public class UserManage {
    private Integer onlineSize;
    private Integer totalSize;
    private Integer offlineSize;

    public Integer getOnlineSize() {
        return onlineSize;
    }

    public void setOnlineSize(Integer onlineSize) {
        this.onlineSize = onlineSize;
    }

    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }

    public Integer getOfflineSize() {
        return offlineSize;
    }

    public void setOfflineSize(Integer offlineSize) {
        this.offlineSize = offlineSize;
    }

    @Override
    public String toString() {
        return "UserManage{" +
                "onlineSize=" + onlineSize +
                ", totalSize=" + totalSize +
                ", offlineSize=" + offlineSize +
                '}';
    }
}
