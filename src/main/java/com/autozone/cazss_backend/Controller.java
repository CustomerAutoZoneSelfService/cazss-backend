package com.autozone.cazss_backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }
}
