package com.maxrun.repairshop.carcare.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maxrun.application.exception.BizExType;
import com.maxrun.application.exception.BizException;

@Service
public class CarCareJobService {
	@Autowired
	private CarCareJobMapper carCareJobMapper;
	
	public List<Map<String, Object>>getPhotoList(Map<String, Object> param)throws Exception{
		return carCareJobMapper.getPhotoList(param);
	}

	public void regCarEnterIn(Map<String, Object> param)throws Exception{
		try {
			carCareJobMapper.regCarEnterIn(param);
		}catch(Exception e) {
			System.out.println(e.getMessage());
			if(e.getMessage().contains("이미 입고내역이 있는 차량번호입니다")) {
				throw new BizException(BizExType.WRONG_PARAMETER_VALUE, "동일 년월에 등록된 차량번호입니다. 차량번호 다음에 _숫자등을 부여해서 구분해주세요!!");
			}else {
				throw e;
			}
		}
		
	}
	
	public void regPhoto(Map<String, Object>param)throws Exception{
		carCareJobMapper.regPhoto(param);
	}
	
	public void removePhoto(int fileNo) throws Exception{
		carCareJobMapper.removePhoto(fileNo);
	}
 
	public String getRepairReqPhotoPath(int reqNo)throws Exception{
		return carCareJobMapper.getRepairReqPhotoPath(reqNo);
	}
	
	public List<Map<String, Object>> getFileListForTransffering()throws Exception{
		return carCareJobMapper.getFileListForTransffering();
	}
}
