package com.terator.generator;

import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class DataExtractor {

    private static final String BUILDING = "building";
    /**
     * more at https://wiki.openstreetmap.org/wiki/Pl:Key:building
     */
    private static final Set<String> LIVING_BUILDING_TYPES = Set.of("apartments", "house");
    private static final Set<String> RELIGIOUS_BUILDING_TYPES = Set.of("cathedral", "chapel", "church");


    static List<AtlasEntity> extractLivingPlaces(List<AtlasEntity> entities) {
        return extractBuildings(entities, entity -> {
            var buildingValue = entity.getOsmTags().get(BUILDING);
            return buildingValue != null && LIVING_BUILDING_TYPES.contains(buildingValue);
        });
    }

    static List<AtlasEntity> extractReligiousPlaces(List<AtlasEntity> entities) {
        return extractBuildings(entities, entity -> {
            var buildingValue = entity.getOsmTags().get(BUILDING);
            return buildingValue != null && RELIGIOUS_BUILDING_TYPES.contains(buildingValue);
        });
    }

    private static List<AtlasEntity> extractBuildings(List<AtlasEntity> entities,
                                                      Predicate<AtlasEntity> additionalPredicate
    ) {
        return entities.stream()
                .filter(entity -> entity.getOsmTags().containsKey(BUILDING))
                .filter(additionalPredicate)
                .collect(Collectors.toList());
    }

}
