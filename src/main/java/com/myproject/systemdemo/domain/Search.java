package com.myproject.systemdemo.domain;

import java.util.List;

public class Search<T> {
    private String type;
    private String content;
    private int totalCount;
    private List<T> rows;




    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "Search{" +
                "type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", totalCount=" + totalCount +
                ", rows=" + rows +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
