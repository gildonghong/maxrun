package com.maxrun.repairshop.carcare.service;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
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
import com.maxrun.repairshop.service.RepairShopService;

@Transactional
@Service
public class CarCareJobService {
	@Autowired
	RepairShopService repairShopService;
	@Autowired
	private CarCareJobMapper carCareJobMapper;
	@Autowired
	private JWTTokenManager jwt;
	@Autowired
	private PropertyManager	pmt;
	
	public List<Map<String, Object>>getPhotoList(Map<String, Object> param)throws Exception{
		List<Map<String, Object>> photoList = carCareJobMapper.getPhotoList(param);
		String photoRootPath = pmt.get("Globals.photoapp.contetxt.root") + "/" + pmt.get("Globals.photo.root.path");
		
		for(Map m:photoList) {
			if(String.valueOf(m.get("fileSavedPath")).startsWith("/"))
				m.replace("fileSavedPath", photoRootPath + m.get("fileSavedPath"));
			else
				m.replace("fileSavedPath", photoRootPath + "/" +  m.get("fileSavedPath"));
		}
			
		return photoList;
	}

	public void regCarEnterIn(Map<String, Object> param)throws Exception{
		try {
			carCareJobMapper.regCarEnterIn(param);
			
			if (StringUtils.isEmpty(createDirectory(Integer.parseInt(String.valueOf(param.get("outReqNo")))))) {
				throw new BizException(BizExType.UNKNOWN, "차량별 디렉토리 생성에 실패했습니다");
			}

			param.put("reqNo", param.get("outReqNo"));
			
		}catch(Exception e) {
			System.out.println(e.getMessage());
			if(e.getMessage().contains("이미 입고내역이 있는 차량번호입니다")) {
				throw new BizException(BizExType.WRONG_PARAMETER_VALUE, "동일 년월에 등록된 차량번호입니다. 차량번호 다음에 _숫자등을 부여해서 구분해주세요!!");
			}else {
				throw e;
			}
		}
		
	}
	
	public Map<String, Object> regCarEnterWithPhoto(MultipartHttpServletRequest request)throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
//		call sp_regCarEnterIn(	#{reqNo},
//				#{repairShopNo},
//				#{carLicenseNo},
//				#{venderNo},
//				#{modelNo},
//				#{etcInfo},
//				#{memo},
//				#{ownerName},
//				#{ownerCpNo},
//				#{ownerEmail},
//				#{paymentType},
//				#{workerNo},
//				#{outReqNo, jdbcType=BIGINT, mode=OUT})
//		param.put("workerNo", claims.get("workerNo"));
//		param.put("repairShopNo", claims.get("repairShopNo"));
		Map<String, Object> param = new HashMap<String, Object>();
		
		param.put("workerNo", claims.get("workerNo"));
		param.put("repairShopNo", claims.get("repairShopNo"));
		param.put("carLicenseNo", request.getParameter("carLicenseNo"));
		param.put("ownerCpNo", request.getParameter("ownerCpNo"));
		param.put("paymentType", request.getParameter("paymentType"));
		param.put("departmentName", "신규등록");
		
		regCarEnterIn(param);
		
		request.setAttribute("reqNo", param.get("reqNo"));
		request.setAttribute("departmentNo", repairShopService.getDepartmentNo(param));
		
		int fileGroupNo = regPhoto(request);
		
		param.put("fileGroupNo", fileGroupNo);
		
		return param;
	}
	
	public int regPhoto(MultipartHttpServletRequest request)throws Exception{
		System.out.println("------------Globals.photo.os.path===============>" + pmt.get("Globals.photo.os.path"));
		System.out.println("------------Globals.photoapp.contetxt.root===============>" + PropertyManager.get("Globals.photoapp.contetxt.root"));
		
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		int reqNo = Integer.parseInt(request.getParameter("reqNo")==null?String.valueOf(request.getAttribute("reqNo")):request.getParameter("reqNo"));
		int	fileGroupNo  = Integer.parseInt(request.getParameter("fileGroupNo")==null?"0":String.valueOf(request.getParameter("fileGroupNo")).trim());
		int repairShopNo  = Integer.parseInt(String.valueOf(claims.get("repairShopNo")));
		int departmentNo  = Integer.parseInt(request.getParameter("departmentNo")==null?String.valueOf(request.getAttribute("departmentNo")):request.getParameter("departmentNo"));
		/*사진 등록시 부서번호 또는 부서명 파라미터는 필수값*/
//		if (request.getParameter("departmentNo")==null || "".equals(String.valueOf(request.getParameter("departmentNo")))) {
//			if (request.getParameter("departmentName")==null || "".equals(String.valueOf(request.getParameter("departmentName")))) {
//				throw new BizException(BizExType.PARAMETER_MISSING, "부서지정없이 파일 사진을 등록할수 없습니다");
//			}
//			Map<String, Object> param = new HashMap<String, Object>();
//			param.put("repairShopNo", repairShopNo);
//			param.put("departmentName", request.getParameter("departmentName"));
//			departmentNo  = repairShopService.getDepartmentNo(param);
//		}else {
//			departmentNo  = Integer.parseInt(request.getParameter("departmentNo"));
//		}
		String pathString= this.createDirectory(reqNo);

		List<MultipartFile> files =request.getMultiFileMap().get("photo");
		
		if(files == null) throw new BizException(BizExType.PARAMETER_MISSING, "이미지 파일이 누락되었습니다");
		
		for(MultipartFile file: files) {
			//String fileNm = this.createFileName(departmentNo);
			//web상의 이미지 경로 : contextPath + "/" + 이미지폴더root + "/" + 정비소번호 + "/" + 년 + "/" + 월 + "/" + 파일명
			String fileUrl= "/" + String.valueOf(repairShopNo) + "/" + 
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
			//file 매터정보 db저장 
			carCareJobMapper.regPhoto(fileMap);	
			//out 파라미터 처리 
			fileMap.put("fileNo", fileMap.get("outFileNo"));
			fileMap.put("fileGroupNo", fileMap.get("outFileGroupNo"));
			fileMap.put("fileName", fileMap.get("outFileNo"));	//파일명은 인코딩 문제때문에 파일번호로 저장하기로 한다
			fileGroupNo = Integer.parseInt(String.valueOf(fileMap.get("outFileGroupNo")));
			
//			URL resource = this.getClass().getClassLoader().getResource(PropertyManager.get("Globals.photo.root.path"));
//			String photoRootPath = resource.getPath().toString();
			
			//exception 발생시 fileDB 삭제
			try {
				if(! System.getProperty("os.name").toLowerCase().contains("window")) {	/*로컬 환경이 아니면*/
					//스토리징 장비 달기전까지 런타임시에 파일을 웹문맥에도 중복으로 저장
//					String tempPath="/maxrunphoto/tomcat/webapps/ROOT/photo/" + String.valueOf(repairShopNo) + File.separator + 
//									CommonUtils.getYearBy4Digit(LocalDate.now()) + File.separator + 
//									CommonUtils.getMonthBy2Digit(LocalDate.now())+ File.separator + 
//									String.valueOf(reqNo);
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
				//removePhoto(Integer.parseInt(String.valueOf(fileMap.get("outFileNo"))));
				throw new BizException(BizExType.SERVER_ERROR, "file saving error!!!");
			}
		}
		//파일이 저장되었으면 파일 그룹번호를 반환한다. 여러개의 파일을 등록하더라도 한 세션에선 저장된 파일은 동일한 파일 그룹번호를 공유한다
		return fileGroupNo;

	}
	
	public void removePhoto(int fileNo) throws Exception{
		carCareJobMapper.removePhoto(fileNo);
	}
 
	public String getRepairReqPhotoPath(int reqNo)throws Exception{
		return carCareJobMapper.getRepairReqPhotoPath(reqNo);
	}

	private String createDirectory(int reqNo) throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));

		int repairShopNo  = Integer.parseInt(String.valueOf(claims.get("repairShopNo")));

		String folderNameForCar = getRepairReqPhotoPath(reqNo);
		
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
}
