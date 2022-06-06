package com.terator.service.trajectoryListCreator;

import com.terator.model.City;
import com.terator.model.Trajectories;
import com.terator.model.generatorTable.Probabilities;
import com.terator.service.generatorCreator.building.BuildingType;
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;

import java.util.List;
import java.util.Map;

public interface TrajectoryListCreator {
    Trajectories createTrajectories(
            Probabilities probabilities, City city, Map<BuildingType, List<AtlasEntity>> allBuildingsByType
    );
}
