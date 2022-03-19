package com.terator.generator;

import com.terator.model.SingleTrajectory;
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;

import java.util.List;

public interface TrajectoriesGeneratorWithStrategy {
    List<SingleTrajectory> createTrajectories(List<AtlasEntity> entities);
}
