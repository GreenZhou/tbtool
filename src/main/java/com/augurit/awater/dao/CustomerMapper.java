package com.augurit.awater.dao;

import com.augurit.awater.entity.Customer;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerMapper {
    Customer getCustomer(@Param("id") String id) throws Exception;
    List<Customer> findCustomerList() throws Exception;
    void saveCustomer(@Param("customer") Customer Customer) throws Exception;
    void delCustomer(@Param("id") String id) throws Exception;
    void updCustomer(@Param("customer") Customer customer) throws Exception;
}