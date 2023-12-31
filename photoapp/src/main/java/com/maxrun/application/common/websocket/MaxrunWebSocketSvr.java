package com.maxrun.application.common.websocket;

import java.io.EOFException;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.maxrun.application.config.PropertyManager;
import com.maxrun.repairshop.service.RepairShopService;

public class MaxrunWebSocketSvr extends AbstractWebSocketHandler {
	@Autowired
	RepairShopService repairShopService;
	
	Logger logger = LogManager.getLogger(getClass());

	static List<Map<String, Object>> needToSendLIst = Collections.synchronizedList(new ArrayList<Map<String, Object>>());
	static Set<WebSocketSession> repairShopList = Collections.synchronizedSet(new HashSet<WebSocketSession>());

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
		System.out.println("connection closed");
		repairShopList.remove(session);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {

		Map<String, Object> userInfo = session.getAttributes();

		repairShopList.add(session);
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
		try {
			if (message instanceof PongMessage) {
				handlePongMessage(session, (PongMessage) message);
				return;
			}
			String paylod = (String) message.getPayload();

			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> msg = mapper.readValue(paylod, Map.class);

			repairShopService.completeCopyToRepairShop(msg);
			logger.info("socket client로부터 메시지 수신");

			if (msg.get("result").equals("FAIL")) {
				logger.info(msg.get("exception"));
			}

			removeWorkAlreadySent(msg); //복사작업완로된 건은 리스테에서 제거 

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
		logger.info(session.getId() + " pong message 수신 ");
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		//System.out.println("handleTransportError===>" + exception.getMessage());
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

	// cleint 로부터 처리완료 받은 메시지들은 메모리에서 바로 바로 삭제해준다
	private synchronized void removeWorkAlreadySent(Map<String, Object> copyDone) {
		try {
			boolean removed=false;

			for (Iterator<Map<String, Object>> itr = needToSendLIst.iterator(); itr.hasNext();) {
				Map<String, Object> m = itr.next();

				if (copyDone.get("division").equals("DIRECTORY")) {
					//System.out.println("DIRECTORY--->" + m);
					if (m.get("division").toString().equals("DIRECTORY")
							&& m.get("reqNo").toString().equals(copyDone.get("reqNo").toString())) {
						
						itr.remove();
						removed=true;
					}
				} else {
					//System.out.println("FILE--->" + m);
					if (m.get("division").toString().equals("FILE")
							&& m.get("fileNo").toString().equals(copyDone.get("fileNo").toString())) {
						itr.remove();
						removed=true;
					}
				}
			}
			
			if(removed==false) {
				logger.info("################################ 복사작업완된 정보객체게 메모리에서 삭제되지 않음 ###############################");
				if(copyDone.get("division").equals("DIRECTORY"))
					logger.info("copyDone.clientPath==>" + copyDone.get("clientPath").toString());
				else {
					logger.info("copyDone.clientPath==>" + copyDone.get("clientPath").toString());
					logger.info("copyDone.fileNo==>" + copyDone.get("fileNo").toString());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	@Scheduled(fixedDelay = 10000)
	public synchronized void sendPing() throws Exception{
		for(WebSocketSession w:repairShopList) {
			PingMessage pm = new PingMessage();
			logger.info("####### now ping send #######");
			w.sendMessage(pm);	//ping message
		}
	}

	@Scheduled(fixedDelay = 5000)
	public synchronized void transferDirNFile() throws Exception {
		
		Gson gson = new Gson();
		String msgStr = null;
		
		try {
			if (repairShopList.size() == 0)
				return;

			for(WebSocketSession w:repairShopList) {
				Map<String, Object> ws = w.getAttributes();
				logger.info("############################# Connected Session Info ################################");
				logger.info(ws.get("loginId"));
				logger.info("############################# Connected Session Info ################################");
				
				if("maxrun".equals("loginId")) {
					WebSocketSession repairShop = findRepairShopSession(-1);
					/*현재 접속중인 공업사 목록을 보낸다*/
					msgStr = gson.toJson(repairShopList);
					TextMessage message = new TextMessage(msgStr);
					repairShop.sendMessage(message);
					/*현재 소켓서버가 들고 있는 데이터목록을 보낸다*/
					msgStr = gson.toJson(needToSendLIst);
					message = new TextMessage(msgStr);
					repairShop.sendMessage(message);
				}
			}
			
			if (needToSendLIst == null || needToSendLIst.size() == 0) {
				needToSendLIst = repairShopService.getNeedToSenderListForTransffering();	
			} else /*if(needToSendLIst.size()<100)*/ {
				//System.out.println("needToSendLIst----->" + needToSendLIst);
				needToSendLIst.addAll(repairShopService.getNeedToSenderListForTransffering());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		for (Iterator<Map<String, Object>> itr = needToSendLIst.iterator(); itr.hasNext();) {
			Map<String, Object> m = itr.next();		

			int repairShopNo = Integer.parseInt(String.valueOf(m.get("repairShopNo")));
			
			try {
				WebSocketSession repairShop = findRepairShopSession(repairShopNo);
				
				if(repairShopNo != -1 && repairShop != null) {
					if (m.get("division").equals("FILE") /*&& !String.valueOf(m.get("status")).equals("sent")*/) {
						String filePath = PropertyManager.get("Globals.photo.os.path") + m.get("serverPath");

						//System.out.println("filePath==>" + filePath);

						if (Files.notExists(Paths.get(filePath))) {
							// 서버경로에 파일이 없어서
							logger.info(filePath + " is not exists");

						}else {
							byte[] bytes = Files.readAllBytes(Paths.get(filePath));

							String b64Str = Base64.getEncoder().encodeToString(bytes);

							m.put("base64", b64Str);
						}
						//m.put(", gson)
						//m.put("status", "sent");	//client로 전송했다는 플래그 값을 설정 
						msgStr = gson.toJson(m);
						TextMessage message = new TextMessage(msgStr);
						logger.info(repairShopNo + " 정비소에 파일 메시지 전송");
						//System.out.println(repairShopNo + "==>" + message.toString());
						
						repairShop.sendMessage(message);
						//itr.remove();
						//removeWorkAlreadySent(m);
					}else if(m.get("division").equals("DIRECTORY") /*&& !String.valueOf(m.get("status")).equals("sent")*/){//DIRECGTORY 인 경우 
						msgStr = gson.toJson(m);
						//logger.info("msgStr==>" + msgStr);
						TextMessage message = new TextMessage(msgStr);
						logger.info(repairShopNo + " 정비소에 메시지 디렉토리 명칭 전송");
						//System.out.println(repairShopNo + "==>" + message.toString());
						//m.put("status", "sent");
						repairShop.sendMessage(message);
						//itr.remove();
						//removeWorkAlreadySent(m);
					}
				}else if(repairShopNo==-1) {//client가 maxrun 인 경우

				}else {//정비소가 접속하지 않은 경우 
					
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
