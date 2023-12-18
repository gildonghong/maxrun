package com.maxrun.common.photoapp.test.web;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maxrun.common.photoapp.test.service.CommonService;

@Controller
public class CommonCtr {
	
	@Autowired
	private CommonService commonService;
	
	@RequestMapping("/point/list")
	public String getPointScoreList(@RequestParam Map<String, Object> param, Model model, HttpServletRequest req) {
		// 현재 날짜 구하기 (시스템 시계, 시스템 타임존)
		LocalDate now = LocalDate.now();
		
		DayOfWeek dayOfWeek = now.getDayOfWeek();	//요일
		
		while(!now.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
			now = now.minusDays(1);
		}
		
		if (param.get("strtDate")==null) {
			param.put("strtDate", now.toString());
		}
		
		if (param.get("endDate")==null) {
			param.put("endDate", now.plusDays(6).toString());
		}

		List<Map<String, Object>> lst = commonService.getPointScoreList(param);

		model.addAttribute("pointList", lst);
		model.addAttribute("strtDate", param.get("strtDate"));
		model.addAttribute("endDate", param.get("endDate"));
		
		return "temp/point-score-list";
	}
	
	@RequestMapping("/point/list/{emp_serno}")
	public String getDetailList(@RequestParam Map<String, Object> param, Model model) {
		
		List<Map<String, Object>> lst = commonService.getPointDetailList(param);
		
		//System.out.println("detailList:"+lst);
		
		model.addAttribute("detailList", lst);
		model.addAttribute("emp_name", param.get("emp_name"));
		model.addAttribute("strtDate", param.get("strtDate"));
		model.addAttribute("endDate", param.get("endDate"));
		
		return "temp/point-score-details";
    }
	
	@RequestMapping("/incentive/hierarchyList")
	public String sp_selectIncentiveRangkingList(@RequestParam Map<String, Object> param, Model model) {
		
		List<Map<String, Object>> lst = commonService.getIncentiveRangkingList(param);
		model.addAttribute("strtDate", param.get("strtDate"));
		model.addAttribute("endDate",  param.get("endDate"));
		model.addAttribute("hierarchyList", lst);

		return "temp/incentive";
	}
	
	@RequestMapping("/workList")
	public String getWorkList(@RequestParam Map<String, Object> param, Model model) {
		
		LocalDate now = LocalDate.now();
		DayOfWeek dayOfWeek = now.getDayOfWeek();	//요일
		
		while(!now.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
			now = now.minusDays(1);
		}
		
		if (param.get("strtDate")==null) {
			param.put("strtDate", now.toString());
		}
		if (param.get("endDate")==null) {
			param.put("endDate", now.plusDays(6).toString());
		}
		
		List<Map<String,Object>> lst = commonService.getWorkList(param);
		
		//System.out.println(lst);
		model.addAttribute("list", lst);
		model.addAttribute("endDate",  param.get("endDate"));
		model.addAttribute("strtDate", param.get("strtDate"));

		return "temp/work-list";
	}
	
	@ResponseBody	
	@RequestMapping("/incentive/realincentive")
	public Map<String, Object> getRealIncentive(@RequestBody Map<String, Object> param, Model model) throws Exception{
		
		if(param.get("strtDate")==null || param.get("endDate")==null) {
			throw new Exception("incentive date can not be missed");
		}
		
		if(param.get("emp_serno")==null) {
			throw new Exception("employee number can not be missed");
		}
		
		Map<String,Object> map = commonService.getRealIncentive(param);

		//model.addAttribute("list", lst);
		model.addAttribute("endDate",  param.get("endDate"));
		model.addAttribute("strtDate", param.get("strtDate"));

		return map;
	}
	
	@RequestMapping("/mamafee")
	public String getMamaFeeList(@RequestParam Map<String, Object> param, Model model) {
		LocalDate now = LocalDate.now();
		DayOfWeek dayOfWeek = now.getDayOfWeek();	//요일
		
		while(!now.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
			now = now.minusDays(1);
		}
		
		if (param.get("strtDate")==null) {
			param.put("strtDate", now.toString());
		}
		if (param.get("endDate")==null) {
			param.put("endDate", now.plusDays(6).toString());
		}
		
		List<Map<String,Object>> lst = commonService.getMamaFeeList(param);
		
		//System.out.println(lst);
		model.addAttribute("list", lst);
		model.addAttribute("endDate",  param.get("endDate"));
		model.addAttribute("strtDate", param.get("strtDate"));

		return "temp/mama-fee-list";
	}
}
