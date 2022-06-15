package com.terator.service.osmImporter;

import org.openstreetmap.atlas.geography.MultiPolygon;
import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.geography.atlas.pbf.AtlasLoadingOption;
import org.openstreetmap.atlas.geography.atlas.pbf.BridgeConfiguredFilter;
import org.openstreetmap.atlas.geography.atlas.raw.creation.RawAtlasGenerator;
import org.openstreetmap.atlas.geography.atlas.raw.sectioning.AtlasSectionProcessor;
import org.openstreetmap.atlas.streaming.resource.File;
import org.openstreetmap.atlas.streaming.resource.FileSuffix;
import org.openstreetmap.atlas.streaming.resource.InputStreamResource;
import org.openstreetmap.atlas.utilities.configuration.StandardConfiguration;
import org.springframework.stereotype.Service;


@Service
public class ToAtlasParser {
    private static final String ATLAS_EDGE_FILTER_CARS = "atlas-edge-cars";

    public Atlas parse(String fileName) {
        final File atlasFile = new File(fileName);

        final AtlasLoadingOption atlasLoadingOption =
                AtlasLoadingOption.createOptionWithOnlySectioning().setLoadAtlasRelation(false);
        atlasLoadingOption.setEdgeFilter(createEdgeFilter());

        final Atlas rawAtlas = new RawAtlasGenerator(
                atlasFile,
                atlasLoadingOption,
                MultiPolygon.MAXIMUM).build();
        return new AtlasSectionProcessor(rawAtlas, atlasLoadingOption)
                .run();
    }

    private BridgeConfiguredFilter createEdgeFilter() {
        return new BridgeConfiguredFilter("",
                AtlasLoadingOption.ATLAS_EDGE_FILTER_NAME,
                new StandardConfiguration(
                        new InputStreamResource(
                                () -> getClass().getClassLoader()
                                        .getResourceAsStream(ATLAS_EDGE_FILTER_CARS + FileSuffix.JSON)
                        )));
    }
}

