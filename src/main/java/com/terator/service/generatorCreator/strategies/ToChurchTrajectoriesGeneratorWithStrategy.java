package com.terator.service.generatorCreator.strategies;

import com.terator.model.Area;
import com.terator.model.Location;
import com.terator.model.generatorTable.DestinationsWithProbabilityAndNumberOfDraws;
import com.terator.model.generatorTable.Probabilities;
import com.terator.model.generatorTable.ProbabilityToArea;
import com.terator.model.generatorTable.SourceAndDestinations;
import com.terator.service.generatorCreator.DataExtractor;
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;
import org.openstreetmap.atlas.geography.atlas.packed.PackedArea;
import org.openstreetmap.atlas.geography.atlas.packed.PackedRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ToChurchTrajectoriesGeneratorWithStrategy implements TrajectoriesGeneratorWithStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToChurchTrajectoriesGeneratorWithStrategy.class);

    @Override
    public Probabilities createProbabilities(List<AtlasEntity> entities) {
        var houses = DataExtractor.extractLivingPlaces(entities);
        var churches = DataExtractor.extractReligiousPlaces(entities);

        Set<SourceAndDestinations> sourceAndDestinations = houses.stream()
                .map(house -> {
                    Set<ProbabilityToArea> fromHouseToChurches = churches.stream()
                            .map(ch -> new ProbabilityToArea(entityToArea(ch), 0.2))
                            .collect(Collectors.toSet());
                    return new SourceAndDestinations(
                            entityToArea(house),
                            new DestinationsWithProbabilityAndNumberOfDraws(fromHouseToChurches, 1L)
                    );
                })
                .collect(Collectors.toSet());

        return new Probabilities(Map.of(Instant.now(), sourceAndDestinations));
    }

    Area entityToArea(AtlasEntity entity) {
        var l = locationOfAtlas(entity)
                .map(ToChurchTrajectoriesGeneratorWithStrategy::getLocation)
                .get();
        // todo
        return new Area(l, l, l, l);
    }

    private static Optional<org.openstreetmap.atlas.geography.Location> locationOfAtlas(AtlasEntity atlasEntity) {
        if (atlasEntity instanceof PackedArea packedArea) {
            var firstLocation = packedArea.asPolygon().get(0);
            return Optional.of(firstLocation);
        } else if (atlasEntity instanceof PackedRelation packedRelation) {
            var firstLocation = packedRelation.members().get(0).bounds().get(0);
            return Optional.of(firstLocation);
        } else {
            LOGGER.error("Cannot find location of object with type {}", atlasEntity.getClass());
            return Optional.empty();
        }
    }

    private static Location getLocation(org.openstreetmap.atlas.geography.Location atlasLocation) {
        return new Location(atlasLocation.getLongitude().toString(), atlasLocation.getLatitude().toString());
    }

}
