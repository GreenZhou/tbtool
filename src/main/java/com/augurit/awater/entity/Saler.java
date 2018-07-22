package com.augurit.awater.entity;

public class Saler {
    private String id;
    private String salerName;//  店铺名称
    private String salerTMName;//  店铺阿里旺旺名称
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

    public String getSalerTMName() {
        return salerTMName;
    }

    public void setSalerTMName(String salerTMName) {
        this.salerTMName = salerTMName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}