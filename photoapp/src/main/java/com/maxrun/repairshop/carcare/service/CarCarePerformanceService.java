package com.maxrun.repairshop.carcare.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Transactional
@Service
public class CarCarePerformanceService {

	@Autowired
	private CarCarePerformanceMapper carCarePerformanceMapper;
	
	public List<Map<String, Object>>getPerformanceList(Map<String, Object> param)throws Exception{
		return carCarePerformanceMapper.getPerformanceList(param);
	}
	
}
