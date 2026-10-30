package com.incidentiq.repository;

import com.incidentiq.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IncidentRepository extends JpaRepository<Incident, UUID> {

    Optional<Incident> findFirstBySourceQueryAndStatusInOrderByCreatedAtDesc(
            String sourceQuery,
            List<String> statuses
    );

    Optional<Incident> findFirstByTitleAndStatusOrderByCreatedAtDesc(
            String title,
            String status
    );

    List<Incident> findByStatus(String status);
    List<Incident> findBySeverity(String severity);
}
