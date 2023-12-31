package com.maxrun.application.common.interceptor;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import com.maxrun.application.exception.BizExType;
import com.maxrun.application.exception.BizException;
import com.maxrun.application.login.web.LoginCtr;

public class WebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor implements ApplicationContextAware {
	Logger logger = LogManager.getLogger(getClass());
	private static ApplicationContext springContext;
	
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

		String queryString=request.getURI().getQuery();
		
		String loginId=queryString.substring(queryString.indexOf("=")+1, queryString.indexOf("&"));
		String passwd=queryString.substring(queryString.indexOf("=", queryString.indexOf("&")+1)+1);

		LoginCtr lc = springContext.getBean(LoginCtr.class);
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("loginId", loginId);
		param.put("passwd", passwd);
		
		try {
			Map<String, Object> userInfo = lc.login(param);
			attributes.putAll(userInfo);
		}catch(Exception ex) {
			throw new BizException(BizExType.NOT_AUTHORIZED, "illegal login trying!!!!!");	
		}
		
		return super.beforeHandshake(request, response, wsHandler, attributes);
	}
	
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
			WebSocketHandler wsHandler, @Nullable Exception ex) {
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		springContext = applicationContext;
	}
	
}
