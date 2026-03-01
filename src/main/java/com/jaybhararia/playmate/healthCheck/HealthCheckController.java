package com.jaybhararia.playmate.healthCheck;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/public")
    public String allAccess(){
        return "public";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/private")
    public String vipAccess(){
        return "VIP";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminAccess(){return "ADMIN";}
}


