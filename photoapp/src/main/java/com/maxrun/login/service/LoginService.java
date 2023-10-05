package com.maxrun.login.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
	@Autowired
	LoginMapper loginMapper;
	public Map<String, Object> login(Map<String, Object> param) throws Exception{
		return loginMapper.login(param);
	}
}
