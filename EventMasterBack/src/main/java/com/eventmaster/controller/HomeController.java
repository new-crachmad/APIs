package com.eventmaster.controller;

import com.eventmaster.model.Event;
import com.eventmaster.repository.EventRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HomeController {

    private final EventRepository eventRepository;

    public HomeController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    // Rota raiz
    @GetMapping("/")
    public String home() {
        List<Event> events = eventRepository.findAll();
        if (events.isEmpty()) {
            return "<h1>Nenhum evento cadastrado ainda.</h1><p>Use o Postman para criar um evento em POST /events.</p>";
        }

        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>EventMaster - Eventos</title></head>");
        html.append("<body style='font-family: sans-serif;'>");
        html.append("<h1>Eventos Cadastrados</h1>");

        for (Event event : events) {
            html.append("<div>");
            html.append("<h2>").append(event.getTitle()).append("</h2>");
            html.append("<p>").append(event.getDescription()).append("</p>");
            html.append("<p><b>Data:</b> ").append(event.getStartAt().toString()).append("</p>");
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
