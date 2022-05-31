package com.terator.service.inductionLoops;

import com.terator.model.Location;
import com.terator.model.inductionLoops.DetectorLocation;
import com.terator.model.inductionLoops.DetectorsWithSegmentId;
import com.terator.model.inductionLoops.Fixture;
import com.terator.model.inductionLoops.InfluenceDetectorSegment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InductionLoopsDataExtractor {
    private final InfluenceDetectorSegmentService influenceDetectorSegmentService;
    private final FixtureService fixtureService;

    public Set<DetectorsWithSegmentId> getDetectorsWithSegmentId() {
        var allDetectorSegments = influenceDetectorSegmentService.findAll();

        var segmentsWithDetectors = allDetectorSegments.stream()
                .collect(Collectors.groupingBy(
                                InfluenceDetectorSegment::getSegmentId,
                                Collectors.mapping(InfluenceDetectorSegment::getDetectorId, Collectors.toSet())
                        )
                );

        return segmentsWithDetectors.entrySet().stream().map(integerSetEntry -> {
                    var segmentId = integerSetEntry.getKey();
                    var detectorsIds = integerSetEntry.getValue();
                    return new DetectorsWithSegmentId(detectorsIds, segmentId);
                })
                .collect(Collectors.toSet());
    }

    public Set<DetectorLocation> extractData() {
        return getDetectorsWithSegmentId().stream()
                .map(detectorsWithSegmentId -> {
                    var segmentId = detectorsWithSegmentId.segmentId();

                    var fixturesInSegment = fixtureService.findBySegmentId(segmentId);
                    var locations = extractLocations(fixturesInSegment);
                    return new DetectorLocation(detectorsWithSegmentId.detectorIds(), segmentId, locations);
                })
                .collect(Collectors.toSet());
    }

    private Set<Location> extractLocations(List<Fixture> fixturesInSegment) {
        return fixturesInSegment.stream()
                .map(fixture -> new Location(fixture.getLon().doubleValue(), fixture.getLat().doubleValue()))
                .collect(Collectors.toSet());
    }

}
