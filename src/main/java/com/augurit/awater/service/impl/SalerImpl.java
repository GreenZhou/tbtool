package com.augurit.awater.service.impl;

import com.augurit.awater.RespCodeMsgDepository;
import com.augurit.awater.dao.UserMapper;
import com.augurit.awater.entity.Saler;
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
public class SalerImpl implements ISaler {

	@Autowired
	private SalerMapper salerMapper;

	@Override
	public User getSaler(String id) throws AppException {
		Saler saler = null;

		try {
			saler = salerMapper.getSaler(id);
		} catch (Exception e) {
			throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "获取商家信息失败", e);
		}

		return saler;
	}

	@Override
	public List<Saler> findSalerList() throws AppException {
		List<Saler> salers = null;

		try {
			salers = salerMapper.findSalerList();
		} catch (Exception e) {
			throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "查询商家信息失败", e);
		}

		return salers;
	}

	@Override
	public void saveSaler(Saler saler) throws AppException {
		try {
			salerMapper.saveSaler(saler);
		} catch (Exception e) {
			throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "添加商家信息失败", e);
		}
	}

	@Override
	public void delSaler(String id) throws AppException {
		try {
			salerMapper.delSaler(id);
		} catch (Exception e) {
			throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "删除商家信息失败", e);
		}
	}

	@Override
	public void updSaler(Saler saler) throws AppException {
		try {
			salerMapper.updSaler(saler);
		} catch (Exception e) {
			throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "修改商家信息失败", e);
		}
	}
}
