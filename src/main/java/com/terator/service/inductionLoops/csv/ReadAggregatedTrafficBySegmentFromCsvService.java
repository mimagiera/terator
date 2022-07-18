package com.terator.service.inductionLoops.csv;

import com.terator.model.inductionLoops.AggregatedTrafficBySegment;
import org.springframework.stereotype.Service;

@Service
public class ReadAggregatedTrafficBySegmentFromCsvService extends ExtractFromCsv<AggregatedTrafficBySegment> {
    public ReadAggregatedTrafficBySegmentFromCsvService() {
        super("aggregated_traffic_by_segment.csv");
    }
}
