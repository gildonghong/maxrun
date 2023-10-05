package com.maxrun.repairshop.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maxrun.repairshop.service.EmployeeService;

@Controller
public class EmployeeCtr {
	
	@Autowired
	private EmployeeService employeeService;
	
	@ResponseBody
	@GetMapping("/repairshop/employee/list")
	public List<Map<String, Object>> getEmployeeList(Map<String, Object> param) throws Exception{
		return employeeService.getEmployeeList(param);
	}
	
	@ResponseBody
	@PostMapping("/repairshop/employee")
	public Map<String, Object> regEmployee(Map<String, Object> param)throws Exception{
		return employeeService.regEmployee(param);
	}
}
