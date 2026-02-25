package com.eventmaster.repository;

import com.eventmaster.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCategoryId(Long categoryId);

    List<Event> findByOrganizerId(Long organizerId);
}