package com.incidentiq.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AnomalyDetectionService {

    public boolean detectErrorSpike(
            int currentErrorCount,
            int previousErrorCount
    ) {

        if (previousErrorCount == 0) {
            return currentErrorCount >= 5;
        }

        double increasePercentage =
                ((double) (currentErrorCount - previousErrorCount)
                        / previousErrorCount) * 100;

        boolean spike =
                currentErrorCount >= 5
                        && increasePercentage >= 50;

        log.info(
                "Error spike check: previous={}, current={}, increase={}%, anomaly={}",
                previousErrorCount,
                currentErrorCount,
                String.format("%.2f", increasePercentage),
                spike
        );

        return spike;
    }
}