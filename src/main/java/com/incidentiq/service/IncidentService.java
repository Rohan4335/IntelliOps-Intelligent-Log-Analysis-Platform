package com.incidentiq.service;

import com.incidentiq.model.Incident;
import com.incidentiq.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;

    public Incident createIncident(Incident incident) {
        return incidentRepository.save(incident);
    }

    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    public Incident getIncident(UUID id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));
    }

    public void deleteIncident(UUID id) {
        incidentRepository.deleteById(id);
    }

    public List<Incident> getIncidentsByStatus(String status) {

        String normalizedStatus = status.toUpperCase();

        if (!List.of("OPEN", "IN_PROGRESS", "RESOLVED")
                .contains(normalizedStatus)) {

            throw new IllegalArgumentException(
                    "Invalid status. Allowed values: OPEN, IN_PROGRESS, RESOLVED"
            );
        }

        return incidentRepository.findByStatus(normalizedStatus);
    }

    public List<Incident> getIncidentsBySeverity(String severity) {

        String normalizedSeverity = severity.toUpperCase();

        if (!List.of("CRITICAL", "WARNING", "INFO", "MEDIUM")
                .contains(normalizedSeverity)) {

            throw new IllegalArgumentException(
                    "Invalid severity. Allowed values: CRITICAL, WARNING, INFO, MEDIUM"
            );
        }

        return incidentRepository.findBySeverity(normalizedSeverity);
    }

    public Incident updateStatus(UUID id, String status) {

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));

        String normalizedStatus = status.toUpperCase();

        if (!List.of("OPEN", "IN_PROGRESS", "RESOLVED")
                .contains(normalizedStatus)) {

            throw new IllegalArgumentException(
                    "Invalid status. Allowed values: OPEN, IN_PROGRESS, RESOLVED"
            );
        }



        incident.setStatus(normalizedStatus);

        return incidentRepository.save(incident);
    }

    public Incident createAnomalyIncident(
            String source,
            String logText,
            int errorCount
    ) {

            Optional<Incident> existingIncident =
                incidentRepository.findFirstByTitleAndStatusOrderByCreatedAtDesc(
                        "Error Spike Detected",
                        "OPEN"
                );

        if (existingIncident.isPresent()) {

            Incident incident = existingIncident.get();

            System.out.println(
                    "Existing active anomaly incident found: "
                            + incident.getId()
            );

            return incident;
        }

        Incident incident = new Incident();

        incident.setTitle("Error Spike Detected");
        incident.setSeverity("CRITICAL");

        incident.setDescription(
                "IntelliOps detected an abnormal spike in application errors. "
                        + "Source: " + source
                        + ", Error count: " + errorCount
        );

        incident.setRootCause(
                "Abnormal increase in ERROR/CRITICAL/FATAL log events detected."
        );

        incident.setImpact(
                "The application may be experiencing a service failure "
                        + "or degraded performance."
        );

        incident.setRecommendedActions(
                "Investigate the related ERROR logs, identify the affected "
                        + "service, and verify database/network dependencies."
        );

        incident.setConfidenceLevel("HIGH");
        incident.setStatus("OPEN");

        incident.setSourceQuery(
                "Automatic anomaly detection: error spike\n\n"
                        + "Evidence Logs:\n"
                        + logText
        );

        Incident savedIncident =
                incidentRepository.save(incident);

        System.out.println(
                "Automatic anomaly incident created: "
                        + savedIncident.getId()
        );

        return savedIncident;
    }
}