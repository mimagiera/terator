package com.terator.service.routesCreator;

import com.terator.model.City;
import com.terator.model.SingleTrajectory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.geography.atlas.items.Route;
import org.openstreetmap.atlas.geography.atlas.routing.AStarRouter;
import org.openstreetmap.atlas.utilities.scalars.Distance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
public class RoutesFromTrajectoriesCreator implements Callable<List<Pair<Route, LocalTime>>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoutesFromTrajectoriesCreator.class);
    private static final Distance THRESHOLD = Distance.meters(52);
    private static final int LOGGER_STEP = 5000;

    private final City city;
    private final List<SingleTrajectory> singleTrajectories;

    @Override
    public List<Pair<Route, LocalTime>> call() throws Exception {
        AtomicInteger index = new AtomicInteger();
        LOGGER.info("start");
        AtomicLong start = new AtomicLong(System.currentTimeMillis());
        return singleTrajectories
                .stream()
                .map(singleTrajectory -> {
                            final Pair<Optional<Route>, LocalTime> routeWithStartTime = Pair.of(
                                    createRoute(singleTrajectory, city.atlas()),
                                    singleTrajectory.startTime());
                            var i = index.incrementAndGet();
                            if (i % LOGGER_STEP == 0) {
                                long end = System.currentTimeMillis();
                                LOGGER.info(
                                        "Processed {} routes. Last {} in {} seconds",
                                        i, LOGGER_STEP, (end - start.get()) / 1000f);
                                start.set(end);
                            }
                            return routeWithStartTime;
                        }
                )
                .filter(a -> a.getLeft().isPresent())
                .map(a -> Pair.of(a.getLeft().get(), a.getRight()))
                .toList();
    }

    private Optional<Route> createRoute(SingleTrajectory singleTrajectory, Atlas atlas) {
        var startLocation = singleTrajectory.startLocation();
        var endLocation = singleTrajectory.endLocation();

        final Route route =
                AStarRouter.fastComputationAndSubOptimalRoute(atlas, THRESHOLD)
                        .route(startLocation, endLocation);

        return Optional.ofNullable(route);
    }
}
