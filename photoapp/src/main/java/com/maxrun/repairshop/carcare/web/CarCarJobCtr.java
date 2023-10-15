package com.maxrun.repairshop.carcare.web;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.maxrun.application.common.auth.service.JWTTokenManager;
import com.maxrun.application.common.utils.CookieUtils;
import com.maxrun.application.common.utils.HttpServletUtils;
import com.maxrun.application.config.PropertyManager;
import com.maxrun.application.exception.BizExType;
import com.maxrun.application.exception.BizException;
import com.maxrun.repairshop.carcare.service.CarCareJobService;

@Controller
public class CarCarJobCtr {
	org.apache.logging.log4j.Logger logger = LogManager.getLogger(this.getClass());
	
	@Autowired
	CarCareJobService carCareJobService;
	@Autowired
	JWTTokenManager jwt;
	
	@ResponseBody
	@PostMapping("/repairshop/carcare/enterin")	/*차량입고등록*/
	public Map<String, Object>regCarRepairReq(@RequestParam Map<String, Object> param)throws Exception{
		Map<String, Object> ret = new HashMap<String, Object>();
		
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
		param.put("workerNo", claims.get("workerNo"));
		param.put("repairShopNo", claims.get("repairShopNo"));
		
		carCareJobService.regCarEnterIn(param);
		ret.put("reqNo", param.get("outReqNo"));
		
		return ret;
	}
	
	@ResponseBody
	@GetMapping("/repairshop/carcare/list")
	public List<Map<String, Object>>getCarCareList(@RequestParam Map<String, Object> param)throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
		param.put("repairShopNo", claims.get("repairShopNo"));
		
		return carCareJobService.getPhotoList(param);
	}

	@ResponseBody
	@PostMapping("/repairshop/carcare/reqair")	/*작업사진등록, /repairshop/carcare/reqair/department 와 동일한 기능*/
	public Map<String, Object> regCarCareJob(MultipartHttpServletRequest request)throws Exception{
		if (!StringUtils.hasText(request.getParameter("reqNo")) || !StringUtils.hasText(request.getParameter("departmentNo"))) {
			throw new BizException(BizExType.PARAMETER_MISSING, "입고번호,부서번호는 필수파라미터입니다");
		}else if(request.getMultiFileMap().get("photo")==null) {
			throw new BizException(BizExType.PARAMETER_MISSING, "사진 파일이 누락되었습니다");
		}
		
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		int reqNo = Integer.parseInt(request.getParameter("reqNo"));
		int departmentNo  = Integer.parseInt(request.getParameter("departmentNo"));
		int	fileGroupNo  = Integer.parseInt(request.getParameter("fileGroupNo")==null?"0":request.getParameter("fileGroupNo"));
		int repairShopNo  = Integer.parseInt(String.valueOf(claims.get("repairShopNo")));
		
		LocalDate now = LocalDate.now();
		String mm = String.valueOf(now.getMonthValue());
		String year=String.valueOf(now.getYear());
		
		String folderNameForCar = carCareJobService.getRepairReqPhotoPath(reqNo);

		String pathString=PropertyManager.get("Globals.photo.root.path") + String.valueOf(repairShopNo) + File.separator + year + File.separator + mm+ File.separator + folderNameForCar;	//차량별 디렉토리 
		
		File folder= new File(pathString);
		
		folder.mkdirs();

		List<MultipartFile> files =request.getMultiFileMap().get("photo");
		
		for(MultipartFile file: files) {
			Map<String, Object> fileMap = new HashMap<String, Object>();
			
			fileMap.put("reqNo", reqNo);
			fileMap.put("fileGroupNo", fileGroupNo);
			fileMap.put("fileSavedPath", PropertyManager.get("Globals.photoapp.contetxt.root")+pathString + File.separator + file.getName());
			fileMap.put("originalFileName", file.getOriginalFilename());
			fileMap.put("fileExt", file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.')).replace(".", ""));
			fileMap.put("fileSize", file.getSize());
			fileMap.put("fileName", UUID.randomUUID());
			fileMap.put("departmentNo", departmentNo);
			fileMap.put("regUserId", claims.get("workerNo"));
			fileMap.put("outFileNo", null);
			fileMap.put("outFileGroupNo", null);
			
			carCareJobService.regPhoto(fileMap);
			fileGroupNo = Integer.parseInt(String.valueOf(fileMap.get("outFileGroupNo")));

			file.transferTo(new File(pathString + File.separator + fileMap.get("fileName")+file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'))));
		}
		//파일이 저장되었으면 파일 그룹번호를 반환한다. 여러개의 파일을 등록하더라도 한 세션에선 저장된 파일은 동일한 파일 그룹번호를 공유한다
		Map<String, Object> ret = new HashMap();
		ret.put("fileGroupNo", fileGroupNo);
		return ret;
	}
	
}
