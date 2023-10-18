package com.maxrun.repairshop.carcare.service;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarCareJobService {
	@Autowired
	private CarCareJobMapper carCareJobMapper;
	
	public List<Map<String, Object>>getPhotoList(Map<String, Object> param)throws Exception{
		return carCareJobMapper.getPhotoList(param);
	}

	public void regCarEnterIn(Map<String, Object> param)throws Exception{
		carCareJobMapper.regCarEnterIn(param);
	}
	
	public void regPhoto(Map<String, Object>param)throws Exception{
		carCareJobMapper.regPhoto(param);
	}
 
	public String getRepairReqPhotoPath(int reqNo)throws Exception{
		return carCareJobMapper.getRepairReqPhotoPath(reqNo);
	}
	
	public List<Map<String, Object>> getFileListForTransffering()throws Exception{
		return carCareJobMapper.getFileListForTransffering();
	}
}
