package com.augurit.awater.dao;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SubjectMapper {

    /**
     * 分页查询水库水情列表(带上测站x,y)
     */
    List<Map<String, Object>> findReservoirList(Map<String, Object> map) throws Exception;

}
