package com.maxrun.application.common.utils;

import java.io.IOException;
import java.util.Map;

import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import com.maxrun.application.exception.BizException;

public class HttpClientUtil {

	public static HttpHeaders createHttpHeaders(Map<String, Object>heareders) throws BizException{
		return null;
	}
	
	public static HttpEntity<Map<String, Object>> createRequest() throws BizException{
		return null;
		
	}
	
	public static ResponseEntity<Map<String, Object>> execute(	HttpMethod httpMehtod,
																MediaType	contentType,
																String	url,
																Map<String, Object> param,
																HttpHeaders	headers) throws BizException{
		ResponseEntity<Map<String, Object>> res;
		
		/*403 forbidding 오류 방지위해 useragent 헤더 설정*/
		headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)" + 
				" AppleWebKit/537.36 (KHTML, like Gecko) chrome/54.0.2840.99 Safari/537.36");
		
		if(contentType.equals(MediaType.APPLICATION_FORM_URLENCODED)) {
			res = post(url, param, headers);
		}else if(contentType.equals(MediaType.APPLICATION_JSON_UTF8) || contentType.equals(MediaType.APPLICATION_JSON)) {
			res = ajax(url, httpMehtod, param, headers);
		}else {
			throw new BizException("지정하신 콘텐츠 타입은 지원하지 않습니다!");
		}
			
		return res; 
	}
	
	/*application/json;characterset=UTF-8방식 지원*/
	private static ResponseEntity<Map<String, Object>> ajax(String url, HttpMethod httpMethod, Map<String, Object> param, HttpHeaders headers) throws BizException{
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		HttpEntity<Map<String, Object>> req = new HttpEntity<Map<String, Object>>(param, headers); /*body: param, headers: headers */
		ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault());
		ResponseEntity<Map<String, Object>> res = null;
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(requestFactory);
		
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			public boolean hasError(ClientHttpResponse response)throws IOException{
				HttpStatus statusCode = response.getStatusCode();
				
				return statusCode.series() == HttpStatus.Series.SERVER_ERROR || statusCode.series() == HttpStatus.Series.CLIENT_ERROR;
			}
		});

		res = restTemplate.exchange(url,  httpMethod, req, new ParameterizedTypeReference<Map<String, Object>>(){});
		return res;
	}
	
	/*application/x-wwww-form-urlencoded 방식 지원*/
	private static ResponseEntity<Map<String, Object>> post( String url, Map<String, Object> param, HttpHeaders headers) throws BizException{
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		
		for(String key: param.keySet()){
			map.add(key,  String.valueOf(param.get(key)));
		}
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			public boolean hasError(ClientHttpResponse response)throws IOException{
				HttpStatus statusCode = response.getStatusCode();
				
				return statusCode.series() == HttpStatus.Series.SERVER_ERROR || statusCode.series() == HttpStatus.Series.CLIENT_ERROR;
			}
		});
		
		ResponseEntity<Map<String, Object>> res = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<Map<String, Object>>(){});
		
		return res;
	}
}
