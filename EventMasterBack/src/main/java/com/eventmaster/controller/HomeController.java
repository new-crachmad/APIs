package com.eventmaster.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    // Rota raiz
    @GetMapping("/")
    public String home() {
        return "Olá, EventMaster está funcionando Mane kkkkkkk!";
    }

    // Exemplo de rota /status
    @GetMapping("/status")
    public String status() {
        return "Status: OK";
    }

    // Exemplo de rota /about
    @GetMapping("/about")
    public String about() {
        return "EventMaster API - v1.0.0";
    }
}
