package com.terator.repository;

import com.terator.model.inductionLoops.InfluenceDetectorSegment;
import com.terator.model.inductionLoops.InfluenceDetectorSegmentId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluenceDetectorSegmentRepository
        extends CrudRepository<InfluenceDetectorSegment, InfluenceDetectorSegmentId> {
}
