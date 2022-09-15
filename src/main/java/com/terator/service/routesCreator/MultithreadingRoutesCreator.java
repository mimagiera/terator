package com.terator.service.routesCreator;

import com.google.common.collect.Lists;
import com.terator.model.City;
import com.terator.model.SingleTrajectory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.openstreetmap.atlas.geography.atlas.items.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static com.terator.service.TeratorExecutorJMetal.printElapsedTime;

@RequiredArgsConstructor
@Service
public class MultithreadingRoutesCreator implements RoutesCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultithreadingRoutesCreator.class);

    @Override
    public List<Pair<Route, LocalTime>> createRoutesWithStartTimeInThreads(
            City city,
            List<SingleTrajectory> singleTrajectories,
            int threadsNumber
    ) {
        LOGGER.info("Starting creating routes, threads number: {}", threadsNumber);
        long start = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(threadsNumber);
        var lists = Lists.partition(singleTrajectories, singleTrajectories.size() / threadsNumber);
        var callables = IntStream.range(0, threadsNumber)
                .mapToObj(i -> new RoutesFromTrajectoriesCreator(city, lists.get(i)))
                .toList();
        List<Pair<Route, LocalTime>> routesWithStartTime = new LinkedList<>();
        try {
            var results = executor.invokeAll(callables);
            routesWithStartTime = results.stream().map(resultFromThread -> {
                        try {
                            return resultFromThread.get();
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .flatMap(Collection::stream)
                    .toList();
        } catch (InterruptedException e) {
            LOGGER.error("thread responsible for calculations interrupted but it may be ok");
        } finally {
            executor.shutdown();
        }
        long end = System.currentTimeMillis();
        printElapsedTime(start, end, "creating routes, created routes: " + routesWithStartTime.size() + ",", LOGGER);
        return routesWithStartTime;
    }
}
