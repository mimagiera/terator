package com.terator.metaheuristic;

import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.terator.model.City;
import com.terator.model.LocationWithMetaSpecificParameter;
import com.terator.model.SingleTrajectory;
import com.terator.model.Trajectories;
import com.terator.model.inductionLoops.AggregatedTrafficBySegment;
import com.terator.service.accuracyChecker.AccuracyChecker;
import com.terator.service.generatorCreator.building.BuildingType;
import com.terator.service.inductionLoopsWithOsm.FixturesLocationMatcher;
import com.terator.service.simulationExecutor.SimulationExecutor;
import com.terator.service.trajectoryListCreator.TrajectoryListCreator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.terator.metaheuristic.GeneratorProblem.STDDEV_ATTRIBUTE;
import static com.terator.metaheuristic.GeneratorProblem.TRAJECTORIES_ATTRIBUTE;

@Service
@RequiredArgsConstructor
public class FindBestGeneratorVariables {
    public static final String RESULTS_DIR = "results";
    public static final String STD_DEV_FILE_NAME = "stddev.tsv";
    private static final Logger LOGGER = LoggerFactory.getLogger(FindBestGeneratorVariables.class);
    private static final int DEFAULT_NUMBER_OF_CORES = 1;

    private final TrajectoryListCreator trajectoryListCreator;
    private final FixturesLocationMatcher fixturesLocationMatcher;
    private final AccuracyChecker accuracyChecker;
    private final SimulationExecutor simulationExecutor;

    public void doEverything(
            City city,
            Map<BuildingType, List<? extends LocationWithMetaSpecificParameter>> allBuildingsByType,
            Map<Integer, Set<AggregatedTrafficBySegment>> aggregatedTrafficBySegments,
            int nThreads
    ) {
        DoubleProblem problem;
        Algorithm<DoubleSolution> algorithm;
        DifferentialEvolutionSelection selection;
        DifferentialEvolutionCrossover crossover;
        SolutionListEvaluator<DoubleSolution> evaluator;

        problem = new GeneratorProblem(trajectoryListCreator, fixturesLocationMatcher, accuracyChecker,
                simulationExecutor, city,
                allBuildingsByType, aggregatedTrafficBySegments, nThreads);

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
                        .setMaxEvaluations(1)
                        .setPopulationSize(3)
                        .build();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

        DoubleSolution solution = algorithm.getResult();
        long computingTime = algorithmRunner.getComputingTime();

        saveResults(solution, computingTime);

        evaluator.shutdown();
    }

    private void saveResults(DoubleSolution solution, long computingTime) {
        File directory = new File(RESULTS_DIR);
        if (!directory.exists()) {
            directory.mkdir();
        }
        new SolutionListOutput(List.of(solution))
                .setVarFileOutputContext(new DefaultFileOutputContext(RESULTS_DIR + "/VAR.tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext(RESULTS_DIR + "/FUN.tsv"))
                .print();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
        JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
        JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

        JMetalLogger.logger.info("Fitness: " + solution.objectives()[0]);

        saveAttributesToFiles(solution.attributes());
    }

    private void saveAttributesToFiles(Map<Object, Object> attributes) {
        saveStdDevToFile((Double) attributes.get(STDDEV_ATTRIBUTE));
        saveTrajectoriesToCsvFiles(attributes);
    }

    private void saveTrajectoriesToCsvFiles(Map<Object, Object> attributes) {
        attributes.entrySet().stream()
                .filter(entry -> entry.getKey().toString().startsWith(TRAJECTORIES_ATTRIBUTE))
                .forEach(entrySet -> {
                    String trajectoriesFileName = RESULTS_DIR + "/" + entrySet.getKey().toString() + ".csv";
                    try (FileWriter writer = new FileWriter(trajectoriesFileName)) {
                        var singleTrajectories = ((Trajectories) entrySet.getValue()).singleTrajectories();
                        new StatefulBeanToCsvBuilder<SingleTrajectory>(writer)
                                .build()
                                .write(singleTrajectories);
                    } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
                        LOGGER.error("Couldn't save trajectories to file", e);
                    }
                });
    }

    private void saveStdDevToFile(Double stddev) {
        try (
                FileOutputStream fileOutStdDev = FileUtils.openOutputStream(new File(RESULTS_DIR, STD_DEV_FILE_NAME));
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOutStdDev)
        ) {
            objectOut.writeUTF(stddev.toString());
        } catch (IOException e) {
            LOGGER.error("Couldn't save stddev to file", e);
        }
        LOGGER.info("Stddev value has been written to file {}", RESULTS_DIR + "/" + STD_DEV_FILE_NAME);
    }

}
