package agh.isc.oop.project.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 * Klasa Silnika symulacji odpowiedzialnego za wykonywanie zadań symulacyjnych przy użyciu puli wątków.
 * Klasa korzysta z wątków, aby uruchamiać symulacje równolegle.
 * Klasa korzysta z klasy ForkJoinPool, która implementuje interfejs ExecutorService.
 */
public class SimulationEngine {
    // Pula wątków
    private final ForkJoinPool executorService = ForkJoinPool.commonPool();
    // Lista symulacji
    private final List<Simulation> simulations = new ArrayList<>();

    /**
     * Dodaje symulację do managera oraz wykonuje ją.
     * @param simulation symulacja do dodania
     */
    public void addSimulation(Simulation simulation) {
        simulations.add(simulation);
        executorService.submit(simulation);
    }
}
