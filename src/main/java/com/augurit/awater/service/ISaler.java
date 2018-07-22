package com.augurit.awater.service;

import com.augurit.awater.entity.Saler;
import com.augurit.awater.exception.AppException;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 说    明：  用户Service接口
 * 创 建 人： ebo
 * 创建日期： 2017-10-18 16:20
 * 修改说明：
 */
public interface ISaler {
	Saler getSaler(@Param("id") String id) throws AppException;
    List<Saler> findSalerList() throws AppException;
    void saveSaler(@Param("saler") Saler saler) throws AppException;
    void delSaler(@Param("id") String id) throws AppException;
    void updSaler(@Param("saler") Saler saler) throws AppException;
}