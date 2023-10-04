package com.maxrun.carcare.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarCareService {
	@Autowired
	private CarCareMapper carCareMapper;
	public List<Map<String, Object>> getCarList(Map<String, Object> params) throws Exception{
		return carCareMapper.getCarList(params);
	}
}
