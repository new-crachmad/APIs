package com.eventmaster.model;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;


@Data
@Entity
public class Registration {
@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


@ManyToOne
@JoinColumn(name = "event_id")
private Event event;


@ManyToOne
@JoinColumn(name = "user_id")
private User user;


private LocalDateTime registeredAt = LocalDateTime.now();
}