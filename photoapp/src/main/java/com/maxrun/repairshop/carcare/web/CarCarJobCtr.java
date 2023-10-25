package com.maxrun.repairshop.carcare.web;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.maxrun.application.common.auth.service.JWTTokenManager;
import com.maxrun.application.common.utils.CommonUtils;
import com.maxrun.application.common.utils.HttpServletUtils;
import com.maxrun.application.config.PropertyManager;
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
		
		param.put("workerNo", claims.get("workerNo"));
		param.put("repairShopNo", claims.get("repairShopNo"));
		
		carCareJobService.regCarEnterIn(param);

		return param;
	}
	
	/*입고등록과 함께 사진 등록을 동시에 */
	@ResponseBody
	@PostMapping("/repairshop/carcare/enterwithphoto")	/*차량입고등록*/
	public Map<String, Object>regCarRepairReqWithPhoto(	MultipartHttpServletRequest request)throws Exception{
		return carCareJobService.regCarEnterWithPhoto(request);
	}
	
	@ResponseBody
	@GetMapping("/repairshop/carcare/list")
	public List<Map<String, Object>>getCarCareList(@RequestParam Map<String, Object> param)throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
		param.put("repairShopNo", claims.get("repairShopNo"));
		
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
		
		return carCareJobService.regPhoto(request);	//저장돈 파일의 파일그룹번호를 반환한다
	}

}
