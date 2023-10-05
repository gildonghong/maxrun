package com.maxrun.repairshop.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RepairShopMapper {
	public List<Map<String, Object>> getRepairShopList() throws SQLException;
	public Map<String, Object> getRepairShopInfo(int repairShopNo) throws SQLException;
	public Map<String, Object> regRepairShop(Map<String, Object> param) throws SQLException;
	public List<Map<String, Object>> getDepartmentList(int repairShopNo) throws SQLException;
	public Map<String, Object> regDepartment(int repairShopNo) throws SQLException;
}
