package com.augurit.awater.controller;

import com.alibaba.fastjson.JSONObject;
import com.augurit.awater.DefaultIdGenerator;
import com.augurit.awater.InProcessContext;
import com.augurit.awater.RespCodeMsgDepository;
import com.augurit.awater.ResponseMsg;
import com.augurit.awater.entity.FileInfo;
import com.augurit.awater.entity.User;
import com.augurit.awater.service.IFile;
import com.augurit.awater.service.IUser;
import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 说    明： 上传文件模块接口
 * 创 建 人： ebo
 * 创建日期： 2017-11-22 13:00
 * 修改说明：
 */
@RestController
@RequestMapping("/upload")
public class UploadFileController {

	private static final Logger log = Logger.getLogger(UploadFileController.class);

	@Autowired
	private IFile iFile;

	@Autowired
	private IUser iUser;

	private String filePath = "C:/awater/upload/";

	@RequestMapping(value = "/file", method= RequestMethod.POST)
	public void upload(HttpServletRequest req, @RequestParam("file") MultipartFile file) throws Exception {
		ResponseMsg responseMsg = RespCodeMsgDepository.UPLOAD_FAILED.toResponseMsg();

		try {
			FileInfo fileInfo = excuteUpload(null, file, req);

			if(fileInfo != null) {
				JSONObject content = new JSONObject();
				content.put("fileInfo", fileInfo);
				responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, content);
			}
		} catch(Exception e) {
			log.error("上传附件[" + file.getOriginalFilename() + "]失败...", e);
		}

		InProcessContext.setResponseMsg(responseMsg);

	}

	@RequestMapping(value = "/files", method= RequestMethod.POST)
	public void uploadFiles(HttpServletRequest req, @RequestParam("file") MultipartFile[] files) throws Exception {
		ResponseMsg responseMsg = RespCodeMsgDepository.UPLOAD_FAILED.toResponseMsg();

		List<FileInfo> successFiles = new ArrayList<FileInfo>();
		for(MultipartFile file : files) {
			try {
				FileInfo fileInfo = excuteUpload(null, file, req);

				if(fileInfo != null) {
					successFiles.add(fileInfo);
				}
			} catch(Exception e) {
				log.error("上传附件[" + file.getOriginalFilename() + "]失败...", e);
			}
		}

		if(CollectionUtils.isNotEmpty(successFiles)) {
			JSONObject ret = new JSONObject();
			ret.put("list", successFiles);
			responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, ret);
		}

		InProcessContext.setResponseMsg(responseMsg);
	}

	private FileInfo excuteUpload(String uploadDir, MultipartFile file, HttpServletRequest req) throws Exception {
		if(Strings.isNullOrEmpty(uploadDir)) {
			uploadDir = filePath;
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
		File serverFile = new File(uploadDir + newFilename);
		// 将上传的文件写入到服务器端文件内
		file.transferTo(serverFile);

		FileInfo fileInfo = new FileInfo();
		fileInfo.setId(id);
		fileInfo.setOriginalFilename(originalFilename);
		fileInfo.setSuffix(suffix);
		fileInfo.setFileSize(Math.floor(file.getSize()/1024d + 0.5) + "KB");
		User user = (User) req.getSession().getAttribute(req.getParameter("token"));
		if(user != null) {
			fileInfo.setCreatorLoginName(user.getLoginName());
			fileInfo.setCreatorUserName(user.getUserName());
		}
		iFile.saveFile(fileInfo);

		return fileInfo;
	}
}
