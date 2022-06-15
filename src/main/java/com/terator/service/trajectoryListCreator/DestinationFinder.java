package com.terator.service.trajectoryListCreator;

import com.terator.model.LocationWithMetaSpecificParameter;
import com.terator.service.generatorCreator.building.BuildingType;
import org.openstreetmap.atlas.geography.Location;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DestinationFinder {
    Optional<Location> findDestination(Location startLocation, BuildingType destinationType,
                                       Double perfectDistanceToType,
                                       Map<BuildingType, List<? extends LocationWithMetaSpecificParameter>> allBuildingsByType
    ) {
        return allBuildingsByType
                .get(destinationType)
                .stream()
                .min(Comparator.comparingDouble(destination ->
                        Math.abs(distanceBetweenEntities(startLocation, destination.getLocation()) -
                                perfectDistanceToType)
                ))
                .map(LocationWithMetaSpecificParameter::getLocation);
    }

    private Double distanceBetweenEntities(Location startLocation, Location endLocation) {
        return startLocation.distanceTo(endLocation).asMeters();
    }
}
