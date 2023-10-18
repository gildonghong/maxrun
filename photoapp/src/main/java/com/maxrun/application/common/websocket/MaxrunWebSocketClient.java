package com.maxrun.application.common.websocket;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

//@Service
public class MaxrunWebSocketClient {
	//private static final Logger LOGGER = LoggerFactory.getLogger(MaxrunWebSocketClient.class);
	
    @PostConstruct
    public void connect() {
        try {
            WebSocketClient webSocketClient = new StandardWebSocketClient();
 
            WebSocketSession webSocketSession = webSocketClient.doHandshake(new TextWebSocketHandler() {
                @Override
                public void handleTextMessage(WebSocketSession session, TextMessage message) {
                    //LOGGER.info("received message - " + message.getPayload());
                }
 
                @Override
                public void afterConnectionEstablished(WebSocketSession session) {
                    //LOGGER.info("established connection - " + session);
                }
            }, new WebSocketHttpHeaders(), URI.create("ws://localhost/socket")).get();
 
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                try {
                    TextMessage message = new TextMessage("Hello !!");
                    webSocketSession.sendMessage(message);
                    //LOGGER.info("sent message - " + message.getPayload());
                } catch (Exception e) {
                    //LOGGER.error("Exception while sending a message", e);
                }
            }, 1, 10, TimeUnit.SECONDS);
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        	e.printStackTrace();
            //LOGGER.error("Exception while accessing websockets", e);
        }
    }
}
