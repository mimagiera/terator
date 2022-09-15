package com.terator.service.osmImporter;

import com.terator.model.City;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.stream.StreamSupport;

import static com.terator.service.TeratorExecutorJMetal.printElapsedTime;

@Service
@RequiredArgsConstructor
public class AtlasOsmImporter implements OsmImporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AtlasOsmImporter.class);

    private final ToAtlasParser toAtlasParser;

    @Override
    public City importData(String fileName) {
        LOGGER.info("Starting importing OSM data");
        long start = System.currentTimeMillis();

        var atlas = toAtlasParser.parse(fileName);
        var entities = StreamSupport
                .stream(atlas.entities().spliterator(), false).toList();

        long end = System.currentTimeMillis();
        printElapsedTime(start, end, "importing OSM data", LOGGER);
        return new City(entities, atlas);
    }
}
