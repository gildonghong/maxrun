package com.maxrun.repairshop.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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
import com.maxrun.application.config.PropertyManager;
import com.maxrun.application.exception.BizExType;
import com.maxrun.application.exception.BizException;
import com.maxrun.repairshop.carcare.service.CarCareJobService;
import com.maxrun.repairshop.service.RepairShopService;

@Controller
public class RepairShopCtr {
	
	@Autowired
	private RepairShopService repairShopService;
	@Autowired
	private JWTTokenManager jwt;
	@Autowired
	private CarCareJobService carCareJobService;
	
	@ResponseBody
	@PostMapping("/repairshop")
	public Map<String, Object> regRepairShop(@RequestParam Map<String, Object> param) throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
		if(!claims.get("repairShopNo").equals(-1)) {//일반 공업사가 로그인 한 경우
			param.put("repairShopNo", claims.get("repairShopNo"));
		}
		
		if(param.containsKey("delYn") && param.get("delYn").equals("Y")) {
			if (!claims.get("repairShopNo").equals(-1)) {
				throw new BizException(BizExType.NOT_AUTHENTICATED, "맥스런 권한이 아닌 경우는 정비소를 삭제할 수 없습니다");
			}
			int repairShopNo = Integer.parseInt(String.valueOf(param.get("repairShopNo")));
			
			int delCnt = repairShopService.deleteRepairShop(repairShopNo);
			
			Map<String, Object> ret = new HashMap<String, Object>();
			ret.put("delCnt", delCnt);
			
			return ret;
		}else {
			if(!StringUtils.hasText(String.valueOf(param.get("repairShopNo")))) {//신규입력인 경우
				if(!StringUtils.hasText(String.valueOf(param.get("businessNo"))))
					throw new BizException(BizExType.PARAMETER_MISSING, "사업자번호는 필수 입력값입니다");
			}
			param.put("regUserId", claims.get("workerNo"));
			param.put("outRepairShopNo", null);
			return repairShopService.regRepairShop(param);
		}
	}
	
	@ResponseBody
	@GetMapping("/repairshop/{repairShopNo}")
	public Map<String, Object> getRepairShopInfo(@PathVariable int repairShopNo) throws Exception{
		return repairShopService.getRepairShopInfo(repairShopNo);
	}
	
	@ResponseBody
	@GetMapping("/repairshop/list")
	public List<Map<String, Object>> getRepairShopList(@RequestParam Map<String, Object> param) throws Exception{
		return repairShopService.getRepairShopList(param);
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
		
		if(!StringUtils.hasText(String.valueOf(param.get("departmentName"))))
			throw new BizException(BizExType.WRONG_PARAMETER_VALUE, "부서명을 입력하세요!!");
		
		param.put("repairShopNo", claims.get("repairShopNo"));
		param.put("regUserId", claims.get("workerNo"));
		
		if(param.containsKey("delYn") && param.get("delYn").equals("Y")) {
			int departmentNo = Integer.parseInt(String.valueOf(param.get("departmentNo")));
			Map<String, Object> ret = new HashMap<String, Object>();
			
			int delCnt = repairShopService.delDepartment(departmentNo);
			ret.put("delCnt", delCnt);
			return ret;
		}else {
			return repairShopService.regDepartment(param);
		}
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
		
		if(claims.get("repairShopNo").equals(-1)){//maxrun 사용자가 로그인 했을 경우
			if(!param.containsKey("repairShopNo")|| param.get("repairShopNo")==null || !StringUtils.hasText(String.valueOf(param.get("repairShopNo")))) {
				throw new BizException(BizExType.PARAMETER_MISSING, "정비소 번호가 누락되었습니다");
			}
		}else {
			param.put("repairShopNo", claims.get("repairShopNo"));
		}
		
		param.put("regUserId", claims.get("workerNo"));
		
		List<Map<String, Object>> lst = repairShopService.getEnterList(param);
		
		try {
			for(Map<String, Object> m:lst) {
				JSONParser jsonParser = new JSONParser();
				if(m.get("memo") != null) {
			        Object obj = jsonParser.parse(String.valueOf(m.get("memo")));
			        org.json.simple.JSONArray memoLst = (org.json.simple.JSONArray) obj;
			        m.replace("memo", memoLst.toArray());
//			        List<Map<String, Object>> temp = (List<Map<String, Object>>)m.get("memo");
//			        
//			        for(Map<String, Object> mp: temp)
//			        	mp.replace("reqNo", Integer.parseInt(String.valueOf(mp.get("reqNo"))));	//reqNo가 자꾸 문자열로 내려간다고 해서 
				}
		        
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		//JSONObject memoLst = new JSONObject(String.valueOf(lst.get(0).get("memo")));
		//List mLst = (List) lst.get(0).get("memo");
		//List<Map<String, Object>> memoLst = (List<Map<String, Object>>) lst.get(0).get("memo");
		
		System.out.println("입고목록 ===>" + lst);
		
		return lst;
	}
	
	@ResponseBody
	@GetMapping("/repairshop/enter/photo/list")	/*관리자 차량별 사진 전체 목록*/
	public List<Map<String, Object>> getPhotoList(@RequestParam Map<String, Object> param) throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
		/* 정비소 번호가 파라미터로 넘어온 경우는 파라미터를 사용하고 넘어오지 않았을 경우만 토큰 값을 사용한다 */
		if(!StringUtils.hasText(String.valueOf(param.get("repairShopNo")))){
			param.put("repairShopNo", claims.get("repairShopNo"));
		}
		
		param.put("regUserId", claims.get("workerNo"));
		
		List<Map<String, Object>> photoLst = repairShopService.getPhotoList(param);
		
		for(Map<String, Object> photo:photoLst)
			photo.replace("serverFile", 
					PropertyManager.get("Globals.photoapp.contetxt.root") + "/" + 
					PropertyManager.get("Globals.photo.root.path") + 
					photo.get("fileSavedPath") + photo.get("fileName") + "." + photo.get("fileExt"));
		
		return photoLst;
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

		if(param.get("target").equals("customer")) {
			if(param.get("ownerName")==null)
				throw new BizException(BizExType.PARAMETER_MISSING, "고객성명이 누락되었습니다");
			if(param.get("ownerCpNo")==null)
				throw new BizException(BizExType.PARAMETER_MISSING, "고객전화번호가 전화번호가 누락되었습니다");
		}
		
		if(claims.get("repairShopNo").equals(-1)) {
			throw new BizException(BizExType.PARAMETER_MISSING, "maxrun 담당자로 로그인 된 상태에서는 알림톡을 보내실 수 없습니다");
		}
		
		if(!claims.containsKey("repairShopTelNo")) {
			throw new BizException(BizExType.PARAMETER_MISSING, "정비소 전화번호가 없이 알림톡을 보내실수는 없습니다");
		}
		if(!StringUtils.hasText(String.valueOf(claims.get("repairShopTelNo")))) {
			throw new BizException(BizExType.PARAMETER_MISSING, "정비소 전화번호가 없이 알림톡을 보내실수는 없습니다");
		}
		
		if(!param.get("target").equals("customer") && !param.get("target").equals("maxrun")) {
			throw new BizException(BizExType.WRONG_PARAMETER_VALUE, "target parameter 값이 허용되지 않는 값을 가지고 있습니다");
		}
		
		if(param.get("target").equals("maxrun"))
			param.put("maxrunChargerCpNo", claims.get("maxrunChargerCpNo"));

		param.put("repairShopName", claims.get("repairShopName"));
		param.put("repairShopTelNo", claims.get("repairShopTelNo"));

		return repairShopService.regMessageSending(param);
	}
	
	@ResponseBody
	@PostMapping("/repairshop/carentering")	/*관리자 입고차량 정보 수정*/
	public Map<String, Object> regCarEntering(@RequestBody Map<String, Object> param) throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
		if (!param.containsKey("reqNo"))
			throw new BizException(BizExType.PARAMETER_MISSING, "차량 입고번호가 누락되었습니다");

		if(claims.get("repairShopNo").equals(-1)) {
			throw new BizException(BizExType.WRONG_PARAMETER_VALUE, "maxrun 담당자로 로그인되어 있는 상태입니다");
		}
		
		if(param.containsKey("delYn") && param.get("delYn").equals("Y")) {
			int reqNo = Integer.parseInt(String.valueOf(param.get("reqNo")));
			int delCnt = carCareJobService.deleteCarEnterIn(reqNo);
			
			Map<String, Object> ret = new HashMap<String, Object>();
			
			ret.put("delCnt", delCnt);
			
			return ret;
		}else {
			param.put("repairshopNo", claims.get("repairshopNo"));
			param.put("regUserId", claims.get("workerNo"));

			carCareJobService.regCarEnterIn(param);
		}

		return param;
	}
	
}
