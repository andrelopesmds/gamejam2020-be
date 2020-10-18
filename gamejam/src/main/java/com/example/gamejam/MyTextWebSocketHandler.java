package com.example.gamejam;
 
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
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
    

    private static final String SECRET = "D#KDWDLKEWRK#WRXXXXXXXX-IMWEBPAGE";
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
        broadCastNewPlayers();
    }
 
    @Override
    protected void handleTextMessage(WebSocketSession wsSession, TextMessage message) throws Exception {
        super.handleTextMessage(wsSession, message);
        String buffer = message.getPayload();

        Session session = getMatchingSession(wsSession);
        if(session == null){
            LOGGER.error("session cannot be found from the cache...", wsSession);
            return;
        }
        if(buffer.contains("open-")){
            String[] name = buffer.split("open-");
            if(name.length > 1){
                session.setName(name[1]);
            }
            broadCastNewPlayers();
            return;
        }
        //Simple secret to determine that webpage is connecting
        else if (buffer.equals(SECRET)){
            session.setIsWebPage(true);
            return;
        } else if (buffer.equals("WEBPAGE-REQUEST-PLAYERS")){
            broadCastNewPlayers();
            return;
            
        }
        
        broadCastEveryone(message);
    }

    private void broadCastNewPlayers(){
        broadCastWebPages(new TextMessage("PLAYERS-" + getActiveSessions()));
    }

    private void broadCastWebPages(TextMessage message){
        sessions.forEach(ses -> {
            if(ses.getIsWebPage() == true){
                try {
                    LOGGER.debug("SENDING ", message);
                    ses.getWsSession().sendMessage(message);
                } catch (IOException e) {
                    LOGGER.error("Error occurred.", e);
                }
            }
        });
    }

    private void broadCastEveryone(TextMessage message){
        sessions.forEach(ses -> {
            try {
                ses.getWsSession().sendMessage(message);
            } catch (IOException e) {
                LOGGER.error("Error occurred.", e);
            }
        });
    }

    private void broadCastPlayers(TextMessage message){
        sessions.forEach(ses -> {
            if(ses.getIsWebPage() == false){
                try {
                    LOGGER.debug("SENDING ", message);
                    ses.getWsSession().sendMessage(message);
                } catch (IOException e) {
                    LOGGER.error("Error occurred.", e);
                }
            }
        });
    }

    private Session getMatchingSession(WebSocketSession wsSession){
        for(Session session : sessions){
            if(wsSession.equals(session.getWsSession())){
                return session;
            }
        }
        return null;
    }

    public List<String> getActiveSessions() {
        List<String> names = new ArrayList<String>();
        sessions.forEach((s) -> {
            if(s.getName() != null){
                names.add(s.getName());
            }
        });
        return names;
    }
}
