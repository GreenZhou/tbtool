package com.augurit.awater.entity;

import java.util.Date;
import java.util.List;

public class TaskInstance {
    public static final int NOT_PUBLISHED = 0;
    public static final int IS_ASSIGNED = 1;
    public static final int IS_COMPLISHED = 2;
    public static final int IS_ABANDONED = 3;

    private String id;
    private String taskName;
    private String creatorId;
    private String creatorName;
    private String recieverId;
    private String recieverName;
    private int minCustomerNum;
    private int realCustomerNum;
    private int status;//  任务状态，0：未发布 1：已发布 2: 已完成 3：已废弃
    private Date createTime;
    private List<TaskDetail> details;

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

    public List<TaskDetail> getDetails() {
        return details;
    }

    public void setDetails(List<TaskDetail> details) {
        this.details = details;
    }
}