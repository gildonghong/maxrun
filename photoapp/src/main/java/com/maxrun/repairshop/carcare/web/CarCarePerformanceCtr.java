package com.maxrun.repairshop.carcare.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maxrun.repairshop.carcare.service.CarCarePerformanceService;

@Controller
public class CarCarePerformanceCtr {

	@Autowired
	private CarCarePerformanceService carCarePerformanceService;
	
	@ResponseBody
	@GetMapping("/repairshop/performance/list")
	public List<Map<String, Object>>getPerformanceList(@RequestParam Map<String, Object> param)throws Exception{
		return carCarePerformanceService.getPerformanceList(param);
	}
}
