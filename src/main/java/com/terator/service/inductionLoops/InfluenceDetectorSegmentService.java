package com.terator.service.inductionLoops;

import com.terator.model.inductionLoops.InfluenceDetectorSegment;
import com.terator.repository.InfluenceDetectorSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InfluenceDetectorSegmentService {
    private final InfluenceDetectorSegmentRepository influenceDetectorSegmentRepository;

    public List<InfluenceDetectorSegment> findAll() {
        return IteratorUtils.toList(influenceDetectorSegmentRepository.findAll().iterator());
    }
}
