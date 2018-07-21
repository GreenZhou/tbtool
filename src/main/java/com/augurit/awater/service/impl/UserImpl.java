package com.augurit.awater.service.impl;

import com.augurit.awater.RespCodeMsgDepository;
import com.augurit.awater.dao.UserMapper;
import com.augurit.awater.entity.User;
import com.augurit.awater.exception.AppException;
import com.augurit.awater.service.IUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 说    明：
 * 创 建 人： ebo
 * 创建日期： 2017-10-18 16:40
 * 修改说明：
 */
@Service
public class UserImpl implements IUser {

	@Autowired
	private UserMapper userMapper;

	@Override
	public User getUser(String loginName) throws AppException {
		User user = null;

		try {
			user = userMapper.getUser(loginName);
		} catch (Exception e) {
			throw new AppException(RespCodeMsgDepository.USER_LOGIN_ERROR, "用户【" + loginName + "】登录失败", e);
		}

		return user;
	}

	@Override
	public List<User> findUserList(int operateUserType) throws AppException {
		List<User> users = null;

		try {
			users = userMapper.findUserList(operateUserType);
		} catch (Exception e) {
			throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "查询用户失败", e);
		}

		return users;
	}

	@Override
	public void saveUser(User user) throws AppException {
		try {
			userMapper.saveUser(user);
		} catch (Exception e) {
			throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "添加用户失败", e);
		}
	}

	@Override
	public void delUser(String id, int userType) throws AppException {
		try {
			userMapper.delUser(id, userType);
		} catch (Exception e) {
			throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "删除用户失败", e);
		}
	}

	@Override
	public void updUser(User user) throws AppException {
		try {
			userMapper.updUser(user);
		} catch (Exception e) {
			throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "修改用户失败", e);
		}
	}
}
