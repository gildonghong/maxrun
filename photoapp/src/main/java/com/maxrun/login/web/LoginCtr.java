package com.maxrun.login.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maxrun.login.service.LoginService;

@Controller
public class LoginCtr {
	@Autowired
	private LoginService loginService;
	
	@ResponseBody
	@RequestMapping("/login")
	public Map<String, Object> login(@RequestParam Map<String, Object> param){
		Map<String, Object> ret = new HashMap<String, Object>();
		
		return param;
	}
}
