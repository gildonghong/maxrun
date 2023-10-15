package com.maxrun.application.common.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

public class MaxrunWebSocket extends AbstractWebSocketHandler {
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
		System.out.println("connection created");
	}
	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		System.out.println("connection created");
	}
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
		System.out.println("message");
	}
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		
	}
}
