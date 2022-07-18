package com.terator.service.inductionLoops;

import com.terator.model.inductionLoops.AggregatedTrafficBySegment;
import com.terator.service.inductionLoops.csv.ReadAggregatedTrafficBySegmentFromCsvService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AggregatedTrafficBySegmentService {
    private final ReadAggregatedTrafficBySegmentFromCsvService readAggregatedTrafficBySegmentFromCsvService;

    public Iterable<AggregatedTrafficBySegment> getAll() {
        return readAggregatedTrafficBySegmentFromCsvService.findAll();
    }
}
