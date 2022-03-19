package com.myproject.systemdemo.domain;

public class WeightMes {
    private Integer id;
    private String category;
    private String cfg;
    private String weight;
    private String message;
    private String url;
    private String date;
    private Boolean state;
    private Boolean train;

    public Boolean getTrain() {
        return train;
    }

    public void setTrain(Boolean train) {
        this.train = train;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCfg() {
        return cfg;
    }

    public void setCfg(String cfg) {
        this.cfg = cfg;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "WeightMes{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", cfg='" + cfg + '\'' +
                ", weight='" + weight + '\'' +
                ", message='" + message + '\'' +
                ", url='" + url + '\'' +
                ", date='" + date + '\'' +
                ", state=" + state +
                ", train=" + train +
                '}';
    }
}
