package com.terator.service.trajectoryListCreator;

import com.terator.model.Location;
import com.terator.service.generatorCreator.building.BuildingType;
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DestinationFinder {
    Optional<Location> findDestination(AtlasEntity entity, BuildingType destinationType, Double perfectDistanceToType) {
        // todo
        return Optional.empty();
    }
}
