package com.maxrun.repairshop.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maxrun.application.common.auth.service.JWTTokenManager;
import com.maxrun.application.common.utils.CookieUtils;
import com.maxrun.application.common.utils.HttpServletUtils;
import com.maxrun.repairshop.service.RepairShopService;

@Controller
public class RepairShopCtr {
	
	@Autowired
	private RepairShopService repairShopService;
	@Autowired
	private JWTTokenManager jwt;
	
	@ResponseBody
	@PostMapping("/repairshop")
	public Map<String, Object> regRepairShop(@RequestParam Map<String, Object> param) throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
		if(!param.containsKey("repairShopNo"))	/*parameter로 명시적으로 전달하는 경우는 파라미터 값을 사용할 것*/
			param.put("repairShopNo", claims.get("repairShopNo"));	//파라밑로 전달되지 않은 경우에만 토큰에서 껴낼것
		param.put("regUserId", claims.get("workerNo"));
		param.put("outRepairShopNo", null);
		return repairShopService.regRepairShop(param);
	}
	@ResponseBody
	@GetMapping("/repairshop/{repairShopNo}")
	public Map<String, Object> getRepairShopInfo(@PathVariable int repairShopNo) throws Exception{
		return repairShopService.getRepairShopInfo(repairShopNo);
	}
	
	@ResponseBody
	@GetMapping("/repairshop/department/list")
	public List<Map<String, Object>> getDepartmentList() throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		int repairShopNo = Integer.parseInt(String.valueOf(claims.get("repairShopNo")));
		
		return repairShopService.getDepartmentList(repairShopNo);
	}
	
	@ResponseBody
	@PostMapping("/repairshop/department")
	public Map<String, Object> regDepartment(@RequestParam Map<String, Object> param) throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
		param.put("repairShopNo", claims.get("repairShopNo"));
		param.put("regUserId", claims.get("workerNo"));
		return repairShopService.regDepartment(param);
	}
	
	@ResponseBody
	@PostMapping("/maxrun")
	public int regMaxRun(@RequestParam Map<String, Object> param) throws Exception{
		param.put("repairShopNo", -1);
		
		repairShopService.regMaxRun(param);
		
		return -1;
	}
	
	@ResponseBody
	@GetMapping("/repairshop/enter/list")
	public List<Map<String, Object>> getEnterList(@RequestParam Map<String, Object> param) throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		param.put("repairShopNo", claims.get("repairShopNo"));
		param.put("regUserId", claims.get("workerNo"));
		
		List<Map<String, Object>> lst = repairShopService.getEnterList(param);
		System.out.println("입고목록 ===>" + lst);
		
		return lst;
	}
	
	@ResponseBody
	@GetMapping("/repairshop/enter/photo/list")
	public List<Map<String, Object>> getPhotoList(@RequestParam Map<String, Object> param) throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		param.put("repairShopNo", claims.get("repairShopNo"));
		param.put("regUserId", claims.get("workerNo"));
		
		return repairShopService.getPhotoList(param);
	}
	
	@ResponseBody
	@GetMapping("/repairshop/performance/list")
	public List<Map<String, Object>>getPerformanceList(@RequestParam Map<String, Object> param)throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		param.put("repairShopNo", claims.get("repairShopNo"));
		
		return repairShopService.getPerformanceList(param);
	}
}
