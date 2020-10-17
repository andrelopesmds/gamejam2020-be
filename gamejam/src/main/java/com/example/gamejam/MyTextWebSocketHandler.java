package com.example.gamejam;
 
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
 
public class MyTextWebSocketHandler extends TextWebSocketHandler {
 
    private static final Logger LOGGER = LoggerFactory.getLogger(MyTextWebSocketHandler.class);
    
    private final List<Session> sessions = new CopyOnWriteArrayList<>();
 
    @Override
    public void afterConnectionEstablished(WebSocketSession wsSession) throws Exception {
        Session session = Session.generateSession(wsSession);
        sessions.add(session);
        super.afterConnectionEstablished(session.getWsSession());
    }
 
    @Override
    public void afterConnectionClosed(WebSocketSession wsSession, CloseStatus status) throws Exception {
        sessions.removeIf((e) -> wsSession.equals(e.getWsSession()));
        super.afterConnectionClosed(wsSession, status);
    }

    
 
    @Override
    protected void handleTextMessage(WebSocketSession wsSession, TextMessage message) throws Exception {
        super.handleTextMessage(wsSession, message);
        String buffer = message.toString();
        if(buffer.contains("open-")){
            for(Session session : sessions){
                if(session.equals(wsSession)){
                    String[] name = buffer.split("open-");
                    if(name.length > 1){
                        session.setName(name[1]);
                        break;
                    }
                   
                }
            }
        }
        
        sessions.forEach(session -> {
            try {
                session.getWsSession().sendMessage(message);
            } catch (IOException e) {
                LOGGER.error("Error occurred.", e);
            }
        });
    }
}
