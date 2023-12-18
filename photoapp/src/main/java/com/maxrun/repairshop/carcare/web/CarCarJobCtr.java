package com.maxrun.repairshop.carcare.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.maxrun.application.common.auth.service.JWTTokenManager;
import com.maxrun.application.common.utils.HttpServletUtils;
import com.maxrun.application.exception.BizExType;
import com.maxrun.application.exception.BizException;
import com.maxrun.repairshop.carcare.service.CarCareJobService;
import com.maxrun.repairshop.service.RepairShopService;

@Controller
public class CarCarJobCtr {
	org.apache.logging.log4j.Logger logger = LogManager.getLogger(this.getClass());
	
	@Autowired
	CarCareJobService carCareJobService;
	@Autowired
	RepairShopService repairShopService;
	@Autowired
	JWTTokenManager jwt;

	@ResponseBody
	@PostMapping("/repairshop/carcare/enterin")	/*차량입고등록*/
	public Map<String, Object>regCarRepairReq(@RequestParam Map<String, Object> param)throws Exception{
		
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
		if(claims.get("repairShopNo").equals(-1)) {
			throw new BizException(BizExType.WRONG_PARAMETER_VALUE, "maxrun 담당자로 로그인되어 있는 상태입니다");
		}
		
		if(param.containsKey("delYn") && param.get("delYn").equals("Y")) {
			int reqNo = Integer.parseInt(String.valueOf(param.get("reqNo")));
			int delCnt = carCareJobService.deleteCarEnterIn(reqNo);
			
			Map<String, Object> ret = new HashMap<String, Object>();
			
			ret.put("delCnt", delCnt);
			
			return ret;
		}
		
		if(!param.containsKey("carLicenseNo") || String.valueOf(param.get("carLicenseNo")).equals("")) {
			throw new BizException(BizExType.PARAMETER_MISSING, "차량번호가 누락되었습니다");
		}
		
		param.put("workerNo", claims.get("workerNo"));
		param.put("repairShopNo", claims.get("repairShopNo"));
		
		carCareJobService.regCarEnterIn(param);

		return param;
	}
	
	/*입고등록과 함께 사진 등록을 동시에 */
	@ResponseBody
	@PostMapping("/repairshop/carcare/enterwithphoto")	/*차량입고등록*/
	public Map<String, Object>regCarRepairReqWithPhoto(	MultipartHttpServletRequest request)throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
		if(claims.get("repairShopNo").equals(-1)) {
			throw new BizException(BizExType.WRONG_PARAMETER_VALUE, "maxrun 담당자로 로그인되어 있는 상태입니다");
		}
		
		return carCareJobService.regCarEnterWithPhoto(request);
	}
	
	@ResponseBody
	@GetMapping("/repairshop/carcare/list")
	public List<Map<String, Object>>getCarCareList(@RequestParam Map<String, Object> param)throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
		if(String.valueOf(claims.get("userAgent")).indexOf("MobileApp")>-1) {	//모바일에서 접속한 경우
//			System.out.println("################### connected from mobile App");
//			System.out.println("################### connected from mobile App");
			
			param.put("departmentName", "신규등록");
		}
		
		param.put("repairShopNo", claims.get("repairShopNo"));

//		for(String key:param.keySet()) {
//			System.out.println(key + "=======>" + param.get(key));
//		}
		return carCareJobService.getPhotoList(param);
	}

	@ResponseBody
	@PostMapping("/repairshop/carcare/repair")	/*작업사진등록, /repairshop/carcare/reqair/department 와 동일한 기능*/
	public int regCarCareJob(MultipartHttpServletRequest request)throws Exception{
		if (!StringUtils.hasText(request.getParameter("reqNo")) || !StringUtils.hasText(request.getParameter("departmentNo"))) {
			throw new BizException(BizExType.PARAMETER_MISSING, "입고번호,부서번호는 필수파라미터입니다");
		}else if(request.getMultiFileMap().get("photo")==null) {
			throw new BizException(BizExType.PARAMETER_MISSING, "사진 파일이 누락되었습니다");
		}
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
		if(claims.get("repairShopNo").equals(-1)) {
			throw new BizException(BizExType.WRONG_PARAMETER_VALUE, "maxrun 담당자로 로그인되어 있는 상태입니다");
		}
		
		return carCareJobService.regPhoto(request);	//저장돈 파일의 파일그룹번호를 반환한다
	}
	
	@ResponseBody
	@GetMapping("/repairshop/carcare/photo/list")	/* 입고차량별 전체사진목록 */
	public List<Map<String, Object>> getPhotoListByCar(@RequestParam int reqNo) throws Exception{
		return carCareJobService.getPhotoListByRepairReq(reqNo);
	}
	
	@ResponseBody
	@PostMapping("/repairshop/carcare/memo")
	public Map<String, Object> regMemo(@RequestParam Map<String, Object> param) throws Exception{
		if(!param.containsKey("reqNo")) {
			throw new BizException(BizExType.PARAMETER_MISSING, "차량 입고번호가 누락도었습니다!");
		}
		
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
		if(claims.get("repairShopNo").equals(-1)) {
			throw new BizException(BizExType.WRONG_PARAMETER_VALUE, "maxrun 담당자로 로그인되어 있는 상태입니다");
		}
		
		return carCareJobService.regMemo(param);
	}
	
	@ResponseBody
	@PostMapping("/repairshop/carcare/memo/delete")
	public int delMemo(@RequestParam int memoNo) throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
		if(claims.get("repairShopNo").equals(-1)) {
			throw new BizException(BizExType.WRONG_PARAMETER_VALUE, "maxrun 담당자로 로그인되어 있는 상태입니다");
		}
		return carCareJobService.deleteMemo(memoNo);
	}
	
	@ResponseBody
	@GetMapping("/repairshop/carcare/memo/list")
	public List<Map<String, Object>> getMemoList(@RequestParam int reqNo) throws Exception{
		return carCareJobService.getMemoList(reqNo);
	}

}
