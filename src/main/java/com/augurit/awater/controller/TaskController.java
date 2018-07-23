package com.augurit.awater.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.augurit.awater.DefaultIdGenerator;
import com.augurit.awater.InProcessContext;
import com.augurit.awater.RespCodeMsgDepository;
import com.augurit.awater.ResponseMsg;
import com.augurit.awater.entity.TaskDetail;
import com.augurit.awater.entity.TaskInstance;
import com.augurit.awater.entity.User;
import com.augurit.awater.exception.AppException;
import com.augurit.awater.service.ITask;
import com.augurit.awater.service.IUser;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

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

    @RequestMapping("/list")
    @ResponseBody
    public void listTaskInstances(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            String recieverId = "", creatorId = "";
            String token = InProcessContext.getRequestMsg().getToken();
            // ToDo 校验请求报文中数据
            User user = (User) req.getSession().getAttribute(token);
           if(STAFF == user.getUserType()) {
               recieverId = user.getId();
           } else if(ADMINISTRATOR == user.getUserType()) {
               creatorId = user.getId();
           } else {
               responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
               return;
           }

            JSONArray intances = (JSONArray) JSONArray.toJSON(itask.findTaskInstanceList(recieverId, creatorId));
            JSONObject ret = new JSONObject();
            ret.put("totalCount", InProcessContext.getPageParameter().getTotalCount());
            ret.put("pageSize", InProcessContext.getPageParameter().getPageSize());
            ret.put("list", intances);
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, ret);
        } catch (Exception e) {
            LOGGER.error("任务列表查询失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/save")
    @ResponseBody
    public void saveTaskInstance(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            String token = InProcessContext.getRequestMsg().getToken();
            // ToDo 校验请求报文中数据
            User user = (User) req.getSession().getAttribute(token);
            if(user == null || user.getUserType() != ADMINISTRATOR) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            JSONObject content = InProcessContext.getRequestMsg().getContent();
            TaskInstance instance = new TaskInstance();
            instance.setId(DefaultIdGenerator.getIdForStr());
            instance.setTaskName(content.getString("taskName"));
            instance.setCreateTime(new Date());
            instance.setCreatorId(user.getId());
            instance.setCreatorName(user.getUserName());
            instance.setStatus(TaskInstance.NOT_PUBLISHED);// 未发布状态
            itask.saveTaskInstance(instance);

            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("任务保存失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/del")
    @ResponseBody
    public void delTaskInstance(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            if(checkUserAdmin(req)) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            JSONObject content = InProcessContext.getRequestMsg().getContent();
            String id = content.getString("id");
            TaskInstance instance = itask.getTaskInstance(id);
            if(instance != null && instance.getDetails() != null && instance.getDetails().size() > 0) {
                responseMsg = RespCodeMsgDepository.DELETE_TASK_ERROR.toResponseMsg();
                return;
            }
            itask.delTaskInstance(id);
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("任务信息删除失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/abandon")
    @ResponseBody
    public void abandonTaskInstance(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            if(checkUserAdmin(req)) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            JSONObject content = InProcessContext.getRequestMsg().getContent();
            TaskInstance instance = itask.getTaskInstance(content.getString("id"));
            List<TaskDetail> details = instance.getDetails();
            if(instance != null && details != null) {
                boolean canAbandonFlag = true;
                for(TaskDetail detail : details) {
                    if(detail.getStatus() != TaskDetail.IS_ABANDONED && detail.getStatus() != TaskDetail.NOT_CLAIMED) {
                        // 具体任务处在已认领和已完成状态
                        canAbandonFlag = false;
                        break;
                    }
                }

                if(!canAbandonFlag) {
                    responseMsg = RespCodeMsgDepository.ABANDON_TASK_ERROR.toResponseMsg();
                    return;
                }
            }
            itask.abandonTaskInstance(content.getString("id"));
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("任务信息废弃失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/update")
    @ResponseBody
    public void updTaskInstance(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            if(checkUserAdmin(req)) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            JSONObject content = InProcessContext.getRequestMsg().getContent();
            TaskInstance instance = new TaskInstance();
            instance.setTaskName(content.getString("taskName"));
            itask.updateTaskInstance(instance);
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("任务信息更新失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    // ToDo 这段有点难以理解
    private boolean checkUserAdmin(HttpServletRequest req) {
        String token = InProcessContext.getRequestMsg().getToken();
        // ToDo 校验请求报文中数据
        User user = (User) req.getSession().getAttribute(token);
        return user.getUserType() == ADMINISTRATOR;
    }
}
