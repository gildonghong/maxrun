package com.maxrun.application.login.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maxrun.application.common.auth.service.JWTTokenManager;

@Service
public class LoginService {
	@Autowired
	LoginMapper loginMapper;
	@Autowired
	JWTTokenManager tokenManager;
	
	public Map<String, Object> login(Map<String, Object> param) throws Exception{

		Map<String, Object> user=tokenManager.generateToken(loginMapper.login(param));
		
		return user;
	}
	
	public Map<String, Object> logout(Map<String, Object> param) throws Exception{
		return loginMapper.logout(param);
	}
}
