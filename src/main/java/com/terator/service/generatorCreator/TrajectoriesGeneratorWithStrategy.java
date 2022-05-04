package com.terator.service.generatorCreator;

import com.terator.model.SingleTrajectory;
import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;

import java.io.IOException;
import java.util.List;

public interface TrajectoriesGeneratorWithStrategy {
    List<SingleTrajectory> createTrajectories(List<AtlasEntity> entities, Atlas atlas) throws IOException;
}
