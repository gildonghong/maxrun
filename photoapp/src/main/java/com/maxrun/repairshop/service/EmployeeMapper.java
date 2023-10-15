package com.maxrun.repairshop.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper {

	public List<Map<String, Object>> getWorkerList(Map<String, Object> param) throws SQLException;
	public Map<String, Object> regWorker(Map<String, Object> param) throws SQLException;
}
