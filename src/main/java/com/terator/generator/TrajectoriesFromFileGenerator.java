package com.terator.generator;

import com.terator.model.SingleTrajectory;
import com.terator.parser.ToAtlasParser;
import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class TrajectoriesFromFileGenerator {

    private final ToAtlasParser toAtlasParser;
    private List<AtlasEntity> entities = new LinkedList<>();
    private Atlas atlas;

    public TrajectoriesFromFileGenerator(ToAtlasParser toAtlasParser) {
        this.toAtlasParser = toAtlasParser;
    }

    List<SingleTrajectory> createTrajectoriesForFile(String fileName) throws IOException {
        if (entities.isEmpty()) {
            atlas = toAtlasParser.parse(fileName);
            entities = StreamSupport
                    .stream(atlas.entities().spliterator(), false).toList();
        }

        return new ToChurchTrajectoriesGeneratorWithStrategy().createTrajectories(entities, atlas);
    }

}
