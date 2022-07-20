package com.terator.metaheuristic;

import com.terator.model.City;
import com.terator.model.LocationWithMetaSpecificParameter;
import com.terator.model.inductionLoops.AggregatedTrafficBySegment;
import com.terator.service.accuracyChecker.AccuracyChecker;
import com.terator.service.generatorCreator.building.BuildingType;
import com.terator.service.inductionLoopsWithOsm.FixturesLocationMatcher;
import com.terator.service.simulationExecutor.SimulationExecutor;
import com.terator.service.trajectoryListCreator.TrajectoryListCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.differentialevolution.DifferentialEvolutionBuilder;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.selection.impl.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.MultiThreadedSolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FindBestGeneratorVariables {
    private static final int DEFAULT_NUMBER_OF_CORES = 1;

    private final TrajectoryListCreator trajectoryListCreator;
    private final FixturesLocationMatcher fixturesLocationMatcher;
    private final AccuracyChecker accuracyChecker;
    private final SimulationExecutor simulationExecutor;

    public void doEverything(
            City city,
            Map<BuildingType, List<? extends LocationWithMetaSpecificParameter>> allBuildingsByType,
            Map<Integer, Set<AggregatedTrafficBySegment>> aggregatedTrafficBySegments
    ) {
        DoubleProblem problem;
        Algorithm<DoubleSolution> algorithm;
        DifferentialEvolutionSelection selection;
        DifferentialEvolutionCrossover crossover;
        SolutionListEvaluator<DoubleSolution> evaluator;

        problem = new GeneratorProblem(trajectoryListCreator, fixturesLocationMatcher, accuracyChecker,
                simulationExecutor, city,
                allBuildingsByType, aggregatedTrafficBySegments);

        var args = new String[]{};
        int numberOfCores;
        if (args.length == 1) {
            numberOfCores = Integer.parseInt(args[0]);
        } else {
            numberOfCores = DEFAULT_NUMBER_OF_CORES;
        }

        if (numberOfCores == 1) {
            evaluator = new SequentialSolutionListEvaluator<>();
        } else {
            evaluator = new MultiThreadedSolutionListEvaluator<>(numberOfCores);
        }

        crossover =
                new DifferentialEvolutionCrossover(
                        0.5, 0.5, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);
        selection = new DifferentialEvolutionSelection();

        algorithm =
                new DifferentialEvolutionBuilder(problem)
                        .setCrossover(crossover)
                        .setSelection(selection)
                        .setSolutionListEvaluator(evaluator)
                        .setMaxEvaluations(4)
                        .setPopulationSize(3)
                        .build();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

        DoubleSolution solution = algorithm.getResult();
        long computingTime = algorithmRunner.getComputingTime();

        List<DoubleSolution> population = new ArrayList<>(1);
        population.add(solution);
        new SolutionListOutput(population)
                .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
                .print();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
        JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
        JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

        JMetalLogger.logger.info("Fitness: " + solution.objectives()[0]);

        evaluator.shutdown();
    }

}
