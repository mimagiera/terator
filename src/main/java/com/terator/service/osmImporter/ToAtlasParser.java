package com.terator.service.osmImporter;

import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.geography.atlas.pbf.AtlasLoadingOption;
import org.openstreetmap.atlas.geography.atlas.raw.creation.RawAtlasGenerator;
import org.openstreetmap.atlas.geography.atlas.raw.sectioning.AtlasSectionProcessor;
import org.openstreetmap.atlas.streaming.resource.File;
import org.springframework.stereotype.Service;

import java.nio.file.FileSystem;

@Service
public class ToAtlasParser {
    public Atlas parse(String fileName) {
        final File atlasFile = new File(fileName);
        final Atlas rawAtlas = new RawAtlasGenerator(atlasFile).build();
        return new AtlasSectionProcessor(rawAtlas, AtlasLoadingOption.createOptionWithNoSlicing())
                .run();
    }
}
