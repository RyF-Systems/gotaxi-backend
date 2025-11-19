package com.ryfsystems.ryftaxi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "redirect:/login.html";
    }
    
    @GetMapping("/chat")
    public String chat() {
        return "redirect:/login.html";
    }
}
