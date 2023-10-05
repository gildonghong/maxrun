package com.maxrun.repairshop.carcare.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CarCarePerformanceMapper {

	public List<Map<String, Object>>getPerformanceList(Map<String, Object> param)throws SQLException;
}
