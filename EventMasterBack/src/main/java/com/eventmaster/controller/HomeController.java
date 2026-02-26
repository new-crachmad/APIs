package com.eventmaster.controller;

import com.eventmaster.dto.EventResponseDTO;
import com.eventmaster.service.EventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HomeController {

    private final EventService eventService;

    public HomeController(EventService eventService) {
        this.eventService = eventService;
    }

    // Rota raiz
    @GetMapping("/")
    public String home() {
        List<EventResponseDTO> events = eventService.findAll();
        if (events.isEmpty()) {
            return "<h1>Nenhum evento cadastrado ainda.</h1><p>Use um cliente de API para criar um evento em POST /events.</p>";
        }

        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>EventMaster - Eventos</title></head>");
        html.append("<body style='font-family: sans-serif;'>");
        html.append("<h1>Eventos Cadastrados</h1>");

        for (EventResponseDTO event : events) {
            html.append("<div>");
            html.append("<h2>").append(event.title()).append("</h2>");
            html.append("<p>").append(event.description()).append("</p>");
            html.append("<p><b>Data:</b> ").append(event.startAt().toString()).append("</p>");
            html.append("<hr>");
            html.append("</div>");
        }

        html.append("</body></html>");
        return html.toString();
    }

    // Exemplo de rota /status
    @GetMapping("/status")
    public String status() {
        return "Status: OK";
    }
}
