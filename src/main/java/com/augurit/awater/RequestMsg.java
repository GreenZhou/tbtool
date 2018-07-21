package com.augurit.awater;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.augurit.awater.exception.AppException;
import com.google.common.base.Strings;

/**
 * 说    明： 请求消息对象
 * 创 建 人： 周卫鹏
 * 创建日期： 2017-10-18 11:03
 * 修改说明：
 */
public class RequestMsg {
    private String token;
    private String pageNo;
    private String showSize;// 显示条目数
    private JSONObject content;
    private String reqData;// 请求原始的JSON数据

    public RequestMsg(String reqData) {
        this.reqData = reqData;

        JSONObject reqJson;
        try {
            reqJson = JSON.parseObject(reqData);
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.REQUEST_DATA_ERROR, "该请求数据不符合规范...");
        }

        if(reqJson == null) {
            throw new AppException(RespCodeMsgDepository.REQUEST_DATA_ERROR, "请求数据不能为空...");
        }

        token = reqJson.getString("token");
        pageNo = reqJson.getString("pageNo");
        showSize = reqJson.getString("showSize");
        content = reqJson.getJSONObject("content");
    }

    public String getReqData() {
        return reqData;
    }

    public String getToken() {
        return token;
    }

    public JSONObject getContent() {
        return content;
    }

    public String getPageNo() {
        return pageNo;
    }

    public String getShowSize() {
        return showSize;
    }
}
