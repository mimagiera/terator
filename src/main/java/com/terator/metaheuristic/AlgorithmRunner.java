package com.terator.metaheuristic;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.util.errorchecking.JMetalException;

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

        public Executor(Algorithm<?> algorithm) {
            this.algorithm = algorithm;
        }

        public AlgorithmRunner execute() {
            long initTime = System.currentTimeMillis();
            Thread thread = new Thread(algorithm);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new JMetalException("Error in thread.join()", e);
            }
            computingTime = System.currentTimeMillis() - initTime;

            return new AlgorithmRunner(this);
        }
    }
}