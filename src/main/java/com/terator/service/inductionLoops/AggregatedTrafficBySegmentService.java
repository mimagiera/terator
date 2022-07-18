package com.terator.service.inductionLoops;

import com.terator.model.inductionLoops.AggregatedTrafficBySegment;
import com.terator.repository.AggregatedTrafficBySegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AggregatedTrafficBySegmentService {
    private final AggregatedTrafficBySegmentRepository aggregatedTrafficBySegmentRepository;
    private final ReadAggregatedTrafficBySegmentFromCsvService readAggregatedTrafficBySegmentFromCsvService;

    public void saveAll(Iterable<AggregatedTrafficBySegment> aggregatedTrafficBySegments) {
        aggregatedTrafficBySegmentRepository.saveAll(aggregatedTrafficBySegments);
    }

    public Iterable<AggregatedTrafficBySegment> getAll() {
//        return readAggregatedTrafficBySegmentFromCsvService.getAllFromCsv();
        return readAggregatedTrafficBySegmentFromCsvService.getAllFromCsv();
    }

    public Iterable<AggregatedTrafficBySegment> getBySegmentId(Integer segmentId) {
        return aggregatedTrafficBySegmentRepository.findAggregatedTrafficsBysegmentId(segmentId);
    }
}
