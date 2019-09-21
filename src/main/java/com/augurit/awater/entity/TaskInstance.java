package com.augurit.awater.entity;

import java.util.Date;
import java.util.List;

public class TaskInstance {
    // 发布状态
    public static final int NOT_PUBLISHED = 0;// 未发布
    public static final int IS_ASSIGNED = 1;// 已发布
    public static final int IS_COMPLISHED = 2;// 已完成
    public static final int IS_ABANDONED = 3;// 已废弃

    // 认领状态
    public static final int NOT_CLAIMED = 0;// 未认领
    public static final int PART_CLAIMED = 1;// 部分认领
    public static final int ALL_CLAIMED = 2;// 全部认领

    // 完成状态
    public static final int NOT_COMPLISHED = 0;// 未完成
    public static final int PART_COMPLISHED= 1;// 部分完成
    public static final int ALL_COMPLISHED = 2;// 全部完成

    // 认领方式
    public static final int PREEMPTION_CLAIM_TYPE = 0;// 抢占式
    public static final int ASSIGN_CLAIM_TYPE = 1;// 指派式

    private String id;
    private String taskName;
    private String creatorId;
    private String creatorName;
    private String recieverIds;// 接收人范围ID
    private List<User> recievers;// 接收人范围列表
    private Integer minCustomerNum;
    private Integer realCustomerNum;
    private Integer claimType;// 认领方式，0：指定认领 1：抢占认领
    private Integer publishedStatus;// 发布状态，0：未发布 1：已发布 2: 已完成 3：已废弃
    private Integer publishStatus;// 发布状态，0：未发布 1：已发布 2: 已完成 3：已废弃
    private Integer claimStatus;// 认领状态，0：未认领 1：部分认领 2: 全部认领
    private Integer complishStatus;// 完成状态，0：未完成 1：部分完成 2: 全部完成
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getMinCustomerNum() {
        return minCustomerNum;
    }

    public void setMinCustomerNum(Integer minCustomerNum) {
        this.minCustomerNum = minCustomerNum;
    }

    public Integer getRealCustomerNum() {
        return realCustomerNum;
    }

    public void setRealCustomerNum(Integer realCustomerNum) {
        this.realCustomerNum = realCustomerNum;
    }

    public List<TaskDetail> getDetails() {
        return details;
    }

    public void setDetails(List<TaskDetail> details) {
        this.details = details;
    }

    public List<User> getRecievers() {
        return recievers;
    }

    public void setRecievers(List<User> recievers) {
        this.recievers = recievers;
    }

    public Integer getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(Integer publishStatus) {
        this.publishStatus = publishStatus;
    }

    public Integer getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(Integer claimStatus) {
        this.claimStatus = claimStatus;
    }

    public Integer getComplishStatus() {
        return complishStatus;
    }

    public void setComplishStatus(Integer complishStatus) {
        this.complishStatus = complishStatus;
    }

    public Integer getClaimType() {
        return claimType;
    }

    public void setClaimType(Integer claimType) {
        this.claimType = claimType;
    }

    public Integer getPublishedStatus() {
        return publishedStatus;
    }

    public void setPublishedStatus(Integer publishedStatus) {
        this.publishedStatus = publishedStatus;
    }

    public String getRecieverIds() {
        return recieverIds;
    }

    public void setRecieverIds(String recieverIds) {
        this.recieverIds = recieverIds;
    }
}