package com.maxrun.carcare.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CarCareMapper {

	public List<Map<String, Object>> getCarList(Map<String, Object> param) throws SQLException;
}
