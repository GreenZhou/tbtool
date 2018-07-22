package com.augurit.awater.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class TaskInstance {
    private String id;
    private String taskName;
    private String creatorId;
    private String creatorName;
    private String recieverId;
    private String recieverName;
    private int minCustomerNum;
    private int realCustomerNum;
    private int status;//  任务状态，0： 未发布 1： 已发布，但员工未处理 2： 已发布，但员工已经分派 3：已废弃
    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getRecieverId() {
        return recieverId;
    }

    public void setRecieverId(String recieverId) {
        this.recieverId = recieverId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRecieverName() {
        return recieverName;
    }

    public void setRecieverName(String recieverName) {
        this.recieverName = recieverName;
    }

    public int getMinCustomerNum() {
        return minCustomerNum;
    }

    public void setMinCustomerNum(int minCustomerNum) {
        this.minCustomerNum = minCustomerNum;
    }

    public int getRealCustomerNum() {
        return realCustomerNum;
    }

    public void setRealCustomerNum(int realCustomerNum) {
        this.realCustomerNum = realCustomerNum;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}