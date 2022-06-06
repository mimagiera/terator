package com.terator.repository;

import com.terator.model.inductionLoops.AggregatedTrafficBySegment;
import com.terator.model.inductionLoops.AggregatedTrafficBySegmentId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AggregatedTrafficBySegmentRepository
        extends CrudRepository<AggregatedTrafficBySegment, AggregatedTrafficBySegmentId> {

    Iterable<AggregatedTrafficBySegment> findAggregatedTrafficsBysegmentId(Integer segmentId);

}
