package com.terator.service.generatorCreator.strategies;

import com.terator.model.SingleTrajectory;
import com.terator.model.generatorTable.Probabilities;
import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;

import java.io.IOException;
import java.util.List;

public interface TrajectoriesGeneratorWithStrategy {
    Probabilities createProbabilities(List<AtlasEntity> entities);
}
