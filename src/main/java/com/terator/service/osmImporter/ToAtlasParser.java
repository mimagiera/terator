package com.terator.service.osmImporter;

import lombok.SneakyThrows;
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

import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;


@Service
public class ToAtlasParser {
    private static final String ATLAS_EDGE_FILTER_CARS = "atlas-edge-cars";

    public Atlas parse(String fileName) {
        final File atlasFile = getAtlasFile(fileName);

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

    @SneakyThrows
    private File getAtlasFile(String fileName) {
        var uri = getClass().getClassLoader().getResource(fileName).toURI();
        if ("jar".equals(uri.getScheme())) {
            for (FileSystemProvider provider : FileSystemProvider.installedProviders()) {
                if (provider.getScheme().equalsIgnoreCase("jar")) {
                    try {
                        provider.getFileSystem(uri);
                    } catch (FileSystemNotFoundException e) {
                        // in this case we need to initialize it first:
                        provider.newFileSystem(uri, Collections.emptyMap());
                    }
                }
            }
        }
        return new File(Paths.get(uri).toAbsolutePath());
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

