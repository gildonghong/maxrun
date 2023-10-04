package com.maxrun.repairshop.carcare.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maxrun.repairshop.carcare.service.CarCareService;

@Controller
public class CarCareCtr {
	
	@Autowired
	CarCareService carCareService;
	
	@ResponseBody
	@RequestMapping("/search")
	public List<Map<String, Object>> getCarList(@RequestParam Map<String, Object> param){
		List<Map<String, Object>> ret = null;
		
		try {
			ret = carCareService.getCarList(param);
		}catch(Exception ex) {
			ret = new ArrayList<Map<String, Object>>();
			Map<String, Object> mp = new HashMap<String, Object>();
			mp.put("errDesc", "오류가발생하였습니다. 고객센터에 문의하십시오!");
			ex.printStackTrace();
		}
		return ret;
	}

}
