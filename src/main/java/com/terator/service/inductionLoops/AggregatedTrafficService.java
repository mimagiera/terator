package com.terator.service.inductionLoops;

import com.terator.model.inductionLoops.AggregatedTraffic;
import com.terator.repository.AggregatedTrafficRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AggregatedTrafficService {
    private final AggregatedTrafficRepository aggregatedTrafficRepository;

    public void saveAll(Iterable<AggregatedTraffic> aggregatedTraffics) {
        aggregatedTrafficRepository.saveAll(aggregatedTraffics);
    }

    public Iterable<AggregatedTraffic> getByDetectorId(Integer detectorId) {
        return aggregatedTrafficRepository.findAggregatedTrafficsBydetectorId(detectorId);
    }
}
