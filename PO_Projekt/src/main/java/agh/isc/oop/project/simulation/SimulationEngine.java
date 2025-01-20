package agh.isc.oop.project.simulation;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class SimulationEngine {

    private final ForkJoinPool executorService = ForkJoinPool.commonPool();
    private final List<Simulation> simulations = new ArrayList<>();

    public void addSimulation(Simulation simulation) {
        simulations.add(simulation);
    }

    public void runAsync() {
        for (Simulation simulation : simulations) {
            executorService.submit(simulation);
        }
    }

    public void stopAll() {
        for (Simulation simulation : simulations) {
            simulation.stop();
        }
        shutdownExecutor();
    }

    public void pauseAll() {
        for (Simulation simulation : simulations) {
            simulation.pause();
        }
    }

    public void resumeAll() {
        for (Simulation simulation : simulations) {
            simulation.resume();
        }
    }

    private void shutdownExecutor() {
        executorService.shutdownNow();
    }

}
