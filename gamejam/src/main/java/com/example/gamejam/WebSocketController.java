package com.example.gamejam;
 
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class WebSocketController {
    
    @RequestMapping("/websocket")
    public String getWebSocket() {
        return "ws-broadcast";
    }


    @GetMapping(value = {"/", "/index"})
    public String index() {
        return "ws-broadcast";
    }
}