package com.augurit.awater.dao;

import com.augurit.awater.entity.Saler;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalerMapper {
    Saler getSaler(@Param("id") String id) throws Exception;
    List<Saler> findSalerList() throws Exception;
    void saveSaler(@Param("saler") Saler saler) throws Exception;
    void delSaler(@Param("id") String id) throws Exception;
    void updSaler(@Param("saler") Saler saler) throws Exception;
}