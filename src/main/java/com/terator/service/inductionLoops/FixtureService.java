package com.terator.service.inductionLoops;

import com.terator.model.inductionLoops.Fixture;
import com.terator.repository.FixtureRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FixtureService {
    private final FixtureRepository fixtureRepository;

    public List<Fixture> findBySegmentId(Integer segmentId) {
        return IteratorUtils.toList(fixtureRepository.findBysegmentId(segmentId).iterator());
    }
}
