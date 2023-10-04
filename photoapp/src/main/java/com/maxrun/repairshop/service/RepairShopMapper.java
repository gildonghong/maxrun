package com.maxrun.repairshop.service;

import java.sql.SQLException;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RepairShopMapper {
	public int regRepairShop(Map<String, Object> param) throws SQLException;
	public Map<String, Object> getRepairShopInfo(Map<String, Object> param) throws SQLException;
}
