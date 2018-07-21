package com.augurit.awater.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class User {
    private String id;
    private String loginName;
    private String userName;
    private String passwd;
    private int userType;// 0: 管理员 1： 员工 2： 买家

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JSONField(serialize = false)
    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    @JSONField(serialize = false)
    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }
}
