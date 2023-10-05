package com.maxrun.repairshop.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

	@Autowired 
	private EmployeeMapper employeeMapper;
	
	public List<Map<String, Object>> getEmployeeList(Map<String, Object> param) throws Exception{
		return employeeMapper.getEmployeeList(param);
	}
	
	public Map<String, Object> regEmployee(Map<String, Object> param) throws Exception {
		return employeeMapper.regEmployee(param);
	}
	
}
