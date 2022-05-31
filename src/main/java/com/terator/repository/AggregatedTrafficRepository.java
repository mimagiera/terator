package com.terator.repository;

import com.terator.model.inductionLoops.AggregatedTraffic;
import com.terator.model.inductionLoops.AggregatedTrafficId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AggregatedTrafficRepository extends CrudRepository<AggregatedTraffic, AggregatedTrafficId> {
    Iterable<AggregatedTraffic> findAggregatedTrafficsBydetectorId(Integer detectoriId);
}
