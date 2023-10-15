package com.maxrun.repairshop.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maxrun.application.common.auth.service.JWTTokenManager;
import com.maxrun.application.common.utils.CookieUtils;
import com.maxrun.application.common.utils.HttpServletUtils;
import com.maxrun.repairshop.service.EmployeeService;

@Controller
public class EmployeeCtr {
	
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private JWTTokenManager jwt;
	
	@ResponseBody
	@GetMapping("/repairshop/employee/list")
	public List<Map<String, Object>> getWorkerList(@RequestParam Map<String, Object> param) throws Exception{
		return employeeService.getWorkerList(param);
	}
	
	@ResponseBody
	@PostMapping("/repairshop/employee")
	public Map<String, Object> regWorker(@RequestParam Map<String, Object> param)throws Exception{ 
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		param.put("regUserId", claims.get("workerNo"));
		
		return employeeService.regWorker(param);
	}
}
