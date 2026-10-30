package com.incidentiq.controller;

import com.incidentiq.model.Incident;
import com.incidentiq.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @PostMapping
    public Incident createIncident(@RequestBody Incident incident) {
        return incidentService.createIncident(incident);
    }

    @GetMapping
    public List<Incident> getAllIncidents() {
        return incidentService.getAllIncidents();
    }

    @GetMapping("/{id}")
    public Incident getIncident(@PathVariable UUID id) {
        return incidentService.getIncident(id);
    }

    @DeleteMapping("/{id}")
    public String deleteIncident(@PathVariable UUID id) {
        incidentService.deleteIncident(id);
        return "Incident deleted successfully";
    }

    @GetMapping("/status/{status}")
    public List<Incident> getIncidentsByStatus(
            @PathVariable String status
    ) {
        return incidentService.getIncidentsByStatus(status);
    }

    @GetMapping("/severity/{severity}")
    public List<Incident> getIncidentsBySeverity(
            @PathVariable String severity
    ) {
        return incidentService.getIncidentsBySeverity(severity);
    }

    @PutMapping("/{id}/status")
    public Incident updateStatus(
            @PathVariable UUID id,
            @RequestParam String status
    ) {
        return incidentService.updateStatus(id, status);
    }
}
