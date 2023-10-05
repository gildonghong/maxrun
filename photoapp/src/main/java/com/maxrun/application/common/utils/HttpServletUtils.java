package com.maxrun.application.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class HttpServletUtils {
	
	public static HttpServletRequest getRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		
		return request;
	}
	
	public static HttpServletResponse getResponse() {
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		
		return response;
	}
	
	//현재 request의 헤더 목록을 반환
	public static List<Map<String, Object>> getRequstHeaders() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		List<Map<String, Object>> headers = new ArrayList<Map<String, Object>>();

		List<String> headerNameList = Collections.list(request.getHeaderNames());
		for(String headerName : headerNameList) {
			Map<String, Object> header = new HashMap<String, Object>();
			
			header.put(headerName, request.getHeader(headerName));
			headers.add(header);
		}
		
		return headers;
	}
	
	//heaerName에 해당하는 header value를 반환
	public static String getRequstHeaderValue(String headerName) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		
		return request.getHeader(headerName);
	}
	
	//heaerName에 해당하는 header value 목록을 Enumeration<String> 형식으로 반환
	public static Enumeration<String> getRequstHeaderValues(String headerName) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		
		return request.getHeaders(headerName);
	}
	//key값으로 저장되어 있는 session Attribute 객체를 반환한다
	public static Map<String, Object> getSessionAttributeValueByMap(String key){
		
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		
		return (Map<String, Object>)request.getSession().getAttribute(key);
	}
	//key값으로 저장되어 있는 session Attribute에서 propertyKey에 해당하는 속성의 값을 반환한다
	public static Object getValueInSessionAttribute(String sessionAttributeName, String propertyKey) {
		Map<String, Object> obj = HttpServletUtils.getSessionAttributeValueByMap(sessionAttributeName);
		
		if(obj != null) {
			return obj.get(propertyKey);
		}else {
			return null;
		}
	}
}
