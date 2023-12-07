package com.maxrun.repairshop.carcare.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.maxrun.application.common.auth.service.JWTTokenManager;
import com.maxrun.application.common.utils.CommonUtils;
import com.maxrun.application.common.utils.HttpServletUtils;
import com.maxrun.application.common.utils.ImageCompressUtil;
import com.maxrun.application.config.PropertyManager;
import com.maxrun.application.exception.BizExType;
import com.maxrun.application.exception.BizException;
import com.maxrun.repairshop.service.RepairShopService;
import java.io.IOException;

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
	@Autowired
	private ImageCompressUtil imgComUtil;
	
	Logger logger = LogManager.getLogger(CarCareJobService.class);

	public List<Map<String, Object>>getPhotoList(Map<String, Object> param)throws Exception{
		List<Map<String, Object>> photoList = carCareJobMapper.getPhotoList(param);
		String photoRootPath = pmt.get("Globals.photoapp.contetxt.root") + "/" + pmt.get("Globals.photo.root.path");
		photoRootPath = photoRootPath.trim();
		
		for(Map m:photoList) {
			if(String.valueOf(m.get("fileSavedPath")).startsWith("/"))
				m.replace("fileSavedPath", photoRootPath + String.valueOf(m.get("fileSavedPath")).trim());
			else
				m.replace("fileSavedPath", photoRootPath + "/" +  String.valueOf(m.get("fileSavedPath")).trim());
			
			//default iamge 처리
			if(m.get("fileGroupNo")==null)
				m.replace("fileSavedPath", PropertyManager.get("Globals.photoapp.contetxt.root") + PropertyManager.get("Globals.photo.defaultimage.path"));
		}
			
		return photoList;
	}
	
	public int deleteCarEnterIn(int reqNo){
		return carCareJobMapper.deleteCarEnterIn(reqNo);
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
				e.printStackTrace();
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
		
		String osPath= PropertyManager.get("Globals.photo.os.path") + File.separator + this.getRepairReqPhotoPath(reqNo);
		
		//pathString = String.valueOf(repairShopNo) + File.pathSeparator + 

		List<MultipartFile> files =request.getMultiFileMap().get("photo");
		
		if(files == null) throw new BizException(BizExType.PARAMETER_MISSING, "이미지 파일이 누락되었습니다");
		
		for(MultipartFile file: files) {
			//String fileNm = this.createFileName(departmentNo);
			//web상의 이미지 경로 : contextPath + "/" + 이미지폴더root + "/" + 정비소번호 + "/" + 년 + "/" + 월 + "/" + 파일명
			String fileUrl= "/" + this.getRepairReqPhotoPath(reqNo);
			fileUrl = fileUrl.replaceAll("\\\\", "/");
			
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
	
			//exception 발생시 fileDB 삭제
			try {
				//DB 트랜잭션이 성공했으므로 실제 물리 파일을 서비스 경로로 복사
				File targetFile=new File( osPath + File.separator + fileMap.get("fileName")+ "." + fileMap.get("fileExt"));
				file.transferTo(targetFile);
				//file resizing
				compressImage(targetFile, reqNo, String.valueOf(fileMap.get("fileName")), String.valueOf(fileMap.get("fileExt")));
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
	
	//upload된 이미지 파일을 리사이징, 파일용량 최적화 
	private void compressImage(File originalImage, int reqNo, String fileNm, String ext) throws Exception{
		//File targetFile=new File( osPath + File.separator + fileMap.get("fileName")+ "." + fileMap.get("fileExt"));
		try {
			BufferedImage og = ImageIO.read(originalImage);

			int iHeight= og.getHeight();
			int iWidth = og.getWidth();
			//long size = originalImage.length();
			//가로 종횡비로 1920:1080 지정 
			if (iWidth > 1920 ) iWidth=1920;
			if (iHeight > 1080) iHeight=1080;
			
			BufferedImage compressed = ImageCompressUtil.resizeImage(og, iWidth, iHeight);
			
			//백업 폴더 생성 
			String reqRepairFolderPath= PropertyManager.get("Globals.photo.os.path") + File.separator + this.getRepairReqPhotoPath(reqNo);
			
			File backUpFolderPath= new File(reqRepairFolderPath + File.separator + "backup");
			backUpFolderPath.mkdirs();
			//backup File 생성
			File backupFile = new File(backUpFolderPath + File.separator + fileNm + "." + ext);
			//원본파일을 백업파일로 변경
			originalImage.renameTo(backupFile);
			
			//리사이징된 파일을 원본파일 경로로 저장
			File compressedImg = new File(reqRepairFolderPath +  File.separator + fileNm + "." + ext);
			ImageIO.write(compressed, ext, compressedImg);

		}catch(IOException e) {
			e.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void removePhoto(int fileNo) throws Exception{
		carCareJobMapper.removePhoto(fileNo);
	}
 
	public String getRepairReqPhotoPath(int reqNo)throws Exception{
		Map<String, Object> ret= carCareJobMapper.getRepairReqPhotoPath(reqNo);

		String path = 	String.valueOf(ret.get("repairShopNo")) + File.separator+ 
						String.valueOf(ret.get("year")) + File.separator + 
						String.valueOf(ret.get("month")) + File.separator + String.valueOf(reqNo);
		
		return path;
	}
	
	public Map<String, Object> regMemo(Map<String, Object> param) throws Exception{
//		List<Map<String, Object>> memoLst = (List<Map<String, Object>>) param.get("memo");
//		
//		if(memoLst != null && memoLst.size()>0)
//			for(Map<String, Object> m:memoLst) {
//				m.put("reqNo", param.get("reqNo"));
//				m.put("outMemoNo", null);
//				
//				carCareJobMapper.regRepairMemo(m);
//				
//				m.replace("memoNo", m.get("outMemoNo"));
//			}
		
		Map<String, Object> memo = carCareJobMapper.regRepairMemo(param);
		
		param.put("regDate", memo.get("regDate"));
		param.put("memoNo", param.get("outMemoNo"));
		return memo;
	}
	
	public int deleteMemo(int memoNo) {
		int delCnt = carCareJobMapper.deleteMemo(memoNo);
		return delCnt;
	}
	
	//메모목록
	public List<Map<String, Object>> getMemoList(int reqNo)throws Exception{
		return carCareJobMapper.getMemoList(reqNo);
	}
	//입고차량별 사진목록
	public List<Map<String, Object>> getPhotoListByRepairReq(int reqNo) throws Exception {
		try {
			return carCareJobMapper.getPhotoListByRepairReq(reqNo);
		}catch(Exception ex) {
			throw new BizException(BizExType.WRONG_PARAMETER_VALUE, "존재하지 않는 입고차량입니다");
		}	
	}

	private String createDirectory(int reqNo) throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));

		int repairShopNo  = Integer.parseInt(String.valueOf(claims.get("repairShopNo")));

//		String folderNameForCar = getRepairReqPhotoPath(reqNo);
//		
//		if (!StringUtils.hasText(folderNameForCar))
//			throw new BizException(BizExType.WRONG_PARAMETER_VALUE, "입고차량을 확인해주십시오. 존재하지 않는 입고번호입니다");
//		
//		folderNameForCar=folderNameForCar.replaceAll(" ", "");
//		
//		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//		System.out.println("folderNameForCar is =============>" + folderNameForCar);
//		
//		folderNameForCar = CommonUtils.getYearBy2Digit(LocalDate.now()) + CommonUtils.getMonthBy2Digit(LocalDate.now()) + folderNameForCar;
//		
//		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//		System.out.println("folderNameForCar is =============>" + folderNameForCar);

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
