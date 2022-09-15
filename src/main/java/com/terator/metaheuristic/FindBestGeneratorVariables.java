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
import com.terator.service.routesCreator.RoutesCreator;
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
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.terator.metaheuristic.GeneratorProblem.ACCURACY_ATTRIBUTE;
import static com.terator.metaheuristic.GeneratorProblem.STDDEV_ATTRIBUTE;
import static com.terator.metaheuristic.GeneratorProblem.TRAJECTORIES_ATTRIBUTE;

@Service
@RequiredArgsConstructor
public class FindBestGeneratorVariables {
    public static final String RESULTS_DIR = "results" + UUID.randomUUID();
    public static final String STD_DEV_FILE_NAME = "stddev.tsv";
    private static final Logger LOGGER = LoggerFactory.getLogger(FindBestGeneratorVariables.class);

    private final TrajectoryListCreator trajectoryListCreator;
    private final FixturesLocationMatcher fixturesLocationMatcher;
    private final AccuracyChecker accuracyChecker;
    private final SimulationExecutor simulationExecutor;
    private final RoutesCreator routesCreator;

    public DoubleSolution doEverything(
            City city,
            Map<BuildingType, List<? extends LocationWithMetaSpecificParameter>> allBuildingsByType,
            Map<Integer, Set<AggregatedTrafficBySegment>> aggregatedTrafficBySegments,
            int concurrentSimulations,
            int concurrentRoutesGenerator
    ) {
        SolutionListEvaluator<DoubleSolution> evaluator;
        evaluator = new MultiThreadedSolutionListEvaluator<>(24);

        DoubleProblem problem = new GeneratorProblem(trajectoryListCreator, fixturesLocationMatcher, accuracyChecker,
                simulationExecutor, city,
                allBuildingsByType, routesCreator, aggregatedTrafficBySegments, concurrentSimulations,
                concurrentRoutesGenerator);

        DifferentialEvolutionCrossover crossover = new DifferentialEvolutionCrossover(
                0.5, 0.5, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN
        );

        DifferentialEvolutionSelection selection = new DifferentialEvolutionSelection();

        Algorithm<DoubleSolution> algorithm = new DifferentialEvolutionBuilder(problem)
                .setCrossover(crossover)
                .setSelection(selection)
                .setSolutionListEvaluator(evaluator)
                .setMaxEvaluations(300)
                .setPopulationSize(8)
                .build();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

        DoubleSolution solution = algorithm.getResult();
        long computingTime = algorithmRunner.getComputingTime();

        saveResults(solution, computingTime);

        evaluator.shutdown();
        return solution;
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
        saveAccuracyToFile(attributes);
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

    private void saveAccuracyToFile(Map<Object, Object> attributes) {
        attributes.entrySet().stream()
                .filter(entry -> entry.getKey().toString().startsWith(ACCURACY_ATTRIBUTE))
                .forEach(entrySet -> {
                    final String fileName = entrySet.getKey().toString();
                    try (FileOutputStream fileOutStdDev = FileUtils.openOutputStream(new File(RESULTS_DIR, fileName))) {
                        ObjectOutputStream objectOut = new ObjectOutputStream(fileOutStdDev);
                        objectOut.writeObject(entrySet.getValue());
                    } catch (IOException e) {
                        LOGGER.error("Couldn't save accuracy to file", e);
                    }
                });
        LOGGER.info("Accuracy value has been written to files {}", RESULTS_DIR + "/" + ACCURACY_ATTRIBUTE + "{i}");
    }

    private void saveStdDevToFile(Double stddev) {
        try (
                FileOutputStream fileOutStdDev = FileUtils.openOutputStream(new File(RESULTS_DIR, STD_DEV_FILE_NAME));
        ) {
            fileOutStdDev.write(stddev.toString().getBytes());
        } catch (IOException e) {
            LOGGER.error("Couldn't save stddev to file", e);
        }
        LOGGER.info("Stddev value has been written to file {}", RESULTS_DIR + "/" + STD_DEV_FILE_NAME);
    }

}
