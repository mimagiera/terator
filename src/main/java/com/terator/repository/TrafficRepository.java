package com.terator.repository;

import com.terator.model.inductionLoops.Traffic;
import com.terator.model.inductionLoops.TrafficId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrafficRepository extends CrudRepository<Traffic, TrafficId> {
    Iterable<Traffic> findBydetectorId(Integer detectorId);
}
