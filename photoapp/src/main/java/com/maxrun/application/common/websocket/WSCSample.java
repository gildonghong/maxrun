package com.maxrun.application.common.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.Map;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

@Component

@ServerEndpoint("/echo")

public class WSCSample {

	static List<Map<String, Object>> lst = null;


	static Set<Session> sessionUsers = Collections.synchronizedSet(new HashSet<Session>());
	
		
	
	/**
	
	 * When the client try to connection to websocket server,
	
	 * open session and add information to the collection.
	
	 *
	
	 * @author GOEDOKID
	
	 * @since 2015. 3. 18. 
	
	 * @param Session userSession
	
	 * @return
	
	 */
	
	@OnOpen
	public void handleOpen(Session userSession) {
	
		System.out.println("WebSocket : Client Session is Open. ID is "+ userSession.getId());
	
		sessionUsers.add(userSession);
	
	}
	
	
	
	/**
	
	 * Send to message designated client by "websocket.send()" command.
	
	 *
	
	 * @author GOEDOKID
	
	 * @since 2015. 3. 18. 
	
	 * @param String message
	
	 * @return
	
	 */
	
	@OnMessage
	
	public void handleMessage(String message) throws IOException {
	
		Iterator<Session> iterator = sessionUsers.iterator();
	
		System.out.println("WebSocket : Send message to all client.");
	
		if(sessionUsers.size() > 0) {
	
			while(iterator.hasNext()) {
	
				iterator.next().getBasicRemote().
	
	                   sendText(JSONConverter(message, "message", "event"));
	
			}	
	
		} else {
	
			System.out.println("WebSocket : Here is no registered destination.");
	
		}
	
	}
	
	
	
	/**
	
	 * Session remove When browser down or close by client control
	
	 * 
	
	 * @author GOEDOKID
	
	 * @since 2015. 3. 18. 
	
	 * @param 
	
	 * @return
	
	 */
	
	@OnClose
	
	public void handleClose(Session session) {
	
		System.out.println("WebSocket : Session remove complete. ID is "+session.getId());
	
		sessionUsers.remove(session);
	
	}
	
		
	
	public String JSONConverter(String message, String command, String type) {
	
		JSONObject jsonObject = new JSONObject();
	
		jsonObject.put("type", type);
	
		jsonObject.put("command", command);
	
		jsonObject.put("message", message);
	
		return jsonObject.toString();
	
	}

}