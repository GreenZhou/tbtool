package com.augurit.awater.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.augurit.awater.DefaultIdGenerator;
import com.augurit.awater.InProcessContext;
import com.augurit.awater.RespCodeMsgDepository;
import com.augurit.awater.ResponseMsg;
import com.augurit.awater.entity.User;
import com.augurit.awater.exception.AppException;
import com.augurit.awater.service.IUser;
import com.augurit.awater.service.ISaler;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/saler")
public class SalerController {

    private final static Logger LOGGER = Logger.getLogger(SalerController.class);

    private final static int STAFF = 1;

    @Autowired
    private IUser iuser;

    @Autowired
    private ISaler isaler;    

    @RequestMapping("/list")
    @ResponseBody
    public void listSalers(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            if(checkUserPriv(req)) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            JSONArray salers = (JSONArray) JSONArray.toJSON(isaler.findSalerList());
            JSONObject ret = new JSONObject();
            ret.put("totalCount", InProcessContext.getPageParameter().getTotalCount());
            ret.put("pageSize", InProcessContext.getPageParameter().getPageSize());
            ret.put("list", salers);
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, ret);
        } catch (Exception e) {
            LOGGER.error("商家查询失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/save")
    @ResponseBody
    public void saveSaler(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            if(checkUserPriv(req)) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            JSONObject content = InProcessContext.getRequestMsg().getContent();
            Saler saler = new Saler();
            saler.setId(defaultIdGenerator.getIdForStr());
            saler.setSalerName(content.getString("salerName"));
            saler.setUrl(content.getString("url"));
            isaler.saveSaler(saler);

            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("商家保存失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/del")
    @ResponseBody
    public void delSaler(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            if(checkUserPriv(req)) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            JSONObject content = InProcessContext.getRequestMsg().getContent();
            isaler.delSaler(content.getString("id"));
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("商家信息删除失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/update")
    @ResponseBody
    public void updSaler(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            if(checkUserPriv(req)) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            JSONObject content = InProcessContext.getRequestMsg().getContent();
            Saler saler = new Saler();
            saler.setId(content.getString("id"));
            saler.setSalerName(content.getString("salerName"));
            saler.setUrl(content.getString("url"));

            isaler.updSaler(saker);
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("商家信息更新失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    // ToDo 这段有点难以理解
    private boolean checkUserPriv(HttpServletRequest req) {
        String token = InProcessContext.getRequestMsg().getToken();
        // ToDo 校验请求报文中数据
        User user = (User) req.getSession().getAttribute(token);
        return user.getUserType() == STAFF;
    }
}
