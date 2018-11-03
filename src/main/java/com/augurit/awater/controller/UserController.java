package com.augurit.awater.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.augurit.awater.DefaultIdGenerator;
import com.augurit.awater.InProcessContext;
import com.augurit.awater.RespCodeMsgDepository;
import com.augurit.awater.ResponseMsg;
import com.augurit.awater.entity.FileInfo;
import com.augurit.awater.entity.User;
import com.augurit.awater.exception.AppException;
import com.augurit.awater.poi.UserProcessor;
import com.augurit.awater.service.IFile;
import com.augurit.awater.service.IUser;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private final static Logger LOGGER = Logger.getLogger(UserController.class);

    //  管理员用户类型
    private final static int ADMINISTRATOR = 0;
    //  普通员工用户类型
    private final static int STAFF = 1;
    //  普通买家用户类型
    private final static int CUSTOMER = 2;

    @Value(value = "${defaultUploadPath}")
    private String defaultUploadPath;

    @Autowired
    private IUser iuser;

    @Autowired
    private IFile ifile;

    @RequestMapping("/login")
    @ResponseBody
    public void login(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            JSONObject content = InProcessContext.getRequestMsg().getContent();
            // ToDo 校验请求报文中数据
            User user = iuser.getUser(content.getString("loginName"));
            if(user == null) {
                responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
            } else {
                String passwd = content.getString("passwd");
                if(user.getPasswd().equalsIgnoreCase(passwd)) {
                    JSONObject respUserData = (JSONObject) JSONObject.toJSON(user);
                    String token = DefaultIdGenerator.getIdForStr();
                    // ToDo 不用HttpServletSession存放
                    req.getSession().setAttribute(token, user);
                    respUserData.put("token", token);
                    responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, respUserData);
                } else {
                    responseMsg = RespCodeMsgDepository.USER_LOGIN_FAIL.toResponseMsg();
                }
            }
        } catch (Exception e) {
            LOGGER.error("用户登录失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/logout")
    @ResponseBody
    public void logout(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            String token = InProcessContext.getRequestMsg().getToken();
            // ToDo 校验请求报文中数据
            req.getSession().removeAttribute(token);
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("用户退出失败", e);
            responseMsg = RespCodeMsgDepository.USER_LOGOUT_FAIL.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/list")
    @ResponseBody
    public void listUsers(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            int operateUserType = checkUserPrivAndRetOperateUserType(req);
            if(operateUserType == -1) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

			String name = InProcessContext.getRequestMsg().getContent().getString("name");
            JSONArray users = (JSONArray) JSONArray.toJSON(iuser.findUserList(operateUserType, name));
            JSONObject ret = new JSONObject();
            ret.put("totalCount", InProcessContext.getPageParameter().getTotalCount());
            ret.put("pageSize", InProcessContext.getPageParameter().getPageSize());
            ret.put("list", users);
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, ret);
        } catch (Exception e) {
            LOGGER.error("用户查询失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/save")
    @ResponseBody
    public void saveUser(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            // 判断当前登录用户是否具备添加用户权限
            int operateUserType = checkUserPrivAndRetOperateUserType(req);
            if(operateUserType == -1) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            JSONObject content = InProcessContext.getRequestMsg().getContent();
            User user = new User();
            user.setUserType(operateUserType);
            user.setUserName(content.getString("userName"));
            user.setLoginName(content.getString("loginName"));
            user.setPasswd(StringUtils.defaultIfBlank(content.getString("passwd"), "80f4189ca1c9d4d9"));// 默认密码是88888888
            user.setId(DefaultIdGenerator.getIdForStr());
            iuser.saveUser(user);

            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("用户保存失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/importUsers")
    @ResponseBody
    public void importUsers(@RequestParam("file") MultipartFile file, @RequestParam("token") String token, HttpServletRequest req) {
		ResponseMsg responseMsg = null;

        try {
			// 校验token
			User user = (User) req.getSession().getAttribute(token);
			if(user == null) {
				LOGGER.error("token[" + token + "] 无效");
				InProcessContext.setResponseMsg(RespCodeMsgDepository.TOKEN_INVALID.toResponseMsg());
				return;
			}

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
            File serverFile = new File(this.defaultUploadPath + newFilename);
            // 将上传的文件写入到服务器端文件内
            file.transferTo(serverFile);

            FileInfo fileInfo = new FileInfo();
            fileInfo.setId(id);
            fileInfo.setOriginalFilename(originalFilename);
            fileInfo.setSuffix(suffix);
            fileInfo.setFileSize(Math.floor(file.getSize()/1024d + 0.5) + "KB");
            fileInfo.setIsActive("0");
            fileInfo.setDirPath(this.defaultUploadPath + newFilename);
            fileInfo.setCreatorLoginName(user.getLoginName());
            fileInfo.setCreatorUserName(user.getUserName());
            ifile.saveFile(fileInfo);

            iuser.saveUserBatch(UserProcessor.importFileToUsers(serverFile, user));

            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("用户导入失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/del")
    @ResponseBody
    public void delUsers(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            // 判断当前登录用户是否具备添加用户权限
            int operateUserType = checkUserPrivAndRetOperateUserType(req);
            if(operateUserType == -1) {
                responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                return;
            }

            JSONObject content = InProcessContext.getRequestMsg().getContent();
            List<String> ids = Lists.newArrayList(content.getString("ids").split(","));
            iuser.delUsers(ids, operateUserType);
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("用户删除失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    @RequestMapping("/update")
    @ResponseBody
    public void updUser(HttpServletRequest req) {
        ResponseMsg responseMsg = null;
        try {
            JSONObject content = InProcessContext.getRequestMsg().getContent();
            User user = new User();
            user.setId(content.getString("id"));
            user.setLoginName(content.getString("loginName"));
            user.setUserName(content.getString("userName"));
            if(!"1".equals(content.getString("isOwn"))) {
                // 判断当前登录用户是否具备添加用户权限
                int operateUserType = checkUserPrivAndRetOperateUserType(req);
                if(operateUserType == -1) {
                    responseMsg = RespCodeMsgDepository.LACK_PRIVILEGIER.toResponseMsg();
                    return;
                }
                user.setUserType(operateUserType);
            } else {
                user.setPasswd(content.getString("passwd"));
                user.setUserType(-1);
            }

            iuser.updUser(user);
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, null);
        } catch (Exception e) {
            LOGGER.error("用户更新失败", e);
            responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();
        } finally {
            InProcessContext.setResponseMsg(responseMsg);
        }
    }

    // ToDo 这段有点难以理解
    private int checkUserPrivAndRetOperateUserType(HttpServletRequest req) {
        String token = InProcessContext.getRequestMsg().getToken();
        // ToDo 校验请求报文中数据
        User user = (User) req.getSession().getAttribute(token);
        if(user.getUserType() == ADMINISTRATOR) {
            return UserController.STAFF;
        } else if(user.getUserType() == UserController.STAFF) {
            return CUSTOMER;
        }
        return -1;
    }
}
