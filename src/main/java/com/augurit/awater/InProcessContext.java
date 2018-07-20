package com.augurit.awater;

import com.google.common.base.Strings;

/**
 * 说    明：
 * 创 建 人： 周卫鹏
 * 创建日期： 2016-10-18 07:06
 * 修改说明：
 */
public class InProcessContext {
    private static final ThreadLocal<RequestMsg> requestMsg = new ThreadLocal<RequestMsg>();
    private static final ThreadLocal<ResponseMsg> responseMsg = new ThreadLocal<ResponseMsg>();
    private static final ThreadLocal<PageParameter> pageParameter = new ThreadLocal<PageParameter>();

    public static void setRequestMsg(RequestMsg data) {
        requestMsg.set(data);
    }

    public static void setResponseMsg(ResponseMsg data) {
        responseMsg.set(data);
    }

    public static void setPageParameter() {
        PageParameter param = new PageParameter();
        if(requestMsg.get() != null) {
            if(!Strings.isNullOrEmpty(requestMsg.get().getPageNo())) {
                param.setPageNo(Integer.valueOf(requestMsg.get().getPageNo()));
            }

            if(!Strings.isNullOrEmpty(requestMsg.get().getShowSize())) {
                param.setShowSize(Integer.valueOf(requestMsg.get().getShowSize()));
            }
        }
        pageParameter.set(param);
    }

    public static RequestMsg getRequestMsg() {
        return requestMsg.get();
    }

    public static ResponseMsg getResponseMsg() {
        return responseMsg.get();
    }

    public static PageParameter getPageParameter() {
        return pageParameter.get();
    }

    public static void clearAll() {
        requestMsg.remove();
        responseMsg.remove();
        pageParameter.remove();
    }
}
