package com.incidentiq.controller;

import com.incidentiq.service.AnomalyDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/anomaly")
@RequiredArgsConstructor
public class AnomalyController {

    private final AnomalyDetectionService anomalyDetectionService;

    @GetMapping("/error-spike")
    public Map<String, Object> detectErrorSpike(
            @RequestParam int current,
            @RequestParam int previous
    ) {

        boolean anomaly =
                anomalyDetectionService.detectErrorSpike(
                        current,
                        previous
                );

        return Map.of(
                "currentErrorCount", current,
                "previousErrorCount", previous,
                "anomalyDetected", anomaly
        );
    }
}
