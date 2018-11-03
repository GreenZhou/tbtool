package com.augurit.awater.service;

import com.augurit.awater.entity.User;
import com.augurit.awater.exception.AppException;

import java.util.List;

/**
 * 说    明：  用户Service接口
 * 创 建 人： ebo
 * 创建日期： 2017-10-18 16:20
 * 修改说明：
 */
public interface IUser {
	User getUser(String loginNameOrId) throws AppException;
	List<User> findUserList(int operateUserType, String name) throws AppException;
	void saveUser(User user) throws AppException;
	void saveUserBatch(List<User> users) throws AppException;
	void delUsers(List<String> ids, int userType) throws AppException;
	void updUser(User user) throws AppException;
}
