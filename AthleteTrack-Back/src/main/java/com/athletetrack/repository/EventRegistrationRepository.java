package com.athletetrack.repository;

import com.athletetrack.entity.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    Optional<EventRegistration> findByUserIdAndEventId(Long userId, Long eventId);
    List<EventRegistration> findByUserId(Long userId);
    
    @Query("SELECT er.event.id FROM EventRegistration er WHERE er.user.id = :userId")
    List<Long> findEventIdsByUserId(@Param("userId") Long userId);
}
