package com.augurit.awater.dao;

import com.augurit.awater.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    User getUser(@Param("loginNameOrId") String loginNameOrId) throws Exception;
    List<User> findUserList(@Param("userType") int userType, @Param("name") String name) throws Exception;
    void saveUser(@Param("user") User user) throws Exception;
    void saveUserBatch(@Param("users") List<User> users) throws Exception;
    void delUsers(@Param("ids") List<String> ids, @Param("userType") int userType) throws Exception;
    void updUser(@Param("user") User user) throws Exception;
}
