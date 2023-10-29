package com.maxrun.repairshop.service;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.maxrun.application.common.utils.HttpClientUtil;
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
	
	public List<Map<String, Object>>getPerformanceList(Map<String, Object> param)throws Exception{
		return repairShopMapper.getPerformanceList(param);
	}

	public Map<String, Object> regMessageSending(Map<String, Object> param)throws Exception{
		//target	: maxrun, customer
		//ownerName
		//ownerCpNo
		JSONArray list = new JSONArray();
		JSONObject msg = new JSONObject();
		
		String carLicenseNo = String.valueOf(param.get("carLicenseNo"));
		String message=null;
		
		if(param.get("target").equals("maxrun")) {
//			[{"message_type":"AT",
//			"phn":"821045673546",
//			"profile":"ddd220da6741a415878d216a1b93c0b93702d7b8",
//			"msg":"홍일사 의 55가5555 차량에 대한 케미컬 청구 신청 합니다.",
//			"tmplId":"photoapp01"
//			}]	
			message= String.valueOf(param.get("repairShopName")) + " 의 " + carLicenseNo + " 차량에 대한 케미컬 청구 신청 합니다.";
			msg.put("phn", param.get("maxrunChargerCpNo"));
			//data.put("msg", "홍일사 의 55가5555 차량에 대한 케미컬 청구 신청 합니다.");
			msg.put("msg", String.valueOf(param.get("repairShopName")) + " 의 " + carLicenseNo + " 차량에 대한 케미컬 청구 신청 합니다.");
			msg.put("tmplId", "photoapp01");
		}else {
//			[
//			    {
//			        "message_type": "AT",
//			        "phn": "01045673546",
//			        "profile": "ddd220da6741a415878d216a1b93c0b93702d7b8",
//			        "tmplId": "photoapp002",
//			        "msg":"홍길동 고객님\n 요청하신 33소3333 에 대한 수리가 완료되었습니다.\n#aaa 을 믿고 차량을 맡겨주셔서 감사합니다.언제나최선을 다하겠습니다.\n- 공공공공업사\n- 연락처 : 02388838383"
//			    }
//			]
			
			if(!(param.containsKey("ownerName") && param.containsKey("ownerCpNo") && param.containsKey("carLicenseNo"))){
				throw new BizException("고객성명, 고객전화번호, 차량번호는 필수 입력값입니다!!");
			}
			message = String.valueOf(param.get("ownerName")) + " 고객님\n 요청하신 " +  carLicenseNo + "에 대한 수리가 완료되었습니다.\n" + 
					String.valueOf(param.get("repairShopName")) + "을 믿고 차량을 맡겨주셔서 감사합니다. 언제나최선을 다하겠습니다.\n-" + 
					String.valueOf(param.get("repairShopName")) + "\n-연락처 : " + String.valueOf(param.get("repairShopTelNo"));
			msg.put("phn", param.get("ownerCpNo"));
			msg.put("tmplId", "photoapp002");
			msg.put("msg", message);
		}
		
		msg.put("message_type", "AT");
		msg.put("profile", "ddd220da6741a415878d216a1b93c0b93702d7b8");
		
		list.add(msg);
		
		List<Map<String, Object>> resResults = HttpClientUtil.excuteByJsonObject("POST", "https://alimtalk-api.bizmsg.kr/v2/sender/send", list);
		
//		[
//		    {
//		        "code": "success",
//		        "data": {
//		            "phn": "01045673546",
//		            "msgid": "WEB20231026221115388076",
//		            "type": "AT"
//		        },
//		        "message": "K000",
//		        "originMessage": null
//		    }
//		]
				
		Map<String, Object> ret = resResults.get(0);
		Map<String, Object> returnMsg = (Map<String, Object>)ret.get("data");
		String msgId = String.valueOf(((Map<String, Object>)ret.get("data")).get("msgid"));
		param.put("messageId", msgId);
		param.put("sendingResult", ret.get("code"));
		param.put("templateId", msg.get("tmplId"));
		
		//알림톡 송신 결과를 DB에 저장한다
		repairShopMapper.regMessageSending(param);
		
		returnMsg.put("result", ret.get("code"));
		returnMsg.put("returnMsg", ret.get("message"));
		return returnMsg;
	}
	
}
