package com.maxrun.application.login.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maxrun.application.common.crytography.DefaultCryptographyHelper;
import com.maxrun.application.common.utils.HttpServletUtils;
import com.maxrun.application.exception.BizExType;
import com.maxrun.application.exception.BizException;
import com.maxrun.application.login.service.LoginService;
import eu.bitwalker.useragentutils.UserAgent;

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
		HttpServletRequest req = HttpServletUtils.getRequest();
		
		//UserAgent ua= UserAgent.parseUserAgentString(req.getHeader("userAgent"));

		param.put("passwd", cryptographyHelper.sha256(String.valueOf(param.get("passwd"))));

		Map<String, Object> map = loginService.login(param);
		
//		if(!StringUtils.hasText(req.getHeader("userAgent"))) {	//Windows에서 접속한 경우
//			if (!map.get("managerYn").equals("Y"))
//				throw new BizException(BizExType.NOT_AUTHENTICATED, "관리자권한이 없습니다");
//		}

		return map;
	}
}
