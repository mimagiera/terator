package com.terator.service.routesCreator;

import com.terator.model.City;
import com.terator.model.SingleTrajectory;
import org.apache.commons.lang3.tuple.Pair;
import org.openstreetmap.atlas.geography.atlas.items.Route;

import java.time.LocalTime;
import java.util.List;

public interface RoutesCreator {
    List<Pair<Route, LocalTime>> createRoutesWithStartTimeInThreads(
            City city,
            List<SingleTrajectory> singleTrajectories, int threadsNumber
    );
}
