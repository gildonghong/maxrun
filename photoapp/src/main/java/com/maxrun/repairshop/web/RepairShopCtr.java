package com.maxrun.repairshop.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.maxrun.repairshop.service.RepairShopService;

@Controller
public class RepairShopCtr {
	
	@Autowired
	private RepairShopService repairShopService;
	
	public int repairShop(Map<String, Object> param) throws Exception{
		return repairShopService.regRepairShop(param);
	}
	
	public Map<String, Object> getRepairShopInfo(Map<String, Object> param) throws Exception{
		return repairShopService.getRepairShopInfo(param);
	}
	
}
