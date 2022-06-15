package com.terator.service.trajectoryListCreator;

import com.terator.model.City;
import com.terator.model.LocationWithMetaSpecificParameter;
import com.terator.model.SingleTrajectory;
import com.terator.model.Trajectories;
import com.terator.model.generatorTable.PerfectDistancesFromBuilding;
import com.terator.model.generatorTable.Probabilities;
import com.terator.model.generatorTable.ProbabilitiesAndNumberOfDrawsFromBuilding;
import com.terator.model.generatorTable.ProbabilitiesAndNumberOfDrawsFromBuildingInSpecificTime;
import com.terator.service.generatorCreator.building.BuildingType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static com.terator.service.TeratorExecutor.MINUTES_INTERVAL_GENERATOR;

@Service
@RequiredArgsConstructor
public class SimpleTrajectoryListCreator implements TrajectoryListCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTrajectoryListCreator.class);

    private final DestinationFinder destinationFinder;
    private final Random random = new Random();

    @Override
    public Trajectories createTrajectories(
            Probabilities probabilities,
            City city,
            Map<BuildingType, List<? extends LocationWithMetaSpecificParameter>> allBuildingsByType
    ) {
        LOGGER.info("Starting creating trajectories");
        var trajectories = Arrays.stream(BuildingType.values())
                .map(buildingType -> findTrajectoriesFromBuildingType(
                        probabilities, buildingType, allBuildingsByType)
                )
                .flatMap(List::stream)
                .collect(Collectors.toList());
        LOGGER.debug("Number of all trajectories from: {}", trajectories.size());

        return new Trajectories(trajectories);
    }

    private List<SingleTrajectory> findTrajectoriesFromBuildingType(
            Probabilities probabilities,
            BuildingType startingBuildingType,
            Map<BuildingType, List<? extends LocationWithMetaSpecificParameter>> allBuildingsByType
    ) {
        return Optional.ofNullable(probabilities.buildingTypeFromBuildingTypeGeneratorMap().get(startingBuildingType))
                .map(fromBuildingTypeGenerator -> {
                    var probabilitiesAndNumberOfDrawsFromBuilding =
                            fromBuildingTypeGenerator.probabilitiesAndNumberOfDrawsFromBuilding();
                    var perfectDistancesFromBuilding = fromBuildingTypeGenerator.perfectDistancesFromBuilding();

                    var startingBuildings = allBuildingsByType.get(startingBuildingType);

                    LOGGER.debug("Number of buildings with type: {}, {}", startingBuildingType,
                            startingBuildings.size());

                    final List<SingleTrajectory> singleTrajectories = startingBuildings.stream()
//                            .limit(3)
                            .map(locationWithMetaSpecificParameter ->
                                    {
                                        var destinationTypesWithStartingTime =
                                                findDestinationTypesWithStartingTime(
                                                        probabilitiesAndNumberOfDrawsFromBuilding,
                                                        locationWithMetaSpecificParameter
                                                );
                                        return createFromSpecificBuilding(
                                                locationWithMetaSpecificParameter,
                                                destinationTypesWithStartingTime,
                                                perfectDistancesFromBuilding,
                                                allBuildingsByType);
                                    }
                            )
                            .flatMap(List::stream)
                            .collect(Collectors.toList());

                    LOGGER.debug("Number of trajectories from type: {}, {}", startingBuildingType,
                            singleTrajectories.size());

                    return singleTrajectories;
                })
                .orElseGet(() -> {
                    LOGGER.warn("Cannot find probabilities for type {}", startingBuildingType);
                    return List.of();
                });
    }

    private List<SingleTrajectory> createFromSpecificBuilding(
            LocationWithMetaSpecificParameter locationWithMetaSpecificParameter,
            List<Pair<LocalTime, BuildingType>> destinations,
            PerfectDistancesFromBuilding perfectDistances,
            Map<BuildingType, List<? extends LocationWithMetaSpecificParameter>> allBuildingsByType
    ) {
        var startingPointLocation = locationWithMetaSpecificParameter.getLocation();
        return destinations.stream()
                .map(localTimeBuildingTypePair -> {
                    var startTime = localTimeBuildingTypePair.getKey();
                    var destinationType = localTimeBuildingTypePair.getValue();
                    double perfectDistance = getPerfectDistance(perfectDistances, destinationType);

                    return destinationFinder
                            .findDestination(startingPointLocation, destinationType, perfectDistance,
                                    allBuildingsByType)
                            .map(destinationLocation ->
                                    new SingleTrajectory(startTime, startingPointLocation,
                                            destinationLocation)
                            );
                })
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    private int getPerfectDistance(PerfectDistancesFromBuilding perfectDistances, BuildingType destinationType) {
        var expectedPerfectDistanceToType =
                perfectDistances.expectedDistancesToBuildingTypes().get(destinationType);
        return (int) random.nextGaussian(expectedPerfectDistanceToType, 100);
    }

    private List<Pair<LocalTime, BuildingType>> findDestinationTypesWithStartingTime(
            ProbabilitiesAndNumberOfDrawsFromBuilding fromBuildingTypeGenerator,
            LocationWithMetaSpecificParameter locationWithMetaSpecificParameter
    ) {
        var buildingSurfaceArea = locationWithMetaSpecificParameter.getMetaSpecificValue();
        return fromBuildingTypeGenerator
                .probabilitiesInTime()
                .entrySet()
                .stream()
                .flatMap(timeWithDestinationProbabilities -> {
                    var startTime = getRandomStartTime(timeWithDestinationProbabilities);
                    var destinationsInTime =
                            calculateDestinationTypes(timeWithDestinationProbabilities.getValue(), buildingSurfaceArea);
                    return destinationsInTime.stream().map(destinationType -> new Pair<>(startTime, destinationType));
                })
                .collect(Collectors.toList());
    }

    private LocalTime getRandomStartTime(
            Map.Entry<LocalTime, ProbabilitiesAndNumberOfDrawsFromBuildingInSpecificTime> timeWithDestinationProbabilities
    ) {
        var localTime = timeWithDestinationProbabilities.getKey();
        int numberOfMinutesToAdd = random.nextInt(MINUTES_INTERVAL_GENERATOR);
        return localTime.plusMinutes(numberOfMinutesToAdd);
    }

    private List<BuildingType> calculateDestinationTypes(
            ProbabilitiesAndNumberOfDrawsFromBuildingInSpecificTime probabilitiesAndNumberOfDrawsFromBuildingInSpecificTime,
            double buildingSurfaceArea
    ) {
        var numberOfDraws =
                getNumberOfDraws(
                        probabilitiesAndNumberOfDrawsFromBuildingInSpecificTime.expectedNumberOfDraws(),
                        buildingSurfaceArea);
        if (numberOfDraws > 0) {
            var probabilityToType = probabilitiesAndNumberOfDrawsFromBuildingInSpecificTime.probabilityToType();
            List<Pair<BuildingType, Double>> itemWeights =
                    probabilityToType.entrySet().stream()
                            .map(buildingTypeDoubleEntry -> new Pair<>(
                                    buildingTypeDoubleEntry.getKey(),
                                    buildingTypeDoubleEntry.getValue())
                            )
                            .collect(Collectors.toList());

            return Arrays.stream(new EnumeratedDistribution<>(itemWeights).sample(numberOfDraws))
                    .map(buildingType -> (BuildingType) buildingType)
                    .toList();
        } else {
            return List.of();
        }
    }

    private int getNumberOfDraws(double expectedNumberOfDrawsFotBuildingType, double buildingSurfaceArea) {
        double areaConst = 1d / 3000d;

        var expectedNumberOfDrawsBasedOnArea = expectedNumberOfDrawsFotBuildingType * buildingSurfaceArea * areaConst;
        return (int) random.nextGaussian(expectedNumberOfDrawsBasedOnArea, 1.5);
    }
}
