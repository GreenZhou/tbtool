package com.augurit.awater.controller;

import com.alibaba.fastjson.JSONObject;
import com.augurit.awater.InProcessContext;
import com.augurit.awater.RespCodeMsgDepository;
import com.augurit.awater.ResponseMsg;
import com.augurit.awater.entity.FileInfo;
import com.augurit.awater.service.IFile;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    private static final Logger log = Logger.getLogger(FileController.class);

    @Autowired
    private IFile iFile;

    @RequestMapping(value = "/listFiles")
    public void listFiles() {
        ResponseMsg responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();

        try {
            JSONObject content = InProcessContext.getRequestMsg().getContent();
            List<FileInfo> fileInfos = iFile.findFileInfoList(null, content.getString("fileName"));
            JSONObject ret = new JSONObject();
            ret.put("totalCount", InProcessContext.getPageParameter().getTotalCount());
            ret.put("pageSize", InProcessContext.getPageParameter().getPageSize());
            ret.put("list", fileInfos);
            responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, ret);
        } catch (Exception e) {
            log.error("查询文件信息失败", e);
        }

        InProcessContext.setResponseMsg(responseMsg);
    }

    @RequestMapping(value = "/delFiles")
    public void delFiles() {
        ResponseMsg responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();

        try {
            JSONObject content = InProcessContext.getRequestMsg().getContent();
            String ids = content.getString("ids");
            if(StringUtils.isBlank(ids)) {
                InProcessContext.setResponseMsg(RespCodeMsgDepository.SUCCESS.toResponseMsg());
                return;
            }

            List<String> fileIds = new ArrayList<String>();
            for(String id : ids.split(",")) {
                if(StringUtils.isNotBlank(id)) {
                    fileIds.add(id);
                }
            }

            if(CollectionUtils.isNotEmpty(fileIds)) {
                iFile.delFileInfos(fileIds);
            }

            responseMsg = RespCodeMsgDepository.SUCCESS.toResponseMsg();
        } catch(Exception e) {
            log.error("文件资源删除失败", e);
        }

        InProcessContext.setResponseMsg(responseMsg);
    }

    @RequestMapping(value = "/delFile/{id}")
    public void delFiles(@PathVariable("id") String id) {
        ResponseMsg responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();

        try {
            if(StringUtils.isBlank(id)) {
                InProcessContext.setResponseMsg(RespCodeMsgDepository.SUCCESS.toResponseMsg());
                return;
            }

            iFile.delFileInfos(Lists.newArrayList(id));

            responseMsg = RespCodeMsgDepository.SUCCESS.toResponseMsg();
        } catch(Exception e) {
            log.error("文件资源删除失败", e);
        }

        InProcessContext.setResponseMsg(responseMsg);
    }

}
