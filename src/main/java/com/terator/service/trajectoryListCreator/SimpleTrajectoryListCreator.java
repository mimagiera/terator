package com.terator.service.trajectoryListCreator;

import com.terator.model.City;
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
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimpleTrajectoryListCreator implements TrajectoryListCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTrajectoryListCreator.class);

    private final DestinationFinder destinationFinder;

    @Override
    public Trajectories createTrajectories(Probabilities probabilities, City city) {
        var trajectories = Arrays.stream(BuildingType.values())
                .map(buildingType -> findTrajectoriesFromBuildingType(probabilities, city, buildingType))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        return new Trajectories(trajectories);
    }

    private List<SingleTrajectory> findTrajectoriesFromBuildingType(Probabilities probabilities, City city,
                                                                    BuildingType startingBuildingType
    ) {
        return Optional.ofNullable(probabilities.buildingTypeFromBuildingTypeGeneratorMap().get(startingBuildingType))
                .map(fromBuildingTypeGenerator -> {
                    var probabilitiesAndNumberOfDrawsFromBuilding =
                            fromBuildingTypeGenerator.probabilitiesAndNumberOfDrawsFromBuilding();
                    var perfectDistancesFromBuilding = fromBuildingTypeGenerator.perfectDistancesFromBuilding();

                    var startingBuildings = startingBuildingType.getEntitiesProvider().apply(city);

                    LOGGER.debug("Number of buildings with type: {}, {}", startingBuildingType,
                            startingBuildings.size());

                    final List<SingleTrajectory> singleTrajectories = startingBuildings.stream()
                            .map(startBuilding ->
                                    createFromSpecificBuilding(startBuilding, city,
                                            findDestinationTypesWithStartingTime(
                                                    probabilitiesAndNumberOfDrawsFromBuilding
                                            ),
                                            perfectDistancesFromBuilding)
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
            AtlasEntity entity,
            City city,
            List<Pair<LocalTime, BuildingType>> destinations,
            PerfectDistancesFromBuilding perfectDistances
    ) {

        return LocationExtractor.teratorLocation(entity)
                .map(startingPointLocation -> destinations.stream()
                        .map(localTimeBuildingTypePair -> {
                            var startTime = localTimeBuildingTypePair.getKey();
                            var destinationType = localTimeBuildingTypePair.getValue();
                            var perfectDistanceToType =
                                    perfectDistances.distancesToBuildingTypes().get(destinationType);

                            return destinationFinder
                                    .findDestination(entity, city, destinationType, perfectDistanceToType)
                                    .map(destinationLocation ->
                                            new SingleTrajectory(startTime, startingPointLocation, destinationLocation)
                                    );
                        })
                        .flatMap(Optional::stream)
                        .collect(Collectors.toList()))
                .orElseGet(() -> {
                    LOGGER.warn("Cannot find location of starting point");
                    return List.of();
                });
    }

    private List<Pair<LocalTime, BuildingType>> findDestinationTypesWithStartingTime(
            ProbabilitiesAndNumberOfDrawsFromBuilding fromBuildingTypeGenerator
    ) {
        return fromBuildingTypeGenerator
                .probabilitiesInTime()
                .entrySet()
                .stream()
                .flatMap(timeWithDestinationProbabilities -> {
                    var localTime = timeWithDestinationProbabilities.getKey();
                    var destinationsInTime = calculateDestinationTypes(timeWithDestinationProbabilities.getValue());
                    return destinationsInTime.stream().map(destinationType -> new Pair<>(localTime, destinationType));
                })
                .collect(Collectors.toList());
    }

    private List<BuildingType> calculateDestinationTypes(
            ProbabilitiesAndNumberOfDrawsFromBuildingInSpecificTime probabilitiesAndNumberOfDrawsFromBuildingInSpecificTime
    ) {
        var numberOfDraws = probabilitiesAndNumberOfDrawsFromBuildingInSpecificTime.numberOfDraws();
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
}
