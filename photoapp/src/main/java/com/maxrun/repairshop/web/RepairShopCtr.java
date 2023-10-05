package com.maxrun.repairshop.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maxrun.repairshop.service.RepairShopService;

@Controller
public class RepairShopCtr {
	
	@Autowired
	private RepairShopService repairShopService;
	
	@ResponseBody
	@PostMapping("/repairshop")
	public Map<String, Object> regRepairShop(Map<String, Object> param) throws Exception{
		return repairShopService.regRepairShop(param);
	}
	@ResponseBody
	@GetMapping("/repairshop/{repairShopNo}")
	public Map<String, Object> getRepairShopInfo(@PathVariable int repairShopNo) throws Exception{
		return repairShopService.getRepairShopInfo(repairShopNo);
	}
	
	@ResponseBody
	@PostMapping("/repairshop/department")
	public Map<String, Object> regDepartment(Map<String, Object> param) throws Exception{
		return null;
	}
	
}
