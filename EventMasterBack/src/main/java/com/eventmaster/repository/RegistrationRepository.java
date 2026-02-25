package com.eventmaster.repository;

import com.eventmaster.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByEventId(Long eventId);

    List<Registration> findByUserId(Long userId);
}