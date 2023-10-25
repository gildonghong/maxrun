package com.maxrun.repairshop.carcare.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CarCareJobMapper {

	public List<Map<String, Object>>getPhotoList(Map<String, Object> param)throws SQLException;
	public List<Map<String, Object>>getJobList(Map<String, Object> param)throws SQLException;
	
	public void regCarEnterIn(Map<String, Object> param) throws SQLException;
	public void removePhoto(int fileNo) throws SQLException;
	public void regPhoto(Map<String, Object> param)throws SQLException;
	public Map<String, Object> getJobDetails(Map<String, Object> param)throws SQLException;
	public String getRepairReqPhotoPath(int reqNo)throws SQLException;
	public List<Map<String, Object>> getFileListForTransffering()throws SQLException;
	public void regRepairMemo(Map<String, Object> param) throws SQLException;
	
}
