package com.hospital.management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("")
    public String root() { 
        return "redirect:/login"; 
    }
    
    @GetMapping("/")
    public String home() { 
        return "redirect:/login"; 
    }
}
