package com.maxrun.repairshop.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import com.maxrun.application.common.auth.service.JWTTokenManager;
import com.maxrun.application.common.utils.CookieUtils;
import com.maxrun.application.exception.BizExType;
import com.maxrun.application.exception.BizException;

@Transactional
@Service
public class RepairShopService {
//	public List<Map<String, Object>> getRepairShopList() throws SQLException;
//	public List<Map<String, Object>> getDepartmentList(int repairShopNo) throws SQLException;
//	public Map<String, Object> regRepairShop(Map<String, Object> param) throws SQLException;
//	public Map<String, Object> getRepairShopInfo(int repairShopNo) throws SQLException;
//	public Map<String, Object> regDepartment(int repairShopNo) throws SQLException;	
	@Autowired
	private RepairShopMapper repairShopMapper;

	public List<Map<String, Object>> getRepairShopList() throws Exception{
		return repairShopMapper.getRepairShopList();
	}
	
	public Map<String, Object> getRepairShopInfo(int repairShopNo) throws Exception{
		return repairShopMapper.getRepairShop(repairShopNo);
	}
	
	public Map<String, Object> regRepairShop(Map<String, Object> param) throws Exception{
		return repairShopMapper.regRepairShop(param);
	}

	public List<Map<String, Object>> getDepartmentList(int repairShopNo) throws Exception{
		return repairShopMapper.getDepartmentList(repairShopNo);
	}
	
	public String	getDepartmentName(int departmentNo) throws Exception{
		return repairShopMapper.getDepartmentName(departmentNo);
	}
	
	public int	getDepartmentNo(Map<String, Object> param) throws Exception{
		return repairShopMapper.getDepartmentNo(param);
	}
	
	public Map<String, Object> regDepartment(Map<String, Object> param) throws Exception{
		
		try {
			return repairShopMapper.regDepartment(param);
		}catch(Exception e) {
			if (e.getMessage().contains("UK_TB_DEPARTMENT")) {
				throw new BizException(BizExType.DUPLICATED_PARAMETER, "이미 존재하는 부서명으로 수정하실 수 없습니다");
			}
			throw e;
		}
	}

	public void regMaxRun(Map<String, Object> param) throws Exception{
		repairShopMapper.regMaxRun(param);
	}
	
	public List<Map<String, Object>> getNeedToSenderListForTransffering() throws Exception{
		return repairShopMapper.getFileListForTransffering();
	}
	
	public void completeCopyToRepairShop(Map<String, Object> param) throws Exception{
		
		System.out.println("param===>" + param);
//		CREATE PROCEDURE sp_completeCopyToRepairShop	@repairShopNo	int,
//														@division		nvarchar(30)='FILE',
//														@reqNo			bigint=null,
//														@fileNo			bigint=null,
//														@result			nvarchar(30)='SUCCESS'
		List<Map<String, Object>> ret = repairShopMapper.completeCopyToRepairShop(param);
		
		System.out.println("ret===>" + ret);
	}
	
	public List<Map<String, Object>> getEnterList(@RequestParam Map<String, Object> param) throws Exception{
		return repairShopMapper.getEnterList(param);
	}
	
	public List<Map<String, Object>> getPhotoList(Map<String, Object> param) throws Exception{
		return repairShopMapper.getPhotoList(param);
	}
	
	
}
