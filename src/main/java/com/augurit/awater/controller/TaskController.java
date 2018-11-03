package com.augurit.awater.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.augurit.awater.DefaultIdGenerator;
import com.augurit.awater.InProcessContext;
import com.augurit.awater.RespCodeMsgDepository;
import com.augurit.awater.ResponseMsg;
import com.augurit.awater.compress.IUncompress;
import com.augurit.awater.compress.UncompressImpl;
import com.augurit.awater.entity.FileInfo;
import com.augurit.awater.entity.TaskDetail;
import com.augurit.awater.entity.User;
import com.augurit.awater.entity.TaskInstance;
import com.augurit.awater.poi.TaskDetailProcessor;
import com.augurit.awater.service.IFile;
import com.augurit.awater.service.ITask;
import com.augurit.awater.service.IUser;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Controller
@RequestMapping("/task")
public class TaskController {

    private final static Logger LOGGER = Logger.getLogger(TaskController.class);

    private final static IUncompress compress = new UncompressImpl();

    //  管理员用户类型
    private final static int ADMINISTRATOR = 0;
    //  普通员工用户类型
    private final static int STAFF = 1;
    //  普通买家用户类型
    private final static int CUSTOMER = 2;

	private String filePath = "C:/awater/upload/";
    private String uncompressDestPath = "C:/awater/compress/";

    @Autowired
    private ITask itask;

	@Autowired
	private IFile ifile;

    @Autowired
	private IUser iuser;

    @RequestMapping("/list")
    @ResponseBody
    public void listTaskInstances(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            String recieverId = null, creatorId = null;
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
            if(!Strings.isNullOrEmpty(content.getString("recieverId"))) {
                User reciever = iuser.getUser(content.getString("recieverId"));
                if(reciever != null) {
                    instance.setRecieverId(reciever.getId());
                    instance.setRecieverName(reciever.getUserName());
                } else {
                    // 数据库中没找到对应用户
                    responseMsg = RespCodeMsgDepository.REQUEST_DATA_ERROR.toResponseMsg();
                    return;
                }
            }
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
            if(!checkUserAdmin(req)) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            JSONObject content = InProcessContext.getRequestMsg().getContent();
            String[] ids = content.getString("ids").split(",");
            for(String id : ids) {
                TaskInstance instance = itask.getTaskInstance(id);
                if(instance != null && instance.getStatus() == TaskInstance.IS_ASSIGNED) {
                    responseMsg = RespCodeMsgDepository.DELETE_TASK_ERROR.toResponseMsg();
                    return;
                }
                itask.delTaskInstance(id);
            }
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
            if(!checkUserAdmin(req)) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            JSONObject content = InProcessContext.getRequestMsg().getContent();
            TaskInstance instance = itask.getTaskInstance(content.getString("id"));
            if(instance != null && instance.getStatus() == TaskInstance.IS_ASSIGNED) {
                boolean canAbandonFlag = true;
                for(TaskDetail detail : instance.getDetails()) {
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
            if(!checkUserAdmin(req)) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            JSONObject content = InProcessContext.getRequestMsg().getContent();
            TaskInstance instance = new TaskInstance();
            instance.setId(content.getString("id"));
            instance.setTaskName(content.getString("taskName"));
            if(!Strings.isNullOrEmpty(content.getString("recieverId"))) {
                User reciever = iuser.getUser(content.getString("recieverId"));
                if(reciever != null) {
                    instance.setRecieverId(reciever.getId());
                    instance.setRecieverName(reciever.getUserName());
                }
            }
            itask.updateTaskInstance(instance);
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("任务信息更新失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/importTaskDetails")
    @ResponseBody
    public void importTaskDetails() {

    }

    @RequestMapping("/publish")
    @ResponseBody
    public void publishTaskInstance(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            if(!checkUserAdmin(req)) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            // 任务发布
            JSONObject content = InProcessContext.getRequestMsg().getContent();
            TaskInstance instance = new TaskInstance();
            instance.setStatus(TaskInstance.IS_ASSIGNED);
            instance.setRecieverId(content.getString("recieverId"));
            instance.setRecieverName(content.getString("recieverName"));
            instance.setMinCustomerNum(1);
            instance.setRealCustomerNum(1);
            itask.updateTaskInstance(instance);
        } catch (Exception e) {
            LOGGER.error("任务信息发布失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/detail/import/{instanceId}/{token}")
    @ResponseBody
    public void importTaskDetails(@RequestParam("file") MultipartFile file, @PathVariable("instanceId") String instanceId, @PathVariable("token") String token, HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            String originalFilename = file.getOriginalFilename();
            // IE8下会拿到文件的路径名
            if(originalFilename.indexOf("\\") != -1) {// windows环境
                originalFilename = originalFilename.substring(originalFilename.lastIndexOf("\\") + 1);
            }
            if(originalFilename.indexOf("/") != -1) {
                originalFilename = originalFilename.substring(originalFilename.lastIndexOf("/") + 1);
            }
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
			String id = DefaultIdGenerator.getIdForStr();
			// 上传文件名
			String newFilename = id + suffix;
			File serverFile = new File(filePath + newFilename);
			// 将上传的文件写入到服务器端文件内
			file.transferTo(serverFile);

			FileInfo fileInfo = new FileInfo();
			fileInfo.setId(id);
			fileInfo.setOriginalFilename(originalFilename);
			fileInfo.setSuffix(suffix);
			fileInfo.setDirPath(filePath + instanceId);
			fileInfo.setFileSize(Math.floor(file.getSize()/1024d + 0.5) + "KB");
			User user = (User) req.getSession().getAttribute(token);
			fileInfo.setCreatorLoginName(user.getLoginName());
			fileInfo.setCreatorUserName(user.getUserName());
			ifile.saveFile(fileInfo);

			// 解压
			compress.uncompress(serverFile.getCanonicalPath(), uncompressDestPath + instanceId);

			// 取xls文件
			// 解析xls文件并数据入库
            List<TaskDetail> details = Lists.newArrayList();
			Iterator<File> files = org.apache.commons.io.FileUtils.iterateFiles(new File(uncompressDestPath + instanceId), new String[] {"xls", "xlsx"}, true);
			while(files.hasNext()) {
				File xlsFile = files.next();
				// 解析excel
                details.addAll(new TaskDetailProcessor().process(xlsFile));
			}

			for(TaskDetail detail : details) {
			    detail.setTaskId(instanceId);
			    detail.setStatus(TaskDetail.NOT_CLAIMED);
            }
			itask.saveTaskDetailBatch(details);
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("任务详情导入失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/detail/list")
    @ResponseBody
    public void listTaskDetails(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            JSONObject content = InProcessContext.getRequestMsg().getContent();

            List<String> excludeCols = new ArrayList<String>();
            String instanceId = content.getString("instanceId");
            User user = (User) req.getSession().getAttribute(InProcessContext.getRequestMsg().getToken());
            if(STAFF == user.getUserType()) {
                // TaskInstance指定的recieverId可以不用是当前登录用户的ID
            } else if(ADMINISTRATOR == user.getUserType()) {
                excludeCols.add("customerCommission");
            } else {
                excludeCols.add("totalCommission");
            }

            JSONArray details = (JSONArray) JSONArray.toJSON(itask.findTaskDetailList(user, instanceId, excludeCols));
            JSONObject ret = new JSONObject();
            ret.put("totalCount", InProcessContext.getPageParameter().getTotalCount());
            ret.put("pageSize", InProcessContext.getPageParameter().getPageSize());
            ret.put("list", details);
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, ret);
        } catch (Exception e) {
            LOGGER.error("任务详情列表查询失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/detail/update")
    @ResponseBody
    public void updTaskDetail(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        TaskDetail detail = new TaskDetail();
        try {
            String token = InProcessContext.getRequestMsg().getToken();
            User user = (User) req.getSession().getAttribute(token);
            JSONObject content = InProcessContext.getRequestMsg().getContent();
            if(user.getUserType() == ADMINISTRATOR) {
                TaskInstance instance = itask.getTaskInstance(content.getString("instanceId"));
                // 管理员主要更新任务明细 salerId、salerName、taskNum、taskDetailName、taskDesc、taskUnitPrice
                // taskTotalPrice、totalCommission
                detail.setSalerId(content.getString("salerId"));
                detail.setSalerName(content.getString("salerName"));
                detail.setTaskNum(content.getIntValue("taskNum"));
                detail.setTaskDetailName(content.getString("taskDetailName"));
                detail.setTaskDesc(content.getString("taskDesc"));
                detail.setTaskUnitPrice(content.getDoubleValue("taskUnitPrice"));
                detail.setTaskTotalPrice(content.getDoubleValue("taskTotalPrice"));
                detail.setTotalCommission(content.getDoubleValue("totalCommission"));

                itask.updTaskDetail(detail);
            } else if(user.getUserType() == STAFF) {
                // 普通员工主要更新任务明细 customerId、customerName、customerCommission、status
                detail.setCustomerId(content.getString("customerId"));
                detail.setCustomerName(content.getString("customerName"));
                detail.setCustomerCommission(content.getDoubleValue("customerCommission"));
                detail.setStatus(content.getInteger("status"));
            } else {
                // 买家主要更新任务明细 status
                detail.setStatus(content.getInteger("status"));
            }

            itask.updTaskDetail(detail);
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("任务详细信息更新失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/detail/del")
    @ResponseBody
    public void delTaskDetail(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            if(!checkUserAdmin(req)) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            // 删除具体详细任务
            JSONObject content = InProcessContext.getRequestMsg().getContent();
            String instanceId = content.getString("instanceId");
            TaskInstance instance = itask.getTaskInstance(instanceId);
            if(instance != null && instance.getStatus() == TaskInstance.IS_ASSIGNED) {
                responseMsg = RespCodeMsgDepository.DELETE_TASK_ERROR.toResponseMsg();
                return;
            }
            List<String> ids = null;
            if(!Strings.isNullOrEmpty(content.getString("ids"))) {
                ids = Lists.newArrayList(content.getString("ids").split(","));
            }
            itask.delTaskDetail(ids, instanceId);
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("任务详细信息删除失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/detail/abandon")
    @ResponseBody
    public void abandonTaskDetail(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            User user = (User) req.getSession().getAttribute(InProcessContext.getRequestMsg().getToken());
            if(user.getUserType() == STAFF) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            // 废弃具体详细任务
            JSONObject content = InProcessContext.getRequestMsg().getContent();
            itask.abandonTaskDetail(user, content.getString("instanceId"), content.getString("id"));
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("明细任务废弃失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/detail/save")
    @ResponseBody
    public void saveTaskDetail(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            String token = InProcessContext.getRequestMsg().getToken();
            User user = (User) req.getSession().getAttribute(token);
            if(user == null || user.getUserType() != ADMINISTRATOR) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }
            // 只能在未发布状态下进行新增
            JSONObject content = InProcessContext.getRequestMsg().getContent();
            TaskInstance instance = itask.getTaskInstance(content.getString("instanceId"));
            if(instance.getStatus() != TaskInstance.NOT_PUBLISHED) {
                responseMsg = RespCodeMsgDepository.SAVE_TASK_ERROR.toResponseMsg();
                return;
            }
            TaskDetail detail = new TaskDetail();
            detail.setId(DefaultIdGenerator.getIdForStr());
            detail.setTaskDetailName(content.getString("taskDetailName"));
            detail.setTaskDesc(content.getString("taskDescName"));
            detail.setTaskUnitPrice(content.getDoubleValue("taskUnitPrice"));
            detail.setTaskTotalPrice(content.getDoubleValue("taskTotalPrice"));
            detail.setTaskNum(content.getIntValue("taskNum"));
            detail.setSalerId(content.getString("salerId"));
            detail.setSalerName(content.getString("salerName"));
            detail.setStatus(TaskDetail.NOT_CLAIMED);
            detail.setTotalCommission(content.getDoubleValue("totalCommission"));
            detail.setCreateTime(new Date());
            itask.saveTaskDetail(detail);

            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("任务明细保存失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/detail/publish")
    @ResponseBody
    public void publishTaskDetail(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            String token = InProcessContext.getRequestMsg().getToken();
            User user = (User) req.getSession().getAttribute(token);
            if(user.getUserType() == STAFF) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            // 具体任务发布
            JSONObject content = InProcessContext.getRequestMsg().getContent();
            itask.publishTaskDetail(user, content.getString("instanceId"));
        } catch (Exception e) {
            LOGGER.error("任务详细信息发布失败", e);
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

    // ToDo 这段有点难以理解
    private boolean checkUserStaff(HttpServletRequest req) {
        String token = InProcessContext.getRequestMsg().getToken();
        // ToDo 校验请求报文中数据
        User user = (User) req.getSession().getAttribute(token);
        return user.getUserType() == STAFF;
    }
}
