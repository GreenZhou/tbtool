package com.augurit.awater.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.augurit.awater.DefaultIdGenerator;
import com.augurit.awater.InProcessContext;
import com.augurit.awater.RespCodeMsgDepository;
import com.augurit.awater.ResponseMsg;
import com.augurit.awater.entity.User;
import com.augurit.awater.exception.AppException;
import com.augurit.awater.service.ITask;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/task")
public class TaskController {

    private final static Logger LOGGER = Logger.getLogger(TaskController.class);

    //  管理员用户类型
    private final static int ADMINISTRATOR = 0;
    //  普通员工用户类型
    private final static int STAFF = 1;
    //  普通买家用户类型
    private final static int CUSTOMER = 2;

    @Autowired
    private ITask itask;

    @Autowired
    private IUser iuser;    

    @RequestMapping("/list")
    @ResponseBody
    public void listTasks(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            if(checkUserPriv(req)) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            JSONArray customers = (JSONArray) JSONArray.toJSON(icustomer.findCustomerList());
            JSONObject ret = new JSONObject();
            ret.put("totalCount", InProcessContext.getPageParameter().getTotalCount());
            ret.put("pageSize", InProcessContext.getPageParameter().getPageSize());
            ret.put("list", customers);
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, ret);
        } catch (Exception e) {
            LOGGER.error("买家信息查询失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/save")
    @ResponseBody
    public void saveCustomer(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            if(checkUserPriv(req)) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            JSONObject content = InProcessContext.getRequestMsg().getContent();
            Customer customer = new Customer();
            customer.setId(defaultIdGenerator.getIdForStr());
            customer.setCustomerName(content.getString("customerName"));
            customer.setQq(content.getString("qq"));
            icustomer.saveCustomer(customer);

            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("买家信息保存失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/del")
    @ResponseBody
    public void delCustomer(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            if(checkUserPriv(req)) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            JSONObject content = InProcessContext.getRequestMsg().getContent();
            icustomer.delCustomer(content.getString("id"));
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("买家信息删除失败", e);
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
            Customer customer = new Customer();
            customer.setId(content.getString("id"));
            customer.setCustomerName(content.getString("customerName"));
            customer.setQq(content.getString("qq"));

            icustomer.updCustomer(customer);
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("买家信息更新失败", e);
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
