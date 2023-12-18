package com.maxrun.application.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxrun.application.exception.BizException;

public class HttpClientUtil {
//	[{"message_type":"AT",
//		"phn":"821045673546",
//		"profile":"ddd220da6741a415878d216a1b93c0b93702d7b8",
//		"msg":"홍일사 의 55가5555 차량에 대한 케미컬 청구 신청 합니다.",
//		"tmplId":"photoapp01"
//		}]
	public static void main(String args[]) throws IOException {
		
//		ObjectMapper objectMapper=new ObjectMapper();
//		objectMapper.readValue
//		
//		List<Map<String, Object>> myObjects = objectMapper.readValue(test , new TypeReference<List<Map<String, Object>>>(){});
//		
		// 리스트 파라미터 예시
		JSONArray list = new JSONArray();
		JSONObject data = new JSONObject();

		HttpHeaders headers = new HttpHeaders();
		
		headers.add("userid", "maxrun");
		
		data.put("message_type", "AT");
		data.put("phn", "821045673546");
		data.put("profile", "ddd220da6741a415878d216a1b93c0b93702d7b8");
		data.put("msg", "홍일사 의 55가5555 차량에 대한 케미컬 청구 신청 합니다.");
		data.put("tmplId", "photoapp01");

		list.add(data);
		
		List<Map<String, Object>> ret = excuteByJsonObject("POST", "https://alimtalk-api.bizmsg.kr/v2/sender/send", list);
//		
//		//ResponseEntity<Map<String, Object>> res = execute(HttpMethod.POST, MediaType.APPLICATION_JSON_UTF8, "https://alimtalk-api.bizmsg.kr/v2/sender/send", test, headers);
//		ResponseEntity<?> res =  ajax2("https://alimtalk-api.bizmsg.kr/v2/sender/send", HttpMethod.POST, msg , headers);
	}

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
	
	/* JSON API   */
	public static List<Map<String, Object>> excuteByJsonObject(String method, String stringURL, JSONArray param) throws IOException {
	    URL obj = null;
	    obj = new URL(stringURL); // API URL

	    HttpURLConnection con = (HttpURLConnection)obj.openConnection();
	    con.setRequestMethod(method); // GET, POST
	    con.setRequestProperty("Content-type", "application/json; charset=UTF-8");
	    //con.setRequestProperty("Content-type", "application/json");
	    con.setRequestProperty("userid", "maxrun");
	    
	    con.setDoOutput(true);
	    // DATA
	    OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());

	    wr.write(param.toString());
	    wr.flush();
	    // API 호출
	    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
	    String line;
	    StringBuffer sb = new StringBuffer();
	    while((line = in.readLine()) != null){
	        sb.append(line);
	    }
	    in.close();
	    con.disconnect();
	    String text = sb.toString();
	    ObjectMapper mapper = new ObjectMapper();
	    List<Map<String, Object>> ret = mapper.readValue(text, List.class);
	    return ret; 
	}
}
