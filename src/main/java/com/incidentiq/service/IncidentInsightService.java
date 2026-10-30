package com.incidentiq.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incidentiq.model.RootCauseInsight;
import com.incidentiq.model.RootCauseInsight.EvidenceHit;
import com.incidentiq.model.SearchHit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.incidentiq.model.Incident;
import com.incidentiq.repository.IncidentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IncidentInsightService {

    private final IncidentRepository incidentRepository;
    private final SearchService semanticSearchService;
    private final GeminiChatClient geminiChatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RootCauseInsight analyzeRootCause(String query, int topK) {

        // 1) Evidence collection
        List<SearchHit> hits = semanticSearchService.hybridSearch(query, topK);

        List<EvidenceHit> evidence = hits.stream()
                .map(h -> EvidenceHit.builder()
                        .id(h.getId())
                        .text(h.getText())
                        .meta(h.getMeta())
                        .score(h.getScore())
                        .build())
                .collect(Collectors.toList());

        String context = evidence.stream()
                .map(e -> "### Evidence (score=" + e.getScore() + ")\n" + e.getText())
                .collect(Collectors.joining("\n\n"));

        // 2) Prompt
        String prompt = """
                You are an expert SRE / DevOps AI assistant.
                
                User Query:
                %s
                
                Evidence Logs:
                %s
                
                Provide a STRICT JSON response ONLY:
                {
                  "summary": "...",
                  "root_cause": "...",
                  "impact": "...",
                  "actions": "...",
                  "confidence": "HIGH/MEDIUM/LOW"
                }
                
                DO NOT wrap the response in ```json blocks.
                """.formatted(query, context);

        // 3) Call Gemini
        String raw;
        try {
            raw = geminiChatClient.chat(prompt);
        } catch (Exception ex) {
            log.error("AI root cause analysis failed for query='{}'", query, ex);
            throw new RuntimeException("AI Analysis failed: " + ex.getMessage());
        }

        // 4) Clean + Parse LLM JSON
        JsonNode json = safeJson(raw);

        String summary = safeField(json, "summary");
        String root = safeField(json, "root_cause");
        String impact = safeField(json, "impact");
        String actions = safeField(json, "actions");
        String confidence = safeField(json, "confidence");

        // 5) Automatically create an Incident in PostgreSQL
        // 5) Check whether this incident is already being tracked
        // 5) Check whether this incident is already being tracked
        List<String> activeStatuses = List.of("OPEN", "IN_PROGRESS");

        var existingIncident =
                incidentRepository.findFirstBySourceQueryAndStatusInOrderByCreatedAtDesc(
                        query,
                        activeStatuses
                );

        if (existingIncident.isPresent()) {

            Incident incident = existingIncident.get();

            log.info(
                    "Existing active incident found: id={}, query='{}'",
                    incident.getId(),
                    query
            );

        } else {

            // 6) Create a new Incident in PostgreSQL
            Incident incident = new Incident();

            incident.setTitle(
                    summary != null && !summary.isBlank()
                            ? summary
                            : "AI Detected Incident"
            );

            incident.setSeverity(determineIncidentSeverity(evidence, confidence));

            incident.setDescription(
                    "Incident automatically detected and analyzed by IntelliOps. "
                            + "Original query: " + query
            );

            incident.setSourceQuery(query);
            incident.setRootCause(root);
            incident.setImpact(impact);
            incident.setRecommendedActions(actions);
            incident.setConfidenceLevel(confidence);
            incident.setStatus("OPEN");

            incidentRepository.save(incident);

            log.info(
                    "New incident created automatically: id={}, severity={}, confidence={}",
                    incident.getId(),
                    incident.getSeverity(),
                    confidence
            );
        }

        // 6) Build response
        return RootCauseInsight.builder()
                .query(query)
                .rootCauseSummary(summary)
                .probableRootCause(root)
                .impact(impact)
                .recommendedActions(actions)
                .confidenceLevel(confidence)
                .rawAnalysis(raw)
                .evidence(evidence)
                .build();
    }

    private boolean containsSeverity(String text, String severity) {

        if (text == null || text.isBlank()) {
            return false;
        }

        return text.toUpperCase()
                .matches("(?s).*\\b" + severity + "\\b.*");
    }

    private String determineIncidentSeverity(
            List<EvidenceHit> evidence,
            String confidence
    ) {

        boolean hasCritical = evidence.stream()
                .anyMatch(e -> containsSeverity(e.getText(), "CRITICAL")
                        || containsSeverity(e.getText(), "FATAL"));

        if (hasCritical) {
            return "CRITICAL";
        }

        boolean hasError = evidence.stream()
                .anyMatch(e -> containsSeverity(e.getText(), "ERROR"));

        if (hasError) {
            return "CRITICAL";
        }

        boolean hasWarning = evidence.stream()
                .anyMatch(e -> containsSeverity(e.getText(), "WARN")
                        || containsSeverity(e.getText(), "WARNING"));

        if (hasWarning) {
            return "WARNING";
        }

        return "INFO";
    }

    private String determineSeverity(String confidence) {

        if (confidence == null) {
            return "MEDIUM";
        }

        return switch (confidence.toUpperCase()) {
            case "HIGH" -> "CRITICAL";
            case "MEDIUM" -> "WARNING";
            case "LOW" -> "INFO";
            default -> "MEDIUM";
        };
    }

    /**
     * Removes Markdown fences (```json ... ```) before JSON parsing.
     */
    private JsonNode safeJson(String str) {
        try {
            String cleaned = str
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            log.info("Cleaned AI JSON: {}", cleaned);

            return objectMapper.readTree(cleaned);
        } catch (Exception e) {
            log.warn("AI returned invalid JSON. Raw returned string: {}", str);
            return objectMapper.createObjectNode();
        }
    }

    /**
     * Safely extracts field from JSON without crashing.
     */
    private String safeField(JsonNode json, String field) {
        try {
            if (json.has(field)) {
                return json.get(field).asText();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void analyzeAutomaticIncident(Incident incident) {

        String evidence = incident.getSourceQuery();

        String prompt = """
            You are an expert SRE and DevOps incident analysis assistant.

            An anomaly was automatically detected by IntelliOps.

            Incident:
            %s

            Analyze the evidence logs and determine the most probable
            root cause, impact, recommended actions, and confidence.

            Return STRICT JSON ONLY:

            {
              "root_cause": "...",
              "impact": "...",
              "actions": "...",
              "confidence": "HIGH/MEDIUM/LOW"
            }

            Do not wrap the response in markdown or ```json.
            """.formatted(evidence);

        try {

            String raw = geminiChatClient.chat(prompt);

            JsonNode json = safeJson(raw);

            String rootCause = safeField(json, "root_cause");
            String impact = safeField(json, "impact");
            String actions = safeField(json, "actions");
            String confidence = safeField(json, "confidence");

            if (rootCause != null && !rootCause.isBlank()) {
                incident.setRootCause(rootCause);
            }

            if (impact != null && !impact.isBlank()) {
                incident.setImpact(impact);
            }

            if (actions != null && !actions.isBlank()) {
                incident.setRecommendedActions(actions);
            }

            if (confidence != null && !confidence.isBlank()) {
                incident.setConfidenceLevel(confidence);
            }

            incidentRepository.save(incident);

            log.info(
                    "Automatic RCA completed for incident {}",
                    incident.getId()
            );

        } catch (Exception ex) {

            log.error(
                    "Automatic RCA failed for incident {}",
                    incident.getId(),
                    ex
            );
        }
    }
}
