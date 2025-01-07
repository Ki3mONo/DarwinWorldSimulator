package agh.isc.oop.project.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimulationEngine {
    private final List<Simulation> simulations = new ArrayList<>();
    private final ExecutorService executorService;

    public SimulationEngine(int maxSimulations) {
        this.executorService = Executors.newFixedThreadPool(maxSimulations);
    }

    public void addSimulation(Simulation simulation) {
        simulations.add(simulation);
    }


    public void runAsync() {
        for (Simulation simulation : simulations) {
            executorService.submit(simulation);
        }
    }

    public void stopSimulations() {
        for (Simulation simulation : simulations) {
            simulation.stop();
        }
        shutdownExecutorService();
    }

    public void awaitSimulationsEnd() throws InterruptedException {
        shutdownExecutorService();
    }

    private void shutdownExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
