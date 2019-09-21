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
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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
import java.util.*;

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

            JSONArray intances = (JSONArray) JSONArray.toJSON(itask.findTaskInstanceList(recieverId, creatorId,
                    InProcessContext.getRequestMsg().getContent().getString("taskName")));
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
            instance.setRecieverIds(content.getString("recieverIds"));
            instance.setCreateTime(new Date());
            instance.setCreatorId(user.getId());
            instance.setCreatorName(user.getUserName());
            instance.setMinCustomerNum(0);
            instance.setRealCustomerNum(0);
            instance.setClaimType(content.getIntValue("claimType"));
            instance.setClaimStatus(TaskInstance.NOT_CLAIMED);
            instance.setPublishStatus(TaskInstance.NOT_PUBLISHED);
            instance.setComplishStatus(TaskInstance.NOT_COMPLISHED);

            /*
            List<User> recievers = Lists.newArrayList();
            String recieverIds = content.getString("recieverIds");
            for(String recieverId : recieverIds.split(",")) {
                User reciever = iuser.getUser(recieverId);
                if(reciever != null) {
                    recievers.add(reciever);
                }
            }
            instance.setRecievers(recievers);
            */
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
            if(instance != null) {
                // 任务明细状态更新
                for(TaskDetail detail : instance.getDetails()) {
                    detail.setOldStatus(detail.getStatus());
                    detail.setStatus(TaskDetail.IS_ABANDONED);
                }

                // 任务状态更新
                instance.setPublishedStatus(instance.getPublishStatus());
                instance.setPublishStatus(TaskInstance.IS_ABANDONED);
                itask.abandonTaskInstance(instance);
            }

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
            instance.setRecieverIds(content.getString("recieverIds"));
            instance.setClaimType(content.getIntValue("claimType"));
            itask.updateTaskInstance(instance);
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("任务信息更新失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    /*
      发布任务          抢占方式                指派方式
      任务明细指定了人   分配给指定人，其他抢占    分配给指定人
      任务明细未指定人   抢占方式                发布时候提示平均随机分配
     */
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
            instance.setId(content.getString("id"));
            instance.setPublishStatus(TaskInstance.IS_ASSIGNED);

            List<TaskDetail> details = itask.findTaskDetailList(null, content.getString("id"), null, null);
            Map<String, Integer> salerCustomerNum = Maps.newHashMap();
            for(TaskDetail detail : details) {
                if(salerCustomerNum.containsKey(detail.getSalerTMName())) {
                    salerCustomerNum.put(detail.getSalerTMName(), salerCustomerNum.get(detail.getSalerTMName()) + 1 );
                } else {
                    salerCustomerNum.put(detail.getSalerTMName(), 1);
                }
            }

            // 指派方式，给未指定的人进行随机分配
            TaskInstance source = itask.getTaskInstance(instance.getId());
            if(TaskInstance.ASSIGN_CLAIM_TYPE == source.getClaimType()) {
                // TODO 优先同一个商铺对应同一个人
                List<String> recieverIds = Lists.newArrayList(source.getRecieverIds());
                List<String> recieverIdPool = Lists.newArrayListWithCapacity(details.size());
                for(int i=0; i<details.size(); i++) {
                    recieverIdPool.add(i, recieverIds.get(i % recieverIds.size()));
                }

                List<TaskDetail> assignedDetails = Lists.newArrayListWithCapacity(details.size());
                for(TaskDetail detail : details) {
                    if(!Strings.isNullOrEmpty(detail.getRecieverId())) {
                        recieverIdPool.remove(detail.getRecieverId());
                    } else {
                        TaskDetail assignedDetail = new TaskDetail();
                        assignedDetail.setId(detail.getId());
                        assignedDetails.add(assignedDetail);
                    }
                }

                // 随机指派
                Random rand = new Random();
                for(TaskDetail detail : assignedDetails) {
                    int randIndex = rand.nextInt(recieverIdPool.size());
                    String randRecieverId = recieverIdPool.get(randIndex);
                    recieverIdPool.remove(randIndex);
                    User randReciever = iuser.getUser(randRecieverId);
                    if(randReciever == null) {
                        LOGGER.error("通过用户ID [" + randRecieverId + "] 没有获取到用户信息");
                        responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
                        return;
                    }
                    detail.setRecieverId(randRecieverId);
                    detail.setRecieverName(randReciever.getUserName());
                    // 更新任务明细
                    itask.updTaskDetail(detail);
                }
            }

            int customerNum = -1;
            for(Map.Entry<String, Integer> entry : salerCustomerNum.entrySet()) {
                if(entry.getValue() > customerNum) {
                    customerNum = entry.getValue();
                }
            }
            instance.setMinCustomerNum(customerNum);
            instance.setRealCustomerNum(customerNum);
            itask.updateTaskInstance(instance);
        } catch (Exception e) {
            LOGGER.error("任务信息发布失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/getTaskRecievers")
    @ResponseBody
    public void getTaskRecievers(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            if(!checkUserAdmin(req)) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            List<User> recievers = itask.getTaskRecievers(InProcessContext.getRequestMsg().getContent().getString("instanceId"));
            JSONObject ret = new JSONObject();
            ret.put("list", recievers);
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, ret);

        } catch (Exception e) {
            LOGGER.error("获取任务接收人范围失败", e);
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
                List<TaskDetail> xlsDetails = new TaskDetailProcessor().process(xlsFile);

                for(TaskDetail detail : xlsDetails) {
                    File detailImg = new File(xlsFile.getParentFile().getCanonicalPath() + "\\" + detail.getTaskUrl());
                    String originalImgName = detailImg.getName();
                    // IE8下会拿到文件的路径名
                    if(originalImgName.indexOf("\\") != -1) {// windows环境
                        originalImgName = originalImgName.substring(originalImgName.lastIndexOf("\\") + 1);
                    }
                    if(originalImgName.indexOf("/") != -1) {
                        originalImgName = originalImgName.substring(originalImgName.lastIndexOf("/") + 1);
                    }
                    String imgSuffix = originalImgName.substring(originalImgName.lastIndexOf("."));
                    String imgId = DefaultIdGenerator.getIdForStr();
                    FileUtils.copyFile(detailImg, new File(this.filePath + imgId + imgSuffix));

                    List<FileInfo> detailFiles = Lists.newArrayList();
                    FileInfo detailImgInfo = new FileInfo();
                    detailImgInfo.setId(imgId);
                    detailImgInfo.setOriginalFilename(originalImgName);
                    detailImgInfo.setSuffix(imgSuffix);
                    detailImgInfo.setFileSize(Math.floor(detailImg.getTotalSpace()/1024d + 0.5) + "KB");
                    if(user != null) {
                        detailImgInfo.setCreatorLoginName(user.getLoginName());
                        detailImgInfo.setCreatorUserName(user.getUserName());
                    }
                    ifile.saveFile(detailImgInfo);
                    detailFiles.add(detailImgInfo);
                    detail.setDetailFiles(detailFiles);
                }
                details.addAll(xlsDetails);
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

            JSONArray details = (JSONArray) JSONArray.toJSON(itask.findTaskDetailList(user, instanceId, content.getString("taskDetailName"), excludeCols));
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
            detail.setId(content.getString("id"));
            if(user.getUserType() == ADMINISTRATOR) {
                TaskInstance instance = itask.getTaskInstance(content.getString("instanceId"));
                // 管理员主要更新任务明细 salerId、salerName、taskNum、taskDetailName、taskDesc、taskUnitPrice
                // taskTotalPrice、totalCommission
                detail.setSalerId(content.getString("salerId"));
                detail.setSalerName(content.getString("salerName"));
                detail.setSalerTMName(content.getString("salerTMName"));
                // 更新任务接收人
                if(!Strings.isNullOrEmpty(content.getString("recieverId"))) {
                    User reciever = iuser.getUser(content.getString("recieverId"));
                    if(reciever != null) {
                        detail.setRecieverId(reciever.getId());
                        detail.setRecieverName(reciever.getUserName());
                    }
                }
                detail.setTaskNum(content.getIntValue("taskNum"));
                detail.setTaskDetailName(content.getString("taskDetailName"));
                detail.setTaskDesc(content.getString("taskDesc"));
                detail.setTaskUnitPrice(content.getDoubleValue("taskUnitPrice"));
                if(content.getString("taskTotalPrice") == null || "0".equals(content.getString("taskTotalPrice"))) {
                    detail.setTaskTotalPrice(content.getDoubleValue("taskUnitPrice") * content.getIntValue("taskNum"));
                }
                // detail.setTaskTotalPrice(content.getDoubleValue("taskTotalPrice"));
                detail.setTotalCommission(content.getDoubleValue("totalCommission"));
                List<FileInfo> detailFiles = Lists.newArrayList();
                JSONArray detailFileArr = content.getJSONArray("detailFiles");
                if(detailFileArr != null) {
                    for(int i = 0; i < detailFileArr.size(); i++) {
                        detailFiles.add(JSONObject.parseObject(detailFileArr.get(i).toString(), FileInfo.class));
                    }
                }
                detail.setDetailFiles(detailFiles);

                // itask.updTaskDetail(detail);
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

            JSONObject content = InProcessContext.getRequestMsg().getContent();
            TaskInstance instance = itask.getTaskInstance(content.getString("taskId"));
            if(instance.getPublishStatus() != TaskInstance.NOT_PUBLISHED) {
                responseMsg = RespCodeMsgDepository.SAVE_TASK_ERROR.toResponseMsg();
                return;
            }
            TaskDetail detail = new TaskDetail();
            detail.setId(DefaultIdGenerator.getIdForStr());
            detail.setTaskId(content.getString("taskId"));
            detail.setTaskDetailName(content.getString("taskDetailName"));
            detail.setTaskDesc(content.getString("taskDesc"));
            detail.setTaskUnitPrice(content.getDoubleValue("taskUnitPrice"));
            detail.setTaskNum(content.getIntValue("taskNum"));
            if(content.getString("taskTotalPrice") == null || "0".equals(content.getString("taskTotalPrice"))) {
                detail.setTaskTotalPrice(content.getDoubleValue("taskUnitPrice") * content.getIntValue("taskNum"));
            }
            String recieverId = content.getString("recieverId");
            detail.setRecieverId(recieverId);
            String recieverName = content.getString("recieverName");
            if(Strings.isNullOrEmpty(recieverName) && !Strings.isNullOrEmpty(recieverId)) {
                User reciever = iuser.getUser(recieverId);
                if(reciever != null) {
                    detail.setRecieverName(reciever.getUserName());
                }
            }
            detail.setSalerId(content.getString("salerId"));
            detail.setSalerName(content.getString("salerName"));
            detail.setSalerTMName(content.getString("salerTMName"));
            detail.setStatus(TaskDetail.NOT_CLAIMED);
            detail.setTotalCommission(content.getDoubleValue("totalCommission"));
            detail.setCreateTime(new Date());
            List<FileInfo> detailFiles = Lists.newArrayList();
            JSONArray detailFileArr = content.getJSONArray("detailFiles");
            if(detailFileArr != null) {
                for(int i = 0; i < detailFileArr.size(); i++) {
                    detailFiles.add(JSONObject.parseObject(detailFileArr.get(i).toString(), FileInfo.class));
                }
            }
            detail.setDetailFiles(detailFiles);
            itask.saveTaskDetail(detail);

            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("任务明细保存失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/detail/delFiles")
    @ResponseBody
    public void delTaskDetailFiles(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            if(!checkUserAdmin(req)) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            // 删除具体详细任务文件列表
            JSONObject content = InProcessContext.getRequestMsg().getContent();
            String detailId = content.getString("id");

            List<String> fileIds = null;
            if(!Strings.isNullOrEmpty(content.getString("fileIds"))) {
                fileIds = Lists.newArrayList(content.getString("fileIds").split(","));
            }
            itask.delTaskDetailFiles(fileIds, detailId);
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("任务详细信文件删除失败", e);
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
