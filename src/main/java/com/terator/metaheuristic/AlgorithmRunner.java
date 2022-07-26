package com.terator.metaheuristic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uma.jmetal.algorithm.Algorithm;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AlgorithmRunner {
    private final long computingTime;

    private AlgorithmRunner(Executor execute) {
        computingTime = execute.computingTime;
    }

    public long getComputingTime() {
        return computingTime;
    }

    public static class Executor {
        Algorithm<?> algorithm;
        long computingTime;
        private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);

        public Executor(Algorithm<?> algorithm) {
            this.algorithm = algorithm;
        }

        public AlgorithmRunner execute() {
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            long initTime = System.currentTimeMillis();

            var future = executor.submit(algorithm);
            try {
                future.get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                LOGGER.error("Time for a program has finished but it is ok.");
                future.cancel(true);
            } finally {
                executor.shutdown();
            }

            computingTime = System.currentTimeMillis() - initTime;

            return new AlgorithmRunner(this);
        }
    }
}