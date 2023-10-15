package com.maxrun.application.common.websocket;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.maxrun.repairshop.carcare.service.CarCareJobService;

public class MaxrunWebSocketSvr extends AbstractWebSocketHandler {
	@Autowired
	CarCareJobService carCareService;
	
	static List<Map<String, Object>> lst = null;
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
		System.out.println("connection created");
	}
	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		System.out.println("connection created");
		if(lst!=null) {
			System.out.println(lst.size());
		}
	}
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
		System.out.println("message");
		if(lst!=null) {
			System.out.println(lst.size());
		}
	}
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		
	}
	
	@Scheduled(fixedDelay=5000)
	public void doSomething() throws Exception {
		System.out.println("1111111111111111111");
		if(lst!=null) {
			System.out.println(lst.size());
			lst.addAll(carCareService.getFileListForTransffering());
		}else {
			lst = carCareService.getFileListForTransffering();
		}
	}
}
