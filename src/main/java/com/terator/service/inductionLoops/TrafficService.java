package com.terator.service.inductionLoops;

import com.terator.model.inductionLoops.Traffic;
import com.terator.repository.TrafficRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrafficService {
    private final TrafficRepository trafficRepository;

    public Iterable<Traffic> findByDetectorId(Integer detectoriId) {
        return trafficRepository.findBydetectorId(detectoriId);
    }
}
