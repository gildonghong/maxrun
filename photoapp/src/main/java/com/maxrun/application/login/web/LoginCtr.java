package com.maxrun.application.login.web;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maxrun.application.common.crytography.DefaultCryptographyHelper;
import com.maxrun.application.login.service.LoginService;

@Controller
public class LoginCtr {
	private static final Logger log = LogManager.getLogger(LoginCtr.class);
	@Autowired
	private LoginService loginService;
	@Autowired
	private DefaultCryptographyHelper cryptographyHelper;
	
	@ResponseBody
	@RequestMapping("/login")
	public Map<String, Object> login(@RequestParam Map<String, Object> param)throws Exception{
		Map<String, Object> map=null;
		param.put("passwd", cryptographyHelper.sha256(String.valueOf(param.get("passwd"))));
		map = loginService.login(param);

		return map;
	}
}
