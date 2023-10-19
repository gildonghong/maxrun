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
	@Autowired
	PropertyManager	pmt;
	
	@ResponseBody
	@PostMapping("/repairshop/carcare/enterin")	/*차량입고등록*/
	public Map<String, Object>regCarRepairReq(@RequestParam Map<String, Object> param)throws Exception{
		Map<String, Object> ret = new HashMap<String, Object>();
		
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
		param.put("workerNo", claims.get("workerNo"));
		param.put("repairShopNo", claims.get("repairShopNo"));

		if(param.get("memo")!=null) {
			Map<String, Object> memoList=(Map<String, Object>)param.get("memo");

		}
		
		carCareJobService.regCarEnterIn(param);

		if (StringUtils.isEmpty(createDirectory(Integer.parseInt(String.valueOf(param.get("outReqNo")))))) {
			throw new BizException(BizExType.UNKNOWN, "차량별 디렉토리 생성에 실패했습니다");
		}
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
	@PostMapping("/repairshop/carcare/repair")	/*작업사진등록, /repairshop/carcare/reqair/department 와 동일한 기능*/
	public Map<String, Object> regCarCareJob(MultipartHttpServletRequest request)throws Exception{
		if (!StringUtils.hasText(request.getParameter("reqNo")) || !StringUtils.hasText(request.getParameter("departmentNo"))) {
			throw new BizException(BizExType.PARAMETER_MISSING, "입고번호,부서번호는 필수파라미터입니다");
		}else if(request.getMultiFileMap().get("photo")==null) {
			throw new BizException(BizExType.PARAMETER_MISSING, "사진 파일이 누락되었습니다");
		}

		System.out.println("------------Globals.photo.os.path===============>" + pmt.get("Globals.photo.os.path"));
		System.out.println("------------Globals.photoapp.contetxt.root===============>" + PropertyManager.get("Globals.photoapp.contetxt.root"));
		
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		int reqNo = Integer.parseInt(request.getParameter("reqNo"));
		int departmentNo  = Integer.parseInt(request.getParameter("departmentNo"));
		int	fileGroupNo  = Integer.parseInt(request.getParameter("fileGroupNo")==null?"0":String.valueOf(request.getParameter("fileGroupNo")).trim());
		int repairShopNo  = Integer.parseInt(String.valueOf(claims.get("repairShopNo")));
		String pathString= this.createDirectory(reqNo);

		List<MultipartFile> files =request.getMultiFileMap().get("photo");
		
		for(MultipartFile file: files) {
			//String fileNm = this.createFileName(departmentNo);
			//web상의 이미지 경로 : contextPath + "/" + 이미지폴더root + "/" + 정비소번호 + "/" + 년 + "/" + 월 + "/" + 파일명
			String fileUrl=pmt.get("Globals.photoapp.contetxt.root") + "/" + 
							pmt.get("Globals.photo.root.path") + "/" + 
							String.valueOf(repairShopNo) + "/" + 
							CommonUtils.getYearBy4Digit(LocalDate.now()) + "/" +  
							CommonUtils.getMonthBy2Digit(LocalDate.now()) + "/" + 
							String.valueOf(reqNo);
			
			Map<String, Object> fileMap = new HashMap<String, Object>();
			
			fileMap.put("reqNo", reqNo);
			fileMap.put("fileGroupNo", fileGroupNo);
			fileMap.put("fileSavedPath", fileUrl + "/");
			fileMap.put("originalFileName", file.getOriginalFilename());
			fileMap.put("fileExt", file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.')).replace(".", ""));
			fileMap.put("fileSize", file.getSize());
			//fileMap.put("fileName", fileNm);
			fileMap.put("departmentNo", departmentNo);
			fileMap.put("regUserId", claims.get("workerNo"));
			fileMap.put("outFileNo", null);
			fileMap.put("outFileGroupNo", null);
			
			//서버단에서 FILE MIME 타입 체크할 것
			ContentInfoUtil util = new ContentInfoUtil();
			ContentInfo info = util.findMatch(file.getBytes());
			if (info == null) {
				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!111111 Unknown content-type");
				throw new BizException(BizExType.PARAMETER_NOT_ALLOWDED, "Unknown content-type");
			} else if(!info.getMimeType().startsWith("image/")) {
			   // other information in ContentInfo type
			   System.out.println("##########################  Content-type is: " + info.getMimeType());
			   throw new BizException(BizExType.PARAMETER_NOT_ALLOWDED, "only image file can be uploaded");
			}
			
			carCareJobService.regPhoto(fileMap);
			//out 파라미터 처리 
			fileMap.put("fileNo", fileMap.get("outFileNo"));
			fileMap.put("fileGroupNo", fileMap.get("outFileGroupNo"));
			fileMap.put("fileName", fileMap.get("outFileNo"));	//파일명은 인코딩 문제때문에 파일번호로 저장하기로 한다
			fileGroupNo = Integer.parseInt(String.valueOf(fileMap.get("outFileGroupNo")));
			
			//exception 발생시 fileDB 삭제
			try {
				if(! System.getProperty("os.name").toLowerCase().contains("window")) {	/*로컬 환경이 아니면*/
					//스토리징 장비 달기전까지 런타임시에 파일을 웹문맥에도 중복으로 저장
					String tempPath="/sabangdisco/tomcat/webapps/ROOT/photo/" + String.valueOf(repairShopNo) + File.separator + 
									CommonUtils.getYearBy4Digit(LocalDate.now()) + File.separator + 
									CommonUtils.getMonthBy2Digit(LocalDate.now())+ File.separator + 
									String.valueOf(reqNo);
					
					File folder= new File(pathString);
					folder.mkdirs();
					
					if(!folder.exists()) {
						throw new BizException(BizExType.SERVER_ERROR, "file saving error!!!");
					}else {
						System.out.println(tempPath + " directory 가 잘 생성되었습니다");
					}
					
					File tempFile=new File( tempPath + "/" + fileMap.get("fileName")+ "." + fileMap.get("fileExt"));
					System.out.println("################ tempFile 생성완료");
					FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(tempFile));
					System.out.println("################ tempFile 생성완료");
					FileCopyUtils.copy(tempFile, new File(pathString + File.separator + fileMap.get("fileName")+ "." + fileMap.get("fileExt")));
					System.out.println("################ os 경로로 tempFile 복사 !!!!!");
				}else {
					//DB 트랜잭션이 성공했으므로 실제 물리 파일을 서비스 경로로 복사
					file.transferTo(new File(pathString + File.separator + fileMap.get("fileName")+ "." + fileMap.get("fileExt")));
				}

			}catch(Exception ex) {
				ex.printStackTrace();
				//OS 상에서 파일 저장과정에서 에러가 발생했으므로 DB에 기저장된 메터 정보도 같이 삭제한다
				carCareJobService.removePhoto(Integer.parseInt(String.valueOf(fileMap.get("outFileNo"))));
				throw new BizException(BizExType.SERVER_ERROR, "file saving error!!!");
			}
		}
		//파일이 저장되었으면 파일 그룹번호를 반환한다. 여러개의 파일을 등록하더라도 한 세션에선 저장된 파일은 동일한 파일 그룹번호를 공유한다
		Map<String, Object> ret = new HashMap();
		ret.put("fileGroupNo", fileGroupNo);
		return ret;
	}

	//차량별 디렉토리 생성
	private String createDirectory(int reqNo) throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));

		int repairShopNo  = Integer.parseInt(String.valueOf(claims.get("repairShopNo")));

		String folderNameForCar = carCareJobService.getRepairReqPhotoPath(reqNo);
		
		if (!StringUtils.hasText(folderNameForCar))
			throw new BizException(BizExType.WRONG_PARAMETER_VALUE, "입고차량을 확인해주십시오. 존재하지 않는 입고번호입니다");
		
		folderNameForCar=folderNameForCar.replaceAll(" ", "");
		
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		System.out.println("folderNameForCar is =============>" + folderNameForCar);
		
		folderNameForCar = CommonUtils.getYearBy2Digit(LocalDate.now()) + CommonUtils.getMonthBy2Digit(LocalDate.now()) + folderNameForCar;
		
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		System.out.println("folderNameForCar is =============>" + folderNameForCar);

		//OS상 파일 경로
		String pathString=pmt.get("Globals.photo.os.path") + File.separator + 
							String.valueOf(repairShopNo) + File.separator + 
							CommonUtils.getYearBy4Digit(LocalDate.now()) + File.separator + 
							CommonUtils.getMonthBy2Digit(LocalDate.now())+ File.separator + 
							String.valueOf(reqNo);	//차량별 디렉토리
		
		System.out.println("******************************************");
		System.out.println("pathString is =============>" + pathString);
		
		File folder= new File(pathString);
		
		folder.mkdirs();
		if(folder.exists()) {
			System.out.println("######################################################################");
			System.out.println(folder.getAbsolutePath() + " directory is created!!!!!!!!!!!!!!!!!");
			System.out.println("######################################################################");

		}else {
			System.out.println("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
			System.out.println(folder.getAbsolutePath() + " directory is creatiom fail!!!!!!!!!!!!!!!!!");
			System.out.println("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
		}
		return folder.getAbsolutePath();
	}
	
//	private String createFileName(int departmentNo) throws Exception{
//
//		LocalDate now = LocalDate.now();
//		String mm = String.valueOf(now.getMonthValue());
//		String year=String.valueOf(now.getYear());
//		String day = now.getDayOfMonth()>9?String.valueOf(now.getDayOfMonth()):"0" + now.getDayOfMonth();
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");
//		String formatedNow = LocalTime.now().format(formatter);
//		
//		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
//		
//		String fileNm = String.valueOf(departmentNo) + "_" +  year+mm+day+formatedNow+ "_" + String.valueOf(claims.get("workerNo"));
//		return fileNm;
//	}
	
}
