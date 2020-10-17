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

    private String title = "Tooot";


    @GetMapping(value = {"/", "/index"})
    public String index() {
        System.out.println("JAHUU");
        return "index";
    }
}