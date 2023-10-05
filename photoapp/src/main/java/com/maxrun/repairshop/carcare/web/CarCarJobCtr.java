package com.maxrun.repairshop.carcare.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maxrun.repairshop.carcare.service.CarCareJobService;

@Controller
public class CarCarJobCtr {
	@Autowired
	CarCareJobService carCareJobService;
	
	@ResponseBody
	@GetMapping("/repairshop/carcare/list")
	public List<Map<String, Object>>getCarCareList(Map<String, Object> param)throws Exception{
		return carCareJobService.getCarCareList(param);
	}
	
	@ResponseBody
	@GetMapping("/repairshop/carcare/job/list")
	public Map<String, Object>getJobList(Map<String, Object> param)throws Exception{
		return carCareJobService.regCarCareJob(param);
	}
	
	@ResponseBody
	@PostMapping("/repairshop/carcare/job")
	public Map<String, Object> regCarCareJob(Map<String, Object> param)throws Exception{
		return carCareJobService.regCarCareJob(param);
	}
}
