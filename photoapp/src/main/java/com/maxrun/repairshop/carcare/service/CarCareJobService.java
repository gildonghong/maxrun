package com.maxrun.repairshop.carcare.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarCareJobService {
	@Autowired
	private CarCareJobMapper carCareJobMapper;
	
	public List<Map<String, Object>>getCarCareList(Map<String, Object> param)throws Exception{
		return carCareJobMapper.getCarCareList(param);
	}

	public List<Map<String, Object>>getJobList(Map<String, Object> param)throws Exception{
		return carCareJobMapper.getJobList(param);
	}
	
	public Map<String, Object> regCarCareJob(Map<String, Object> param)throws Exception{
		return carCareJobMapper.regCarCareJob(param);
	}
	public Map<String, Object> getJobDetails(Map<String, Object> param)throws Exception{
		return carCareJobMapper.getJobDetails(param);
	}
}
