package com.augurit.awater;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 说    明： 对外提供的响应对象
 *           里面的message不应该包含细节信息, 细节信息应该记录到日志当中，而不应该暴露给用户界面
 *           比如： 数据库插入ID重复不应该反馈到界面上，而应该用系统内部错误替代
 * 创 建 人： 周卫鹏
 * 创建日期： 2017-10-18 11:03
 * 修改说明：
 */
public class ResponseMsg {

    private String code = "0000";
    private String message = "响应成功";

    private JSONObject content;

	// 禁止外部直接调用
	private ResponseMsg(RespCodeMsgDepository depository, JSONObject content) {
		this(depository.getCode(), depository.getMessage(), content);
	}

	// 禁止外部直接调用
    private ResponseMsg(String code, String message, JSONObject content) {
		this.code = code;
		this.message = message;
		this.content = content;
    }


    @Override
    public String toString() {
        Map<String, Object> respMap = Maps.newHashMapWithExpectedSize(3);
        respMap.put("code", code);
        respMap.put("message", message);
        respMap.put("content", content == null? new Object() : content);

        return JSONObject.toJSONString(respMap);
    }

    public static class ResponseMsgBuilder {
	    // Map的key为返回码
	    private static final Map<String, ResponseMsg> failedResponseMsgCache = Maps.newHashMap();

	    // 初始化responseMsgCache缓存
	    static {
		    failedResponseMsgCache.put(RespCodeMsgDepository.SERVER_INTERNAL_ERROR.getCode(),
				    new ResponseMsg(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, null));
		}

        public static ResponseMsg build(RespCodeMsgDepository depository, JSONObject content) {
	        if(depository == null) {
		        depository = RespCodeMsgDepository.SERVER_INTERNAL_ERROR;
	        }

	        String code = depository.getCode();
	        if(failedResponseMsgCache.containsKey(code)) {
		        return failedResponseMsgCache.get(code);
	        }

	        if(RespCodeMsgDepository.SUCCESS.getCode().equals(code)) {
		        return new ResponseMsg(RespCodeMsgDepository.SUCCESS, content);
	        }

	        // 错误的响应，同时没缓存过
	        ResponseMsg responseMsg = new ResponseMsg(depository, null);

	        // 缓存
	        synchronized (ResponseMsg.class) {
		        failedResponseMsgCache.put(code, responseMsg);
	        }

	        return responseMsg;
        }
    }
}
