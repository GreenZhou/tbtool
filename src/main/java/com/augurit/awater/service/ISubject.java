package com.augurit.awater.service;

import java.util.List;
import java.util.Map;

/**
 * 说    明：
 * 创 建 人： ebo
 * 创建日期： 2017-10-18 16:20
 * 修改说明：
 */
public interface ISubject {
	List<Map<String, Object>> findReservoirList(Map<String, Object> param) throws Exception;
}
