package com.terator.service.generatorCreator;

import com.terator.model.Location;
import com.terator.model.SingleTrajectory;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;
import org.openstreetmap.atlas.geography.atlas.packed.PackedArea;
import org.openstreetmap.atlas.geography.atlas.packed.PackedRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ToChurchTrajectoriesGeneratorWithStrategy implements TrajectoriesGeneratorWithStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToChurchTrajectoriesGeneratorWithStrategy.class);

    @Override
    public List<SingleTrajectory> createTrajectories(List<AtlasEntity> entities, Atlas atlas) throws IOException {
        return generateRoutesFromHousesToChurches(entities, Instant.now(), atlas);
    }

    private List<SingleTrajectory> generateRoutesFromHousesToChurches(List<AtlasEntity> entities, Instant instant,
                                                                      Atlas atlas
    ) throws IOException {
        var percentOfHousesGoingToChurch = 0.4;

        var churches = DataExtractor.extractReligiousPlaces(entities);
        var houses = DataExtractor.extractLivingPlaces(entities);

        Collections.shuffle(houses);

        final int numberOfHousesThatGoToChurch = (int) (houses.size() * percentOfHousesGoingToChurch);
        var randomHouses = houses.subList(0, numberOfHousesThatGoToChurch);

        var numberOfHousesPerChurch = randomHouses.size() / churches.size();

        List<ImmutablePair<org.openstreetmap.atlas.geography.Location, org.openstreetmap.atlas.geography.Location>>
                result = new LinkedList<>();

        for (int churchNumber = 0; churchNumber < churches.size(); churchNumber++) {
            var housesToGoToThisChurch = randomHouses.subList(churchNumber * numberOfHousesPerChurch,
                    (churchNumber + 1) * numberOfHousesPerChurch);

            var fromHousesToThisChurch =
                    createPairsOfAtlasLocation(housesToGoToThisChurch, churches.get(churchNumber));
            result.addAll(fromHousesToThisChurch);
        }

        if (false) {
            // generate whole routes
            ToSmartsFileSaver.saveToXml(atlas, result);
        }

        return result.stream()
                .map(pair -> {
                    var houseLocation = pair.getLeft();
                    var churchLocation = pair.getRight();
                    return new SingleTrajectory(instant, getLocation(houseLocation), getLocation(churchLocation));
                })
                .collect(Collectors.toList());
    }

    private List<ImmutablePair<org.openstreetmap.atlas.geography.Location, org.openstreetmap.atlas.geography.Location>> createPairsOfAtlasLocation(
            List<AtlasEntity> houses,
            AtlasEntity church
    ) {
        var churchLocation = locationOfAtlas(church);
        return churchLocation.map(location ->
                        houses.stream().map(this::locationOfAtlas)
                                .flatMap(Optional::stream)
                                .map(houseLocation -> new ImmutablePair<>(houseLocation, location))
                                .collect(Collectors.toList())
                )
                .orElseGet(List::of);
    }

    private Optional<org.openstreetmap.atlas.geography.Location> locationOfAtlas(AtlasEntity atlasEntity) {
        if (atlasEntity instanceof PackedArea packedArea) {
            var firstLocation = packedArea.asPolygon().get(0);
            return Optional.of(firstLocation);
        } else if (atlasEntity instanceof PackedRelation packedRelation) {
            var firstLocation = packedRelation.members().get(0).bounds().get(0);
            return Optional.of(firstLocation);
        } else {
            LOGGER.error("Cannot finn location of object with type {}", atlasEntity.getClass());
            return Optional.empty();
        }
    }

    private Location getLocation(org.openstreetmap.atlas.geography.Location atlasLocation) {
        return new Location(atlasLocation.getLongitude().toString(), atlasLocation.getLatitude().toString());
    }

}
