package com.augurit.awater.service.impl;

import com.augurit.awater.RespCodeMsgDepository;
import com.augurit.awater.dao.CustomerMapper;
import com.augurit.awater.entity.Customer;
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
public class Customermpl implements ICustomer {

	@Autowired
	private CustomerMapper customerMapper;

	@Override
	public Customer getCustomer(String id) throws AppException {
		Customer customer = null;

		try {
			customer = customerMapper.getCustomer(id);
		} catch (Exception e) {
			throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "获取买家信息失败", e);
		}

		return customer;
	}

	@Override
	public List<Customer> findCustomerList() throws AppException {
		List<Customer> customers = null;

		try {
			customers = customerMapper.findCustomerList();
		} catch (Exception e) {
			throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "查询买家信息失败", e);
		}

		return customers;
	}

	@Override
	public void saveCustomer(Customer customer) throws AppException {
		try {
			customerMapper.saveCustomer(customer);
		} catch (Exception e) {
			throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "添加买家信息失败", e);
		}
	}

	@Override
	public void delCustomer(String id) throws AppException {
		try {
			customerMapper.delCustomer(id);
		} catch (Exception e) {
			throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "删除买家信息失败", e);
		}
	}

	@Override
	public void updCustomer(Customer customer) throws AppException {
		try {
			customerMapper.updCustomer(customer);
		} catch (Exception e) {
			throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "修改买家信息失败", e);
		}
	}
}
