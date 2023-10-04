package com.maxrun.repairshop.service;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepairShopService {
	
	@Autowired
	private RepairShopMapper repairShopMapper;
	
	public int regRepairShop(Map<String, Object> param) throws Exception{
		return repairShopMapper.regRepairShop(param);
	}
	
	public Map<String, Object> getRepairShopInfo(Map<String, Object> param) throws Exception{
		return repairShopMapper.getRepairShopInfo(param);
	}
}
