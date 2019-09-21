package com.augurit.awater.entity;

import java.util.Date;
import java.util.List;

public class TaskDetail {
    // 状态
    public static final int NOT_CLAIMED = 0;// 未认领
    public static final int IS_CLAIMED = 1;// 已认领
    public static final int IS_COMPLISHED = 2;// 已完成
    public static final int IS_ABANDONED = 3;// 已废弃

    private String id;
    private String taskId;
    private String customerId;
    private String customerName;
    private String salerId;
    private String salerName;
    private String salerTMName;
    private String recieverId;
    private String recieverName;
    private Integer taskNum;
    private String taskDetailName;
    private String taskDesc;
    private String taskUrl;
    private List<FileInfo> detailFiles;
    private Double taskUnitPrice;
    private Double taskTotalPrice;
    private Double totalCommission;
    private Double customerCommission;
    private Integer status;// 任务明细状态，0: 未认领 1：已认领 2：已完成 3：已废弃
    private Integer oldStatus;// 任务明细状态，0: 未认领 1：已认领 2：已完成
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

    public Integer getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(Integer taskNum) {
        this.taskNum = taskNum;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public Double getTaskUnitPrice() {
        return taskUnitPrice;
    }

    public void setTaskUnitPrice(Double taskUnitPrice) {
        this.taskUnitPrice = taskUnitPrice;
    }

    public Double getTaskTotalPrice() {
        return taskTotalPrice;
    }

    public void setTaskTotalPrice(Double taskTotalPrice) {
        this.taskTotalPrice = taskTotalPrice;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Double getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(Double totalCommission) {
        this.totalCommission = totalCommission;
    }

    public Double getCustomerCommission() {
        return customerCommission;
    }

    public void setCustomerCommission(Double customerCommission) {
        this.customerCommission = customerCommission;
    }

    public String getSalerTMName() {
        return salerTMName;
    }

    public void setSalerTMName(String salerTMName) {
        this.salerTMName = salerTMName;
    }

    public String getTaskUrl() {
        return taskUrl;
    }

    public void setTaskUrl(String taskUrl) {
        this.taskUrl = taskUrl;
    }

    public Integer getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(Integer oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getRecieverId() {
        return recieverId;
    }

    public void setRecieverId(String recieverId) {
        this.recieverId = recieverId;
    }

    public String getRecieverName() {
        return recieverName;
    }

    public void setRecieverName(String recieverName) {
        this.recieverName = recieverName;
    }

	public List<FileInfo> getDetailFiles() {
		return detailFiles;
	}

	public void setDetailFiles(List<FileInfo> detailFiles) {
		this.detailFiles = detailFiles;
	}
}