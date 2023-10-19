package com.maxrun.application.common.websocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import com.maxrun.repairshop.carcare.service.CarCareJobService;

public class MaxrunWebSocketSvr extends AbstractWebSocketHandler {
	@Autowired
	CarCareJobService carCareService;
	
	//static Set<Session> sessionUsers = Collections.synchronizedSet(new HashSet<Session>());
	static List<Map<String, Object>> lst = null;
	static Set<WebSocketSession> repairShopList = Collections.synchronizedSet(new HashSet<WebSocketSession>());
	//static List<WebSocketSession> repairShopList = Collections.synchronizedList(new ArrayList<WebSocketSession>());
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
		System.out.println("connection created");
	}
	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		System.out.println("connection created");
		Map<String, Object> userInfo = session.getAttributes();
		System.out.println("userInfo ==========>"+ userInfo);
		System.out.println("uri ==========>"+ session.getUri());
		
		repairShopList.add(session);
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
	
	private WebSocketSession findRepairShopSession(int repairShopNo) {
		Map<String,Object> userInfo = null;
		for(WebSocketSession session:repairShopList) {
			userInfo = session.getAttributes();
			
			if(userInfo.get("repairShopNo").equals(repairShopNo)) {
				return session;
			}
		}
		return null;
	}
	
	@Scheduled(fixedDelay=2000)
	public void doSomething() throws Exception {
		//System.out.println("1111111111111111111");
		if(lst!=null) {
			//System.out.println(lst.size());
			
			if (lst.size()<10)
			lst.addAll(carCareService.getFileListForTransffering());
		}else {
			lst = carCareService.getFileListForTransffering();
		}
		
		//loop돌면서 sending
		for(Map<String, Object> m:lst) {
			int repairShopNo = Integer.parseInt(String.valueOf(m.get("repairShopNo")));
			
			try {
				WebSocketSession repairShop = findRepairShopSession(repairShopNo);
				if(repairShop!=null) {
					TextMessage message = new TextMessage("ddddddddddd");
					System.out.println(repairShopNo + " 정비소에 메시지 전송");
					repairShop.sendMessage(message);
				}
			}catch(IndexOutOfBoundsException e) {
				//System.out.println("ok");
			}catch(Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
	}
}
