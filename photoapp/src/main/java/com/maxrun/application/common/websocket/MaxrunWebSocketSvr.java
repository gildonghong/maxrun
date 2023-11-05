package com.maxrun.application.common.websocket;

import java.io.EOFException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.maxrun.application.config.PropertyManager;
import com.maxrun.application.exception.BizExType;
import com.maxrun.application.exception.BizException;
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
//		System.out.println("connection created");
//		Map<String, Object> userInfo = session.getAttributes();
//		System.out.println("userInfo ==========>" + userInfo);
//		System.out.println("uri ==========>" + session.getUri());
		Map<String, Object> userInfo = session.getAttributes();
		
//		for(WebSocketSession client:repairShopList) {
//			Map<String, Object> info= client.getAttributes();
//			if(info.get("loginId").equals(userInfo.get("loginId")))	//기접속 클라이언트가 다시 접속한 경우 
//				return;
//		}
		repairShopList.add(session);
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {

		String paylod = (String) message.getPayload();

		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> msg = mapper.readValue(paylod, Map.class);

			// System.out.println("paylod--->" + msg);
			repairShopService.completeCopyToRepairShop(msg);
			
			if (msg.get("result").equals("FAIL")) {
				System.out.println(msg.get("exception"));
			}

			//removeWorkAlreadySent(msg); //복사작업완로된 건은 리스테에서 제거 

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		System.out.println("handleTransportError===>" + exception.getMessage());
	}

	private WebSocketSession findRepairShopSession(int repairShopNo) {
		Map<String, Object> userInfo = null;
		for (WebSocketSession session : repairShopList) {
			userInfo = session.getAttributes();

			if (userInfo.get("repairShopNo").equals(repairShopNo)) {
				return session;
			}
		}
		return null;
	}

//	// cleint 로부터 처리완료 받은 메시지들은 메모리에서 바로 바로 삭제해준다
//	private synchronized void removeWorkAlreadySent(Map<String, Object> copyDone) {
//		try {
//			boolean removed=false;
//
//			for (Iterator<Map<String, Object>> itr = needToSendLIst.iterator(); itr.hasNext();) {
//				Map<String, Object> m = itr.next();
//
//				if (copyDone.get("division").equals("DIRECTORY")) {
//					//System.out.println("DIRECTORY--->" + m);
//					if (m.get("division").toString().equals("DIRECTORY")
//							&& m.get("reqNo").toString().equals(copyDone.get("reqNo").toString())) {
//						
//						itr.remove();
//						removed=true;
//					}
//				} else {
//					//System.out.println("FILE--->" + m);
//					if (m.get("division").toString().equals("FILE")
//							&& m.get("fileNo").toString().equals(copyDone.get("fileNo").toString())) {
//						itr.remove();
//						removed=true;
//					}
//				}
//			}
//			
//			if(removed==false) {
//				//System.out.println("copyDone.divisiton==>" + copyDone.get("division").toString());
//				if(copyDone.get("division").equals("DIRECTORY"))
//					System.out.println("copyDone.clientPath==>" + copyDone.get("clientPath").toString());
//				else {
//					System.out.println("copyDone.clientPath==>" + copyDone.get("clientPath").toString());
//					System.out.println("copyDone.fileNo==>" + copyDone.get("fileNo").toString());
//				}
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//
//	}

	@Scheduled(fixedDelay = 5000)
	public synchronized void transferDirNFile() throws Exception {

		try {
			if (repairShopList.size() == 0)
				return;
			
			for(WebSocketSession w:repairShopList) {
				Map<String, Object> ws = w.getAttributes();
				System.out.println("############################# Connected Session Info ################################");
				System.out.println(ws.get("loginId"));
				System.out.println("############################# Connected Session Info ################################");
			}

			if (needToSendLIst == null || needToSendLIst.size() == 0) {
				needToSendLIst = repairShopService.getNeedToSenderListForTransffering();	
			} else if(needToSendLIst.size()<100) {
				needToSendLIst.addAll(repairShopService.getNeedToSenderListForTransffering());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Gson gson = new Gson();
		// loop돌면서 sending
		//for (Map<String, Object> m : needToSendLIst) {
			
		for (Iterator<Map<String, Object>> itr = needToSendLIst.iterator(); itr.hasNext();) {
			Map<String, Object> m = itr.next();		
			
			
			int repairShopNo = Integer.parseInt(String.valueOf(m.get("repairShopNo")));
			String msgStr = null;
			try {
				WebSocketSession repairShop = findRepairShopSession(repairShopNo);
				
				if(repairShop != null) {
					if (m.get("division").equals("FILE") /*&& !String.valueOf(m.get("status")).equals("sent")*/) {
						String filePath = PropertyManager.get("Globals.photo.os.path") + m.get("serverPath");

						System.out.println("filePath==>" + filePath);

						if (Files.notExists(Paths.get(filePath))) {
							// 서버경로에 파일이 없어서
							System.out.println(filePath + " is not exists");

						}else {
							byte[] bytes = Files.readAllBytes(Paths.get(filePath));

							String b64Str = Base64.getEncoder().encodeToString(bytes);

							m.put("base64", b64Str);
						}
						//m.put(", gson)
						//m.put("status", "sent");	//client로 전송했다는 플래그 값을 설정 
						msgStr = gson.toJson(m);
						TextMessage message = new TextMessage(msgStr);
						System.out.println(repairShopNo + " 정비소에 메시지 전송");
						//System.out.println(repairShopNo + "==>" + message.toString());
						
						repairShop.sendMessage(message);
						itr.remove();
						//removeWorkAlreadySent(m);
					}else if(m.get("division").equals("DIRECTORY") /*&& !String.valueOf(m.get("status")).equals("sent")*/){//DIRECGTORY 인 경우 
						msgStr = gson.toJson(m);
						System.out.println("msgStr==>" + msgStr);
						TextMessage message = new TextMessage(msgStr);
						System.out.println(repairShopNo + " 정비소에 메시지 전송");
						//System.out.println(repairShopNo + "==>" + message.toString());
						//m.put("status", "sent");
						repairShop.sendMessage(message);
						itr.remove();
						//removeWorkAlreadySent(m);
					}
				}else {	//정비소가 접속하지 않은 경우 

				}
				// }
			} catch (IndexOutOfBoundsException e) {
				m.put("exception", e.getMessage());
				repairShopService.regWSException(m);
				// System.out.println("ok");
			} catch (EOFException e) {
				m.put("exception", e.getMessage());
				System.out.println("message===>" + m);
				repairShopService.regWSException(m);

				e.printStackTrace();
				// throw e;
			} catch (NullPointerException e) {
				m.put("exception", e.getMessage());
				System.out.println("message===>" + m);
				repairShopService.regWSException(m);

				e.printStackTrace();
				// throw e;
			} catch (Exception e) {
				m.put("exception", e.getMessage());
				System.out.println("message===>" + m);
				repairShopService.regWSException(m);

				e.printStackTrace();
				// throw e;
			}
		}
	}
}
