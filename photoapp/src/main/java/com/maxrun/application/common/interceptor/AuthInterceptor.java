package com.maxrun.application.common.interceptor;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.maxrun.application.common.auth.service.JWTTokenManager;
import com.maxrun.application.common.utils.CookieUtils;
import com.maxrun.application.exception.BizExType;
import com.maxrun.application.exception.BizException;
import com.maxrun.application.exception.ErrorCode;

import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;

public class AuthInterceptor implements HandlerInterceptor {
	private static final Logger log = LogManager.getLogger(AuthInterceptor.class);
	
	@Autowired
	JWTTokenManager jwtTokenManager;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		log.info(request.getRequestURI() + "에 대해서 preHandle 수행");

		String uAtoken = CookieUtils.getCookieValue("uAtoken");

		if (uAtoken==null) {
			uAtoken = request.getHeader("Authorization");
			
	        if (!StringUtils.hasText(uAtoken) || !uAtoken.startsWith("Bearer")) {
	        	throw new BizException(BizExType.ACCESS_TOKEN_MISSING, ErrorCode.UNAUTHORIZED.getMessage());
	        }
	        uAtoken = uAtoken.replace("Bearer ", "");
		}/*else if(!StringUtils.hasText(request.getHeader("userAgent"))) {	//Windwos App에서 
			if(!StringUtils.hasText(uAtoken))								//
				throw new BizException(BizExType.NOT_AUTHENTICATED, "불법적인 접근입니다");
		}*/

		try {
			Map<String, Object> user=jwtTokenManager.evaluateToken(uAtoken);
			request.getSession().setAttribute("uAtoken", uAtoken);
			//CookieUtils.addCookie("uAtoken", uAtoken, 0);
			return true;
		}catch(Exception e) {
			throw e;
		}
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
		log.info(request.getRequestURI() + "에 대해서 postHandle 수행");
	}
	
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
		log.info(request.getRequestURI() + "에 대해서 afterCompletion 수행");
	}
	

}
