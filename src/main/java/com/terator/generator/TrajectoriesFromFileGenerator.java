package com.terator.generator;

import com.terator.model.SingleTrajectory;
import com.terator.parser.ToAtlasParser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class TrajectoriesFromFileGenerator {

    private final ToAtlasParser toAtlasParser;

    public TrajectoriesFromFileGenerator(ToAtlasParser toAtlasParser) {
        this.toAtlasParser = toAtlasParser;
    }

    List<SingleTrajectory> createTrajectoriesForFile(String fileName) {
        var atlas = toAtlasParser.parse(fileName);
        var entities = StreamSupport
                .stream(atlas.entities().spliterator(), false).toList();

        return new ToChurchTrajectoriesGeneratorWithStrategy().createTrajectories(entities);
    }

}
