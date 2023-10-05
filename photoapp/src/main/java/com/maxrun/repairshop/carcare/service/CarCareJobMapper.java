package com.maxrun.repairshop.carcare.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CarCareJobMapper {

	public List<Map<String, Object>>getCarCareList(Map<String, Object> param)throws SQLException;
	public List<Map<String, Object>>getJobList(Map<String, Object> param)throws SQLException;
	
	public Map<String, Object> regCarCareJob(Map<String, Object> param)throws SQLException;
	public Map<String, Object> getJobDetails(Map<String, Object> param)throws SQLException;
	
}
