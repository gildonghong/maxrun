package com.maxrun.application.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxrun.application.exception.BizException;

//import com.ebs.gslp.common.constants.Constants;
//import com.ebs.gslp.common.utils.Autowired;
//import com.ebs.gslp.common.utils.Component;
//import com.ebs.gslp.common.utils.HttpServletRequest;
//import com.ebs.gslp.common.utils.Logger;
//import com.ebs.gslp.common.utils.ObjectMapper;
//import com.ebs.gslp.exception.BizException;
//import com.ebs.gslp.pallycon.util.PallyConDrmTokenUtil;

@Component
public class HttpConnectionUtil {

	Logger logger = LogManager.getLogger(this.getClass());
//	@Autowired private PallyConDrmTokenUtil pallyConDrmTokenUtil;
	private final String charSet = "UTF-8";
	
	/**
	 * httpConnection get 요청
	 * @param targetUrl : api url
	 * @param params : 파리미터
	 * @param type : "GET"
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> getExcute(String targetUrl, String params, String type, HttpServletRequest request) throws BizException{
		Map<String, Object> returnMap = new HashMap<String, Object>(); 
		//http 요청 시 url 주소와 파라미터 데이터를 결합하기 위한 변수 선언
//		String totalUrl = "";
//		if(params != null && params.length() > 0 && !params.equals("") && !params.contains("null")) { //파라미터 값이 널값이 아닌지 확인
//			totalUrl = Constants.CPM_API_URI + targetUrl + "?" + params.trim().toString();
//			
//		}
//		else {
//			totalUrl = Constants.CPM_API_URI + targetUrl;
//		}
//		System.out.println("url ===> " + totalUrl);
	    HttpURLConnection connection = null;
		//http 통신 요청 후 응답 받은 데이터를 담기 위한 변수
		String responseData = "";	
		InputStream is = null;
		BufferedReader br = null;
		StringBuffer sb = null;
	    
		//메소드 호출 결과값을 반환하기 위한 변수
		String returnData = "";
	    try {
	    	URL url = new URL(targetUrl);
	        connection = (HttpURLConnection)url.openConnection();

	        connection.setRequestMethod(type);
	        connection.setRequestProperty("Cache-Control", "max-age=0");
	        connection.setRequestProperty("Content-Type", "application/json");
	        // cpm 필수값 헤더에 삽입
//	        connection.setRequestProperty("Authorization", Constants.CPM_API_KEY);
//	        connection.setRequestProperty("projectId", Constants.CPM_API_PROJECT_ID);
//	        
//	        logger.info("http query string : "+totalUrl);
			//http 요청 실시
	        connection.connect();
//			logger.info("http 요청 주소 : "+targetUrl);
//			logger.info("http 요청 데이터 : "+params);
			
			//http 요청 후 응답 받은 데이터를 버퍼에 쌓는다
			is = connection.getInputStream();
			
			br = new BufferedReader(new InputStreamReader(is, charSet));	
			sb = new StringBuffer();	       
			while ((responseData = br.readLine()) != null) {
				sb.append(responseData); //StringBuffer에 응답받은 데이터 순차적으로 저장 실시
			}
	 
			//메소드 호출 완료 시 반환하는 변수에 버퍼 데이터 삽입 실시
			returnData = sb.toString(); 
			
			//http 요청 응답 코드 확인 실시
			String responseCode = String.valueOf(connection.getResponseCode());
//			logger.info("http 응답 코드 : "+responseCode);
//			logger.info("http 응답 데이터 : "+returnData);
			
			if(!"200".equals(responseCode)) {
				throw new BizException(responseCode);
			}
			
			ObjectMapper mapper = new ObjectMapper();
			// 비디오정보 
			returnMap = mapper.readValue(returnData, Map.class);
			
			if(returnMap != null && !returnMap.isEmpty()) {
				List<Map<String, Object>> sources = (List<Map<String, Object>>)returnMap.get("sources");
				if(sources != null && sources.size() > 0) {
					// 비디정보의 인코딩영상리스트(MP4,DASH,HLS등..)
					for (Map<String, Object> source : sources) {
						List encryptions = (List)source.get("encryptions");
						String videoEncodeType = source.get("type").toString().toUpperCase();
						Map<String, Object> job = (Map<String, Object>)source.get("job");
						String jobId = (String)job.get("id");
					}
				}
			}
	    } catch (MalformedURLException e) {
	    	logger.error("error : {}", e.getMessage());;
	    	throw new BizException(e.getMessage());
	    } catch (IOException e) {
	    	logger.error("error : {}", e.getMessage());;
	    	throw new BizException(e.getMessage());
	    } finally {
	    	if(br != null) { 
	    		try { br.close(); } catch (IOException e) { logger.error("error : {}", e.getMessage());; }
	    	}
	    	if(is != null) {
	    		try { is.close(); } catch (IOException e) { logger.error("error : {}", e.getMessage());; }
	    	}
	    	if(connection != null) { connection.disconnect(); }
	    }
	    return returnMap;
	}
}