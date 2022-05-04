package com.terator.service.osmImporter;

import com.terator.model.City;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AtlasOsmImporter implements OsmImporter {
    private final ToAtlasParser toAtlasParser;

    @Override
    public City importData(String fileName) {
        var atlas = toAtlasParser.parse(fileName);
        var entities = StreamSupport
                .stream(atlas.entities().spliterator(), false).toList();

        return null;
    }
}
