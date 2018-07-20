package com.augurit.awater.service.impl;

import com.augurit.awater.dao.SubjectMapper;
import com.augurit.awater.service.ISubject;
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
public class SubjectImpl implements ISubject {

	@Autowired
	private SubjectMapper subjectMapper;

	@Override
	public List<Map<String, Object>> findReservoirList(Map<String, Object> param) throws Exception {
		return subjectMapper.findReservoirList(param);
	}
}
