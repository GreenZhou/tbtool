package com.augurit.awater.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class TaskDetail {
    private String id;
    private String taskId;
    private String customerId;
    private String customerName;
    private String salerId;
    private String salerName;
    private int taskNum;
    private String taskDetailName;
    private String taskDesc;
    private double taskUnitPrice;
    private double taskTotalPrice;
    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskDetailName() {
        return taskDetailName;
    }

    public void setTaskDetailName(String taskDetailName) {
        this.taskDetailName = taskDetailName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getSalerId() {
        return salerId;
    }

    public void setSalerId(String salerId) {
        this.salerId = salerId;
    }

    public String getSalerName() {
        return salerName;
    }

    public void setSalerName(String salerName) {
        this.salerName = salerName;
    }

    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public double getTaskUnitPrice() {
        return taskUnitPrice;
    }

    public void setTaskUnitPrice(double taskUnitPrice) {
        this.taskUnitPrice = taskUnitPrice;
    }

    public double getTaskTotalPrice() {
        return taskTotalPrice;
    }

    public void setTaskTotalPrice(double taskTotalPrice) {
        this.taskTotalPrice = taskTotalPrice;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}