package com.terator.service.inductionLoops.csv;

import com.terator.model.inductionLoops.Fixture;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FixtureFromCsvService extends ExtractFromCsv<Fixture> {

    private final List<Fixture> allFromCsv;

    public FixtureFromCsvService() {
        super("fixtures.csv");
        allFromCsv = super.findAll();
    }

    public List<Fixture> findBySegmentId(Integer segmentId) {
        return allFromCsv.stream()
                .filter(fixture -> segmentId.equals(fixture.getSegmentId()))
                .toList();
    }
}
