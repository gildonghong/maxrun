package com.maxrun.application.common.websocket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.maxrun.application.config.PropertyManager;
import com.maxrun.repairshop.carcare.service.CarCareJobService;
import com.maxrun.repairshop.service.RepairShopService;

public class MaxrunWebSocketSvr extends AbstractWebSocketHandler {
	@Autowired
	RepairShopService repairShopService;

	static List<Map<String, Object>> needToSendLIst = null;
	static Set<WebSocketSession> repairShopList = Collections.synchronizedSet(new HashSet<WebSocketSession>());

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
		System.out.println("connection closed");
		repairShopList.remove(session);
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
		
		String paylod = (String) message.getPayload();

		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> msg = mapper.readValue(paylod, Map.class);

			System.out.println("paylod--->" + msg);
			repairShopService.completeCopyToRepairShop(msg);
			
			if(msg.get("result").equals("SUCCESS"))
				removeWorkListAlreadyCopyDone(msg);	//복사작업 완료된 건은 리스트에서 제거
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		System.out.println("handleTransportError===>" + exception.getMessage());
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
	
	private synchronized void removeWorkListAlreadyCopyDone(Map<String, Object> copyDone){
		//Paylod--->{division=FILE, repairShopNo=1, reqNo=1, fileNo=6, result=SUCCESS}
		try {
			System.out.println("copyDone-->" + copyDone);
			
			for(Iterator<Map<String, Object>> itr = needToSendLIst.iterator(); itr.hasNext();) {
				Map<String, Object> m = itr.next();
				
				if (copyDone.get("division").equals("DIRECTORY")) {
					System.out.println("DIRECTORY--->" + m);
					if (m.get("division").toString().equals("DIRECTORY") && m.get("reqNo").toString().equals(copyDone.get("reqNo").toString())){
						itr.remove();
					}
				}else {
					System.out.println("FILE--->" + m);
					if (m.get("division").toString().equals("FILE") && m.get("fileNo").toString().equals(copyDone.get("fileNo").toString())){
						itr.remove();
					}
				}
			}
//			for(Map<String, Object> m: needToSendLIst) {
//				if (copyDone.get("division").equals("DIRECTORY")) {
//					System.out.println("DIRECTORY--->" + m);
//					if (m.get("division").toString().equals("DIRECTORY") && m.get("reqNo").toString().equals(copyDone.get("reqNo").toString())){
//						needToSendLIst.remove(m);
//					}
//				}else {
//					System.out.println("FILE--->" + m);
//					if (m.get("division").toString().equals("FILE") && m.get("fileNo").toString().equals(copyDone.get("fileNo").toString())){
//						needToSendLIst.remove(m);
//					}
//				}
//			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	@Scheduled(fixedDelay=2000)
	public synchronized void doSomething() throws Exception {
		
		try {
			if(repairShopList.size()==0) return;
			
			if(needToSendLIst!=null) {
				if (needToSendLIst.size()<10)
				needToSendLIst.addAll(repairShopService.getNeedToSenderListForTransffering());
			}else {
				needToSendLIst = repairShopService.getNeedToSenderListForTransffering();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}

		Gson gson = new Gson();
		//loop돌면서 sending
		for(Map<String, Object> m:needToSendLIst) {
			int repairShopNo = Integer.parseInt(String.valueOf(m.get("repairShopNo")));
			String msgStr = null;
			try {
				WebSocketSession repairShop = findRepairShopSession(repairShopNo);

				//if(repairShop!=null && repairShop.isOpen()) {
				if(m.get("division").equals("FILE")){
					String filePath = PropertyManager.get("Globals.photo.os.path") + m.get("serverPath");
					
					//filePath=filePath.replaceAll("/", File.separator);
					
					System.out.println("filePath==>" + filePath);
					
//					File file = new File(filePath);
//					InputStream in = new FileInputStream(file);
//					byte[] bytes = new byte[(int) file.length()];
					
					byte[] bytes = Files.readAllBytes(Paths.get(filePath));
					
					String b64Str = Base64.getEncoder().encodeToString(bytes);

					m.put("base64", b64Str);
					//m.put("clientPath", m.get("repairShopPhotoPath")+ "\\20" + m.get("year") + "\\" +  m.get("month") + "\\" + m.get("carLicenseNo"));
//					byte[] data = DatatypeConverter.parseBase64Binary(b64Str);
//					
//					FileOutputStream out = new FileOutputStream(new File("C:\\Temp3\\base64Test." + m.get("fileExt")));
//					out.write(data);
					
					
				}
				msgStr = gson.toJson(m);
				TextMessage message = new TextMessage(msgStr);
				System.out.println(repairShopNo + " 정비소에 메시지 전송");
				repairShop.sendMessage(message);
				//}
			}catch(IndexOutOfBoundsException e) {
				//System.out.println("ok");
			}catch(Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
	}
}
