package com.maxrun.repairshop.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.RequestParam;

@Mapper
public interface RepairShopMapper {
	public List<Map<String, Object>> getRepairShopList(Map<String, Object> param) throws SQLException;
	public Map<String, Object> getRepairShop(int repairShopNo) throws SQLException;
	public Map<String, Object> regRepairShop(Map<String, Object> param) throws SQLException;
	public List<Map<String, Object>> getDepartmentList(int repairShopNo) throws SQLException;
	public String getDepartmentName(int departmentNo) throws SQLException;
	public int	getDepartmentNo(Map<String, Object> param) throws SQLException;
	public Map<String, Object> regDepartment(Map<String, Object> param) throws SQLException;
	public void regMaxRun(Map<String, Object> param) throws SQLException;
	public List<Map<String, Object>> getFileListForTransffering() throws SQLException;
	public List<Map<String, Object>> completeCopyToRepairShop(Map<String, Object> param) throws SQLException;
	public List<Map<String, Object>> getEnterList(Map<String, Object> param) throws SQLException;
	public List<Map<String, Object>> getPhotoList(Map<String, Object> param) throws SQLException;
	public List<Map<String, Object>>getPerformanceList(Map<String, Object> param)throws SQLException;
	public Map<String, Object> regMessageSending(Map<String, Object> param) throws SQLException;
	public void regWSException(Map<String, Object> param)throws SQLException;
	public int deleteRepairShop(int repairShopNo)throws SQLException;
	public int delDepartment(int departmentNo)throws SQLException;
	public void updatePendingStatus();
}
