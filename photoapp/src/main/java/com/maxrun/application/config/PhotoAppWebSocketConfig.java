package com.maxrun.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import com.maxrun.application.common.interceptor.WebSocketHandshakeInterceptor;
import com.maxrun.application.common.websocket.MaxrunWebSocketSvr;

@Configuration
@EnableWebSocket
public class PhotoAppWebSocketConfig implements WebSocketConfigurer{

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(getSocketHandler(), "/socket")
				.addInterceptors(getInterceptor());
	}
	
	//A handler for WebSocket messages and lifecycle events.
	@Bean
	public WebSocketHandler getSocketHandler() {
		return new MaxrunWebSocketSvr();
	}
	
	@Bean
	public HttpSessionHandshakeInterceptor getInterceptor() {
		return new WebSocketHandshakeInterceptor();
	}

}
