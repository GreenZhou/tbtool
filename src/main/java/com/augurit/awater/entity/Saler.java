package com.augurit.awater.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class Saler {
    private String id;
    private String salerName;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSalerName() {
        return salerName;
    }

    public void setSalerName(String salerName) {
        this.salerName = salerName;
    }

    public String getUrl() {
        return userName;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}