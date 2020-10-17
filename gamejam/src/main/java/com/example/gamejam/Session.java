package com.example.gamejam;


import org.springframework.web.socket.WebSocketSession;


public class Session {

    private Session(WebSocketSession ses){
        this.wssession = ses;
    }

    public static Session generateSession(WebSocketSession ses){
        return new Session(ses);
    }

    public WebSocketSession getWsSession(){
        return this.wssession;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    private final WebSocketSession wssession;

    private String name;

    private Boolean isWebPage = false;

    public void setIsWebPage(Boolean isWebPage){
        this.isWebPage = isWebPage;
    }

    public Boolean getIsWebPage(){
        return this.isWebPage;
    }

}