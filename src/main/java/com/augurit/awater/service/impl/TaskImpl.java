package com.augurit.awater.service.impl;

import com.augurit.awater.RespCodeMsgDepository;
import com.augurit.awater.dao.FileMapper;
import com.augurit.awater.dao.TaskMapper;
import com.augurit.awater.dao.UserMapper;
import com.augurit.awater.entity.FileInfo;
import com.augurit.awater.entity.TaskDetail;
import com.augurit.awater.entity.TaskInstance;
import com.augurit.awater.entity.User;
import com.augurit.awater.exception.AppException;
import com.augurit.awater.service.ITask;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskImpl implements ITask {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FileMapper fileMapper;

    @Override
    public TaskInstance getTaskInstance(String id) throws AppException {
        TaskInstance instance;

        try {
            instance = taskMapper.getTaskInstance(id);
        } catch(Exception e) {
            throw new AppException(RespCodeMsgDepository.TASK_NOT_EXISTS, "任务不存在");
        }

        return instance;
    }

    @Override
    public void saveTaskInstance(TaskInstance instance) throws AppException {
        try {
            taskMapper.saveTaskInstance(instance);
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.TASK_PUBLISHED_FAILED, "任务发布失败");
        }
    }

    @Override
    public void publishTaskInstance(String id, List<TaskDetail> details) throws AppException {

    }

    @Override
    public List<TaskInstance> findTaskInstanceList(String recieverId, String creatorId, String taskName) throws AppException {
        try {
            return taskMapper.findTaskInstanceList(recieverId, creatorId, taskName);
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "任务查询失败");
        }
    }

    @Override
    public void updateTaskInstance(TaskInstance instance) throws AppException {
        try {
            taskMapper.updateTaskInstance(instance);
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "任务更新失败");
        }
    }

    @Override
    public void delTaskInstance(String id) throws AppException {
        try {
            taskMapper.delTaskInstance(id);
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "任务删除失败");
        }
    }

    @Override
    public void abandonTaskInstance(TaskInstance instance) throws AppException {
        if(CollectionUtils.isNotEmpty(instance.getDetails())) {
            for(TaskDetail detail : instance.getDetails()) {
                try {
                    taskMapper.abandonTaskDetail(detail);
                } catch (Exception e) {
                    throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "任务明细废弃状态更新失败");
                }
            }
        }

        try {
            taskMapper.abandonTaskInstance(instance);
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "任务废弃状态更新失败");
        }
    }

    @Override
    public void assignTaskInstance(String id, List<String> customerIds, int customerNum) throws AppException {

    }

    @Override
    public List<User> getTaskRecievers(String instanceId) throws AppException {
        try {
            TaskInstance instance = taskMapper.getTaskInstance(instanceId);
            if(Strings.isNullOrEmpty(instance.getRecieverIds())) {
                return Lists.newArrayList();
            }
            return userMapper.getUserByIds(Lists.newArrayList(instance.getRecieverIds().split(",")));
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "获取任务接收人范围列表数据失败");
        }
    }

    @Override
    public List<TaskDetail> findTaskDetailList(User user, String instanceId, String taskDetailName, List<String> excludeCols) throws AppException {
        try {
            List<TaskDetail> details = taskMapper.findTaskDetailList(user, instanceId, taskDetailName, excludeCols);
            if(CollectionUtils.isNotEmpty(excludeCols)) {
                // TODO 屏蔽掉没权限的属性字段
            }

            // 任务明细文件列表
            for(TaskDetail detail : details) {
                detail.setDetailFiles(fileMapper.findFileInfosByTaskDetailId(detail.getId()));
            }
            return details;
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "任务明细列表查询失败");
        }
    }

    @Override
    public void updTaskDetail(TaskDetail detail) throws AppException {
        try {
            List<FileInfo> willDeletedFiles = fileMapper.findFileInfosByTaskDetailId(detail.getId());
            if(CollectionUtils.isNotEmpty(willDeletedFiles)) {
                for(FileInfo fileInfo : willDeletedFiles) {
                    fileMapper.removeTaskDetailFile(detail.getId(), fileInfo.getId());
                }
            }

            taskMapper.updTaskDetail(detail);

            if(CollectionUtils.isNotEmpty(detail.getDetailFiles())) {
                List<String> fileIds = Lists.newArrayList();

                for(FileInfo fileInfo : detail.getDetailFiles()) {
                    fileIds.add(fileInfo.getId());
                }

                fileMapper.saveTaskDetailFiles(detail.getId(), fileIds);
            }
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "任务明细更新除失败");
        }
    }

    @Override
    public void delTaskDetail(List<String> ids, String instanceId) throws AppException {
        try {
            taskMapper.delTaskDetail(ids, instanceId);
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "任务明细删除失败");
        }
    }

    @Override
    public void delTaskDetailFiles(List<String> fileIds, String detailId) throws AppException {
        try {
            fileMapper.removeFiles(fileIds);
            taskMapper.delTaskDetailFiles(fileIds, detailId);
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "任务明细文件删除失败");
        }
    }

    @Override
    public void saveTaskDetail(TaskDetail detail) throws AppException {
        try {
            taskMapper.saveTaskDetail(detail);
            List<FileInfo> detailFiles = detail.getDetailFiles();
            if(CollectionUtils.isNotEmpty(detailFiles)) {
                List<String> fileIds = Lists.newArrayList();

                for(FileInfo fileInfo : detailFiles) {
                    fileIds.add(fileInfo.getId());
                }

                fileMapper.saveTaskDetailFiles(detail.getId(), fileIds);
            }
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "任务明细添加失败");
        }
    }

    @Override
    public void saveTaskDetailBatch(List<TaskDetail> details) throws AppException {
        try {
            taskMapper.saveTaskDetailBatch(details);
            for(TaskDetail detail : details) {
                List<FileInfo> detailFiles = detail.getDetailFiles();
                if(CollectionUtils.isNotEmpty(detailFiles)) {
                    List<String> fileIds = Lists.newArrayList();

                    for(FileInfo fileInfo : detailFiles) {
                        fileIds.add(fileInfo.getId());
                    }

                    fileMapper.saveTaskDetailFiles(detail.getId(), fileIds);
                }
            }
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "任务明细导入失败");
        }
    }

    @Override
    public void abandonTaskDetail(User user, String instanceId, String id) throws AppException {

    }
}
