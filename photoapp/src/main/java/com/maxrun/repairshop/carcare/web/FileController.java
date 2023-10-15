package com.maxrun.repairshop.carcare.web;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.Part;

import org.apache.logging.log4j.LogManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
//import com.ebs.gslp.common.MultipartFile;
//import com.ebs.gslp.common.MultipartHttpServletRequest;
//import com.ebs.gslp.common.Part;
//import com.ebs.gslp.common.RequestMapping;
//import com.ebs.gslp.common.ResponseBody;
//import com.ebs.gslp.common.ServletException;

public class FileController {
	org.apache.logging.log4j.Logger logger = LogManager.getLogger(this.getClass());
	
	@PostMapping("/")
	@ResponseBody
	public String upload2(MultipartHttpServletRequest request)throws IOException, ServletException{
		
		Collection<Part> parts = request.getParts();
		
		for(Part part:parts) {			
			logger.info("part name:" + part.getName());
			logger.info("submittedName:" + part.getSubmittedFileName());
			logger.info("content-type:" + part.getContentType());
			logger.info("header:" + Arrays.toString(part.getHeaderNames().toArray()));
		}
		
		MultipartFile file = request.getFile("file");
		logger.info("fileName ==>" + file.getName());
		logger.info("fileSize ==>" + file.getSize());
		logger.info("originFileName ==>" + file.getOriginalFilename());
		
		return "file upload success";
	}

}
