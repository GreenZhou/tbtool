package com.augurit.awater.dao;

import com.augurit.awater.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    User getUser(@Param("loginName") String loginName) throws Exception;
    List<User> findUserList(@Param("userType") int userType) throws Exception;
    void saveUser(@Param("user") User user) throws Exception;
    void delUser(@Param("id") String id, @Param("userType") int userType) throws Exception;
    void updUser(@Param("user") User user) throws Exception;
}
