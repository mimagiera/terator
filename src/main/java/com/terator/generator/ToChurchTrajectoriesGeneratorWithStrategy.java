package com.terator.generator;

import com.terator.model.Location;
import com.terator.model.SingleTrajectory;
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;
import org.openstreetmap.atlas.geography.atlas.packed.PackedArea;
import org.openstreetmap.atlas.geography.atlas.packed.PackedRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ToChurchTrajectoriesGeneratorWithStrategy implements TrajectoriesGeneratorWithStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToChurchTrajectoriesGeneratorWithStrategy.class);

    @Override
    public List<SingleTrajectory> createTrajectories(List<AtlasEntity> entities) {
        return generateRoutesFromHousesToChurches(entities, Instant.now());
    }

    private List<SingleTrajectory> generateRoutesFromHousesToChurches(List<AtlasEntity> entities, Instant instant) {
        var percentOfHousesGoingToChurch = 1;

        var churches = DataExtractor.extractReligiousPlaces(entities);
        var houses = DataExtractor.extractLivingPlaces(entities);

        Collections.shuffle(houses);

        final int numberOfHousesThatGoToChurch = (int) (houses.size() * percentOfHousesGoingToChurch);
        var randomHouses = houses.subList(0, numberOfHousesThatGoToChurch);

        var numberOfHousesPerChurch = randomHouses.size() / churches.size();

        List<SingleTrajectory> result = new LinkedList<>();

        for (int churchNumber = 0; churchNumber < churches.size(); churchNumber++) {
            var housesToGoToThisChurch = randomHouses.subList(
                    churchNumber * numberOfHousesPerChurch,
                    (churchNumber + 1) * numberOfHousesPerChurch
            );

            var a = fromHousesToChurch(housesToGoToThisChurch, churches.get(churchNumber), instant);
            result.addAll(a);
        }

        return result;
    }

    private List<SingleTrajectory> fromHousesToChurch(List<AtlasEntity> houses, AtlasEntity church, Instant instant) {
        var churchLocation = locationOfAtlasEntity(church);

        return churchLocation.map(
                        chLocation -> houses.stream()
                                .map(this::locationOfAtlasEntity)
                                .flatMap(Optional::stream)
                                .map(houseLocation -> new SingleTrajectory(houseLocation, chLocation, instant))
                                .collect(Collectors.toList())
                )
                .orElseGet(List::of);
    }

    private Optional<Location> locationOfAtlasEntity(AtlasEntity atlasEntity) {
        if (atlasEntity instanceof PackedArea packedArea) {
            var firstLocation = packedArea.asPolygon().get(0);
            return getLocation(firstLocation);
        } else if (atlasEntity instanceof PackedRelation packedRelation) {
            var firstLocation = packedRelation.members().get(0).bounds().get(0);
            return getLocation(firstLocation);
        } else {
            LOGGER.error("Cannot finn location of object with type {}", atlasEntity.getClass());
            return Optional.empty();
        }
    }

    private Optional<Location> getLocation(org.openstreetmap.atlas.geography.Location atlasLocation) {
        return Optional.of(
                new Location(
                        atlasLocation.getLongitude().toString(),
                        atlasLocation.getLatitude().toString()
                )
        );
    }

}
