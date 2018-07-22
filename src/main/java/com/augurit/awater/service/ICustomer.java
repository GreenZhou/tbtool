package com.augurit.awater.service;

import com.augurit.awater.entity.Customer;
import com.augurit.awater.exception.AppException;

import java.util.List;

/**
 * 说    明：  用户Service接口
 * 创 建 人： ebo
 * 创建日期： 2017-10-18 16:20
 * 修改说明：
 */
public interface ICustomer {
	Customer getCustomer(@Param("id") String id) throws AppException;
    List<Customer> findCustomerList() throws AppException;
    void saveCustomer(@Param("customer") Customer Customer) throws AppException;
    void delCustomer(@Param("id") String id) throws AppException;
    void updCustomer(@Param("customer") Customer customer) throws AppException;
}