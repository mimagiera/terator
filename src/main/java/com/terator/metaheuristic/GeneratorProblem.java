package com.terator.metaheuristic;

import com.terator.model.City;
import com.terator.model.LocationWithMetaSpecificParameter;
import com.terator.model.accuracyChecker.AccuracyInSegment;
import com.terator.model.generatorTable.Probabilities;
import com.terator.model.inductionLoops.AggregatedTrafficBySegment;
import com.terator.service.accuracyChecker.AccuracyChecker;
import com.terator.service.generatorCreator.building.BuildingType;
import com.terator.service.inductionLoopsWithOsm.FixturesLocationMatcher;
import com.terator.service.simulationExecutor.SimulationExecutor;
import com.terator.service.trajectoryListCreator.TrajectoryListCreator;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static com.terator.metaheuristic.ProbabilitiesToVariables.BUILDING_TYPES_WITH_ORDER;
import static com.terator.metaheuristic.ProbabilitiesToVariables.FROM_ONE_TYPE_NUMBER_OF_VARIABLES;
import static com.terator.metaheuristic.ProbabilitiesToVariables.PROBABILITIES_IN_TIME_SIZE;
import static com.terator.service.TeratorExecutor.printElapsedTime;

public class GeneratorProblem extends AbstractDoubleProblem {
    private static final int NUMBER_OF_VARIABLES = BUILDING_TYPES_WITH_ORDER.size() * FROM_ONE_TYPE_NUMBER_OF_VARIABLES;

    private final TrajectoryListCreator trajectoryListCreator;
    private final FixturesLocationMatcher fixturesLocationMatcher;
    private final AccuracyChecker accuracyChecker;
    private final City city;
    private final Map<BuildingType, List<? extends LocationWithMetaSpecificParameter>> allBuildingsByType;
    private final SimulationExecutor simulationExecutor;
    private final Map<Integer, Set<AggregatedTrafficBySegment>> aggregatedTrafficBySegments;

    public GeneratorProblem(
            TrajectoryListCreator trajectoryListCreator,
            FixturesLocationMatcher fixturesLocationMatcher,
            AccuracyChecker accuracyChecker,
            SimulationExecutor simulationExecutor,
            City city,
            Map<BuildingType, List<? extends LocationWithMetaSpecificParameter>> allBuildingsByType,
            Map<Integer, Set<AggregatedTrafficBySegment>> aggregatedTrafficBySegments
    ) {
        this.trajectoryListCreator = trajectoryListCreator;
        this.fixturesLocationMatcher = fixturesLocationMatcher;
        this.accuracyChecker = accuracyChecker;
        this.city = city;
        this.allBuildingsByType = allBuildingsByType;
        this.simulationExecutor = simulationExecutor;
        this.aggregatedTrafficBySegments = aggregatedTrafficBySegments;
        setNumberOfVariables(NUMBER_OF_VARIABLES);
        setNumberOfObjectives(1);

        setName("Generator");

        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

        for (int i = 0; i < getNumberOfVariables(); i++) {
            lowerLimit.add(0d);
            upperLimit.add(1d);
        }

        // set distances limits
        IntStream.range(0, BUILDING_TYPES_WITH_ORDER.size())
                .forEach(buildingTypeNumber -> {
                    var firstIndexWithDistance =
                            buildingTypeNumber * FROM_ONE_TYPE_NUMBER_OF_VARIABLES + PROBABILITIES_IN_TIME_SIZE;
                    IntStream.range(firstIndexWithDistance, firstIndexWithDistance + BUILDING_TYPES_WITH_ORDER.size())
                            .forEach(indexWithDistance -> {
                                lowerLimit.set(indexWithDistance, 750d);
                                upperLimit.set(indexWithDistance, 3000d);
                            });
                });

        // set number of draws limits
        IntStream.range(0, BUILDING_TYPES_WITH_ORDER.size())
                .forEach(buildingTypeNumber -> IntStream.range(0, 24)
                        .forEach(hour -> {
                            var indexOfNumberOfDraws =
                                    buildingTypeNumber * FROM_ONE_TYPE_NUMBER_OF_VARIABLES +
                                            hour * (BUILDING_TYPES_WITH_ORDER.size() + 1) +
                                            BUILDING_TYPES_WITH_ORDER.size();
                            lowerLimit.set(indexOfNumberOfDraws, 0d);
                            upperLimit.set(indexOfNumberOfDraws, 5d);
                        }));

        setVariableBounds(lowerLimit, upperLimit);
    }

    @Override
    public DoubleSolution evaluate(DoubleSolution solution) {
        Probabilities probabilities = ProbabilitiesToVariables.getProbabilities(solution.variables());

        var endFindingBuildingsWithTypes = System.currentTimeMillis();
        // generate trajectories based on generator
        var trajectories = trajectoryListCreator.createTrajectories(probabilities, city, allBuildingsByType);
        long endTrajectories = System.currentTimeMillis();
        printElapsedTime(endFindingBuildingsWithTypes, endTrajectories, "trajectories");

        // simulate trajectories
        var simulationResult = simulationExecutor.executeSimulation(city, trajectories);
        long endSimulationResult = System.currentTimeMillis();
        printElapsedTime(endTrajectories, endSimulationResult, "simulationResult");

        // find accuracy
        var detectorLocationToSimulationSegment =
                fixturesLocationMatcher.findAllMatchingSegmentsToDetectors(
                        simulationResult.simulationState().state().keySet()
                );
        var accuracy = accuracyChecker.checkAccuracy(
                simulationResult, aggregatedTrafficBySegments, detectorLocationToSimulationSegment
        );
        long endAccuracy = System.currentTimeMillis();
        printElapsedTime(endSimulationResult, endAccuracy, "accuracy");

        solution.objectives()[0] =
                accuracy.accuracyInSegments().stream().mapToDouble(AccuracyInSegment::accuracy).average().getAsDouble();

        return solution;
    }
}
