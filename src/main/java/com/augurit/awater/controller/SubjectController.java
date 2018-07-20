package com.augurit.awater.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.augurit.awater.InProcessContext;
import com.augurit.awater.RespCodeMsgDepository;
import com.augurit.awater.ResponseMsg;
import com.augurit.awater.service.ISubject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 *
 *
 */
@Controller
@RequestMapping("/subject")
public class SubjectController {

	private final static Logger LOGGER = Logger.getLogger(SubjectController.class);

    @Autowired
    private ISubject subject;

    @RequestMapping("/listReservoirPage")
    @ResponseBody
    public void listReservoirPage() {
	    ResponseMsg responseMsg = null;
	    try {
		    JSONObject content = InProcessContext.getRequestMsg().getContent();
		    Map<String, Object> paramMap = JSONObject.toJavaObject(content, Map.class);
		    paramMap.put("zlStr", content.getString("zl"));
		    List<Map<String, Object>> list = subject.findReservoirList(paramMap);

		    JSONArray array = (JSONArray) JSONArray.toJSON(list);
		    JSONObject ret = new JSONObject();
		    ret.put("totalCount", InProcessContext.getPageParameter().getTotalCount());
		    ret.put("list", array);
		    responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, ret);

	    } catch (Exception e) {
		    LOGGER.error("查询XXX报错...", e);
		    responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
	    } finally {
		    InProcessContext.setResponseMsg(responseMsg);
	    }
    }

}
