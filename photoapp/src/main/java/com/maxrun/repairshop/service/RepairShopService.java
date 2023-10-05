package com.maxrun.repairshop.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepairShopService {
//	public List<Map<String, Object>> getRepairShopList() throws SQLException;
//	public List<Map<String, Object>> getDepartmentList(int repairShopNo) throws SQLException;
//	public Map<String, Object> regRepairShop(Map<String, Object> param) throws SQLException;
//	public Map<String, Object> getRepairShopInfo(int repairShopNo) throws SQLException;
//	public Map<String, Object> regDepartment(int repairShopNo) throws SQLException;	
	@Autowired
	private RepairShopMapper repairShopMapper;

	public List<Map<String, Object>> getRepairShopList() throws Exception{
		return repairShopMapper.getRepairShopList();
	}
	
	public Map<String, Object> getRepairShopInfo(int repairShopNo) throws Exception{
		return repairShopMapper.getRepairShopInfo(repairShopNo);
	}
	
	public Map<String, Object> regRepairShop(Map<String, Object> param) throws Exception{
		return repairShopMapper.regRepairShop(param);
	}

	public List<Map<String, Object>> getDepartmentList(int repairShopNo) throws Exception{
		return repairShopMapper.getDepartmentList(repairShopNo);
	}
	
	public Map<String, Object> regDepartment(int repairShopNo) throws Exception{
		return repairShopMapper.regDepartment(repairShopNo);
	}
}
