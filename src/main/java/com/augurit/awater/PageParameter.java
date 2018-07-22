package com.augurit.awater;

/**
 * 说    明： 分页参数对象
 * 创 建 人： 周卫鹏
 * 创建日期： 2017-10-18 11:03
 * 修改说明：
 */
public class PageParameter {
    private int totalCount = 0; // 记录总数
    private int pageSize = 1;// 记录总页数
    private int pageNo = 1;// 当前页
    private int showSize = 20;// 显示数目
    private int currentIndex = 0;

    public PageParameter() {}

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        if(totalCount < 0) {
            this.totalCount = 0;
        } else {
            this.totalCount = totalCount;
        }

        if(this.totalCount > 0 && this.totalCount%showSize == 0) {
            this.pageSize = this.totalCount/showSize;
        } else {
            this.pageSize = this.totalCount/showSize + 1;
        }

        if(this.pageNo > this.pageSize) {
            this.pageNo = this.pageSize;
        }

        this.currentIndex = (this.pageNo - 1) * this.showSize;

    }

    public int getPageSize() {
        return pageSize;
    }

    // 调用要在setTotalCount方法之前, 否则则需自己设置参数
    public void setPageNo(int pageNo) {
        if(pageNo < 1) {
            this.pageNo = 1;
        } else {
            this.pageNo = pageNo;
        }

        currentIndex = (this.pageNo - 1) * this.showSize;
    }

    public int getShowSize() {
        return showSize;
    }

    public void setShowSize(int showSize) {
        this.showSize = showSize;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

}
