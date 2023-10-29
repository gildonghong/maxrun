package com.maxrun.application.login.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maxrun.application.common.auth.service.JWTTokenManager;
import com.maxrun.application.exception.BizExType;
import com.maxrun.application.exception.BizException;

@Service
public class LoginService {
	@Autowired
	LoginMapper loginMapper;
	@Autowired
	JWTTokenManager tokenManager;
	
	public Map<String, Object> login(Map<String, Object> param) throws Exception{
		Map<String, Object> user = loginMapper.login(param);
		
		if(user==null) {
			throw new BizException(BizExType.NO_MATCHING_USER_FOUND, "ID 또는 PASSWORD를 확인하십시오!");
		}
		
		return tokenManager.generateToken(user);
		
	}
	
	public Map<String, Object> logout(Map<String, Object> param) throws Exception{
		return loginMapper.logout(param);
	}
}
