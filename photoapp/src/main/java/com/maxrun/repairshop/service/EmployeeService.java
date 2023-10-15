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
	
	public List<Map<String, Object>> getWorkerList(Map<String, Object> param) throws Exception{
		return employeeMapper.getWorkerList(param);
	}
	
	public Map<String, Object> regWorker(Map<String, Object> param) throws Exception {
		return employeeMapper.regWorker(param);
	}
	
}
