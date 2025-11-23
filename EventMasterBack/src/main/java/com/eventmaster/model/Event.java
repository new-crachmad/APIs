package com.eventmaster.model;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;


@Data
@Entity
public class Event {
@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


private String title;
private String description;
private LocalDateTime startAt;


@ManyToOne
@JoinColumn(name = "category_id")
private Category category;


@ManyToOne
@JoinColumn(name = "organizer_id")
private User organizer;


public Object getDate() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getDate'");
}


public void setDate(Object date) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'setDate'");
}


public Object getName() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getName'");
}


public void setName(Object name) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'setName'");
}
}