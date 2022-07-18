package com.terator.service.inductionLoops.csv;

import com.terator.model.inductionLoops.InfluenceDetectorSegment;
import org.springframework.stereotype.Service;

@Service
public class InfluenceDetectorSegmentFromCsvService extends ExtractFromCsv<InfluenceDetectorSegment> {
    protected InfluenceDetectorSegmentFromCsvService() {
        super("influence_detector_segment.csv");
    }
}
