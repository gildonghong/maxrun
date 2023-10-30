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
		System.out.println("connection created");
		Map<String, Object> userInfo = session.getAttributes();
		System.out.println("userInfo ==========>" + userInfo);
		System.out.println("uri ==========>" + session.getUri());

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

			//if(msg.get("result").equals("SUCCESS")) 
			removeWorkAlreadySent(msg); //복사작업

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

	// cleint 로 보낸 메시지들은 메모리에서 바로 바로 삭제해준다
	private synchronized void removeWorkAlreadySent(Map<String, Object> copyDone) {
		try {
			System.out.println("copyDone-->" + copyDone);

			for (Iterator<Map<String, Object>> itr = needToSendLIst.iterator(); itr.hasNext();) {
				Map<String, Object> m = itr.next();

				if (copyDone.get("division").equals("DIRECTORY")) {
					System.out.println("DIRECTORY--->" + m);
					if (m.get("division").toString().equals("DIRECTORY")
							&& m.get("reqNo").toString().equals(copyDone.get("reqNo").toString())) {
						itr.remove();
					}
				} else {
					System.out.println("FILE--->" + m);
					if (m.get("division").toString().equals("FILE")
							&& m.get("fileNo").toString().equals(copyDone.get("fileNo").toString())) {
						itr.remove();
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Scheduled(fixedDelay = 2000)
	public synchronized void doSomething() throws Exception {

		try {
			if (repairShopList.size() == 0)
				return;

			if (needToSendLIst == null) {
				needToSendLIst = repairShopService.getNeedToSenderListForTransffering();	
			} else if(needToSendLIst.size()<100) {
				needToSendLIst.addAll(repairShopService.getNeedToSenderListForTransffering());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Gson gson = new Gson();
		// loop돌면서 sending
		for (Map<String, Object> m : needToSendLIst) {
			int repairShopNo = Integer.parseInt(String.valueOf(m.get("repairShopNo")));
			String msgStr = null;
			try {
				WebSocketSession repairShop = findRepairShopSession(repairShopNo);
				
				if(repairShop != null) {
					if (m.get("division").equals("FILE")) {
						String filePath = PropertyManager.get("Globals.photo.os.path") + m.get("serverPath");

						System.out.println("filePath==>" + filePath);

						if (Files.notExists(Paths.get(filePath))) {
							// 서버경로에 파일이 없어서
							System.out.println(filePath + " is not exists");
							m.put("exception", "서버경로에 파일이 없습니다");

							repairShopService.regWSException(m); // 예외발생을 기록함
						} else {
							byte[] bytes = Files.readAllBytes(Paths.get(filePath));

							String b64Str = Base64.getEncoder().encodeToString(bytes);

							m.put("base64", b64Str);
							
							msgStr = gson.toJson(m);
							TextMessage message = new TextMessage(msgStr);
							System.out.println(repairShopNo + " 정비소에 메시지 전송");
							System.out.println(repairShopNo + "==>" + message.toString());

							repairShop.sendMessage(message);
						}
					}else {//DIRECGTORY 인 경우 
						msgStr = gson.toJson(m);
						TextMessage message = new TextMessage(msgStr);
						System.out.println(repairShopNo + " 정비소에 메시지 전송");
						System.out.println(repairShopNo + "==>" + message.toString());

						repairShop.sendMessage(message);
					}
					
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
