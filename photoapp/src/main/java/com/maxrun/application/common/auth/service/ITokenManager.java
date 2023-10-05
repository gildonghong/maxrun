package com.maxrun.application.common.auth.service;

import java.util.Map;

import com.maxrun.application.exception.BizException;


public interface ITokenManager {

	//인증용 토큰을 response쿠키에 담아서 사용할때
	public Map<String, Object> generateToken(Map<String, Object> param) throws BizException;	
	
	public Map<String, Object> validateToken(String accessToken, String refreshToken)  throws BizException;	

	//guest 용 토큰 발행
	public Map<String, Object> generateGuestToken(Map<String, Object> param) throws BizException;
	//현재 쿠키에 있는 토큰 정보를 대상으로 인증사용자의 정보를 반환
	public Map<String, Object> getAuthenticatedUserInformation() throws BizException;	
	//인증용 토큰 검증
	public Map<String, Object> evaluateToken() throws IllegalArgumentException, BizException;
	//원타임토큰 인증
	public Map<String, Object> evaluateToken(String token) throws BizException;
	//token을 파라미터로 사용하고자 할때
	public String createToken(Map<String, Object> param) throws Exception;	
	//토큰 반환
	public String getToken() throws Exception;	
	// 원타임 URL 생성 등에 사용되는 토큰 생성 메소드. 로그인 인증에서 사용하면 안됨
	//public String generateOneTimeToken(Map<String, Object> param, long expireMilli) throws JwtException;

}
