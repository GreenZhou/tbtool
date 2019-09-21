package com.augurit.awater.controller;

import com.augurit.awater.RespCodeMsgDepository;
import com.augurit.awater.entity.FileInfo;
import com.augurit.awater.service.IFile;
import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * 说    明： 附件下载接口
 * 创 建 人： ebo
 * 创建日期： 2017-11-22 19:54
 * 修改说明：
 */
@RestController
@RequestMapping("/download")
public class DownloadFileController {
	private static final Logger log = Logger.getLogger(DownloadFileController.class);

	@Autowired
	private IFile iFile;

	private String filePath = "C:/awater/upload/";

	@RequestMapping(value = "/file/{id}", method= RequestMethod.GET)
	public void download(HttpServletResponse resp, @PathVariable("id") String id) throws Exception {
		InputStream is = null;
		try {
			// 获取文件信息
			FileInfo fileInfo = iFile.getFileInfo(id);

			String originalFileName = URLEncoder.encode(fileInfo.getOriginalFilename(), "UTF-8");
			originalFileName = originalFileName.replaceAll("\\+", " ");
			File file = new File(filePath + fileInfo.getId() + fileInfo.getSuffix());
			if(!file.exists()) {
				File localFile = new File(filePath + fileInfo.getId() + fileInfo.getSuffix());
				if(file.getCanonicalPath().equals(localFile.getCanonicalPath())) {
					file.createNewFile();
				} else {
					Files.copy(file, localFile);
				}
			}
			is = new FileInputStream(file);
			resp.setContentType("application/octet-stream");
			resp.setHeader("Content-Disposition", "attachment; filename=\"" + originalFileName + "\"");

			// 将成功上传的附件信息返回回去
			IOUtils.copy(is, resp.getOutputStream());
		} catch(Exception e) {
			log.error("下载文件出错...", e);
			// 异常处理
			resp.setContentType("text/plain; charset=UTF-8");
			resp.resetBuffer();
			IOUtils.write(RespCodeMsgDepository.DOWNLOAD_FAILED.toResponseMsg().toString()
					.getBytes("utf-8"), resp.getOutputStream());
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
}
