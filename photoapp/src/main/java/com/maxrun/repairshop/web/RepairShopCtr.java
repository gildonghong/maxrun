package com.maxrun.repairshop.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maxrun.application.common.auth.service.JWTTokenManager;
import com.maxrun.application.common.utils.CookieUtils;
import com.maxrun.application.common.utils.HttpClientUtil;
import com.maxrun.application.common.utils.HttpServletUtils;
import com.maxrun.application.exception.BizExType;
import com.maxrun.application.exception.BizException;
import com.maxrun.repairshop.service.RepairShopService;

@Controller
public class RepairShopCtr {
	
	@Autowired
	private RepairShopService repairShopService;
	@Autowired
	private JWTTokenManager jwt;
	
	@ResponseBody
	@PostMapping("/repairshop")
	public Map<String, Object> regRepairShop(@RequestParam Map<String, Object> param) throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
		param.put("regUserId", claims.get("workerNo"));
		param.put("outRepairShopNo", null);
		return repairShopService.regRepairShop(param);
	}
	@ResponseBody
	@GetMapping("/repairshop/{repairShopNo}")
	public Map<String, Object> getRepairShopInfo(@PathVariable int repairShopNo) throws Exception{
		return repairShopService.getRepairShopInfo(repairShopNo);
	}
	
	@ResponseBody
	@GetMapping("/repairshop/department/list")
	public List<Map<String, Object>> getDepartmentList() throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		int repairShopNo = Integer.parseInt(String.valueOf(claims.get("repairShopNo")));
		
		return repairShopService.getDepartmentList(repairShopNo);
	}
	
	@ResponseBody
	@PostMapping("/repairshop/department")
	public Map<String, Object> regDepartment(@RequestParam Map<String, Object> param) throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
		param.put("repairShopNo", claims.get("repairShopNo"));
		param.put("regUserId", claims.get("workerNo"));
		return repairShopService.regDepartment(param);
	}
	
	@ResponseBody
	@PostMapping("/maxrun")
	public int regMaxRun(@RequestParam Map<String, Object> param) throws Exception{
		param.put("repairShopNo", -1);
		
		repairShopService.regMaxRun(param);
		
		return -1;
	}
	
	@ResponseBody
	@GetMapping("/repairshop/enter/list")
	public List<Map<String, Object>> getEnterList(@RequestParam Map<String, Object> param) throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		param.put("repairShopNo", claims.get("repairShopNo"));
		param.put("regUserId", claims.get("workerNo"));
		
		List<Map<String, Object>> lst = repairShopService.getEnterList(param);
		System.out.println("입고목록 ===>" + lst);
		
		return lst;
	}
	
	@ResponseBody
	@GetMapping("/repairshop/enter/photo/list")
	public List<Map<String, Object>> getPhotoList(@RequestParam Map<String, Object> param) throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		param.put("repairShopNo", claims.get("repairShopNo"));
		param.put("regUserId", claims.get("workerNo"));
		
		return repairShopService.getPhotoList(param);
	}
	
	@ResponseBody
	@GetMapping("/repairshop/performance/list")
	public List<Map<String, Object>>getPerformanceList(@RequestParam Map<String, Object> param)throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		param.put("repairShopNo", claims.get("repairShopNo"));
		
		return repairShopService.getPerformanceList(param);
	}
	
	@ResponseBody
	@PostMapping("/repairshop/message")
	public Map<String, Object> sendMessage(@RequestParam Map<String, Object> param) throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
		if(!param.containsKey("reqNo"))
			throw new BizException(BizExType.PARAMETER_MISSING, "입고번호 파라미터가 누락되었습니다");
		
		if(!param.containsKey("target"))
			throw new BizException(BizExType.PARAMETER_MISSING, "메시지 전송대상값을 target파라미터로 전달해주십시오!");
		
		if(!param.containsKey("carLicenseNo"))
			throw new BizException(BizExType.PARAMETER_MISSING, "차량번호가 누락되었습니다");
		
		if(param.get("target").equals("maxrun") && param.get("maxrunChargerCpNo")==null)
			throw new BizException(BizExType.PARAMETER_MISSING, "maxrun 담당자 전화번호가 누락되었습니다");
		
		if(param.get("target").equals("customer")) {
			if(param.get("ownerName")==null)
				throw new BizException(BizExType.PARAMETER_MISSING, "고객성명이 누락되었습니다");
			if(param.get("ownerCpNo")==null)
				throw new BizException(BizExType.PARAMETER_MISSING, "고객전화번호가 전화번호가 누락되었습니다");
		}
		
		if(!param.get("target").equals("customer") && !param.get("target").equals("maxrun")) {
			throw new BizException(BizExType.WRONG_PARAMETER_VALUE, "target parameter 값이 허용되지 않는 값을 가지고 있습니다");
		}
		param.put("repairShopName", claims.get("repairShopName"));
		param.put("repairShopTelNo", claims.get("repairShopTelNo"));
		
//		String talkMsg;
//		String carLicenseNo = String.valueOf(param.get("carLicenseNo"));
////		[{
////		    "message_type":"AT",
////		    "phn":"821045673546",
////		    "profile":"ddd220da6741a415878d216a1b93c0b93702d7b8",
////		    "tmplId":"photoapp01",
////		    "msg":"홍길동님의 33소3333 차량에 대한 케미컬 청구 신청 합니다."
////		}]	
//		if(param.get("target").equals("customer")) {
////			talkMsg = "홍길동님의 33소3333 차량에 대한 케미컬 청구 신청 합니다.";
//			talkMsg = param.get("ownerName") + "님의 " + carLicenseNo + " 차량에 대한 케미컬 청구 신청합니다";
//		}else {
//			//talkMsg = "홍길동 고객님\\n요청하신 33소3333 에 대한 수리가 완료되었습니다.\\n 맥스런을 믿고 차량을 맡겨주셔서 감사합니다.언제나최선을 다하겠습니다.\\n- 공공공공업사\\n- 연락처 : 02388838383";
//			talkMsg = param.get("ownerName") + " 고객님\\n" + "요청하신 " + param.get("carLicenseNo") + "에 대한 수리가 완료되었습니다 \\n" + param.get("repairShopName") + 
//					  "을(를) 믿고 차량을 맡겨주셔서 감사합니다. 언제나 최선을 다하겠습니다.\\n" + param.get("repairShopName") + " :" + param.get("repairShopTelNo");
//		}
//		Map<String, Object> msg = null;
//		
//		HttpHeaders headers = new HttpHeaders();
//		
//		headers.add("userid", "maxrun");
//		
//		HttpClientUtil.execute(	HttpMethod.POST, 
//								MediaType.APPLICATION_JSON_UTF8, 
//								"https://alimtalk-api.bizmsg.kr/v2/sender/send", 
//								msg, 
//								headers);
		
		repairShopService.regMessageSending(param);
		return claims;
	}
}
