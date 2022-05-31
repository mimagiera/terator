package com.terator.service.inductionLoops;

import com.terator.model.inductionLoops.AggregatedTraffic;
import com.terator.model.inductionLoops.AggregatedTrafficBySegment;
import com.terator.model.inductionLoops.DetectorsWithSegmentId;
import com.terator.model.inductionLoops.InfluenceDetectorSegment;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
/*
  Can be used to aggreaget dta from induction loops by detector id or segment id
 */
public class AggregateTrafficData {

    private final TrafficService trafficService;
    private final AggregatedTrafficService aggregatedTrafficService;
    private final AggregatedTrafficBySegmentService aggregatedTrafficBySegmentService;
    private final InfluenceDetectorSegmentService influenceDetectorSegmentService;
    private final InductionLoopsDataExtractor inductionLoopsDataExtractor;

    public void aggregateBySegmentId() {
        var detectorsWithSegmentId = inductionLoopsDataExtractor.getDetectorsWithSegmentId();

        detectorsWithSegmentId.stream()
                .sorted(Comparator.comparingInt(DetectorsWithSegmentId::segmentId))
                .forEach(detectorsInSegmentWithSegmentId -> {
                    var aggregatedTrafficBySegments = aggregateForSegment(detectorsInSegmentWithSegmentId);
                    System.out.println("Saving for segment: " + detectorsInSegmentWithSegmentId.segmentId());
                    aggregatedTrafficBySegmentService.saveAll(aggregatedTrafficBySegments);
                    System.out.println("Saved for segment: " + detectorsInSegmentWithSegmentId.segmentId());
                });
    }

    private Set<AggregatedTrafficBySegment> aggregateForSegment(DetectorsWithSegmentId detectorsWithSegmentId) {
        var detectorsIds = detectorsWithSegmentId.detectorIds();
        var segmentId = detectorsWithSegmentId.segmentId();

        // map of day and hour to count
        Map<Pair<LocalDate, Integer>, Integer> result = new LinkedHashMap<>();

        detectorsIds.forEach(detectorId -> {
            var dataInSegment = aggregatedTrafficService.getByDetectorId(detectorId);
            dataInSegment.forEach(aggregatedTraffic -> {
                var date = aggregatedTraffic.getDate();
                var hour = aggregatedTraffic.getHour();
                var count = aggregatedTraffic.getCount();
                var key = new Pair<>(date, hour);

                result.put(key, result.getOrDefault(key, 0) + count);
            });
        });

        return result.entrySet().stream().map(pairIntegerEntry -> {
                    var day = pairIntegerEntry.getKey().getFirst();
                    var hour = pairIntegerEntry.getKey().getSecond();
                    var count = pairIntegerEntry.getValue();
                    return new AggregatedTrafficBySegment(segmentId, day, hour, count);
                })
                .collect(Collectors.toSet());
    }

    public void aggregateByDetectorId() {
        var detectorIds = influenceDetectorSegmentService.findAll().stream()
                .map(InfluenceDetectorSegment::getDetectorId)
                .collect(Collectors.toSet());
        detectorIds.forEach(detectorId -> {
            var aggregatedTraffics = aggregateForDetector(detectorId);
            System.out.println("Saving for detector: " + detectorId);
            aggregatedTrafficService.saveAll(aggregatedTraffics);
            System.out.println("Saved for detector: " + detectorId);
        });
    }

    private Set<AggregatedTraffic> aggregateForDetector(Integer detectorId) {
        var data = trafficService.findByDetectorId(detectorId);

        // map of day and hour to count
        Map<Pair<LocalDate, Integer>, Integer> result = new LinkedHashMap<>();

        data.forEach(traffic -> {
            final LocalDateTime startTime = traffic.getStarttime();
            var day = startTime.toLocalDate();
            var hour = startTime.getHour();
            var count = traffic.getCount();
            var key = new Pair<>(day, hour);

            result.put(key, result.getOrDefault(key, 0) + count);
        });

        return result.entrySet().stream().map(pairIntegerEntry -> {
                    var day = pairIntegerEntry.getKey().getFirst();
                    var hour = pairIntegerEntry.getKey().getSecond();
                    var count = pairIntegerEntry.getValue();
                    return new AggregatedTraffic(detectorId, day, hour, count);
                })
                .collect(Collectors.toSet());
    }
}
