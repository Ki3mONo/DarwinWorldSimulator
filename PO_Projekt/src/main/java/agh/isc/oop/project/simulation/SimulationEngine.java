package agh.isc.oop.project.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimulationEngine {

    private final ExecutorService executorService = Executors.newCachedThreadPool();
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
