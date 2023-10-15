package com.maxrun.application.common.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

public class MaxrunWebSocketSvr extends AbstractWebSocketHandler {
//	@Autowired 
//	MaxrunWebSocketClient client;

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
		System.out.println("connection created");
	}
	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		System.out.println("connection created");
		//client.connect();
	}
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
		System.out.println("message");
	}
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		
	}
}
