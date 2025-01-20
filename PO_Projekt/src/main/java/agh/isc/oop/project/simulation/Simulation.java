package agh.isc.oop.project.simulation;

import agh.isc.oop.project.model.*;
import agh.isc.oop.project.model.util.GenomeGenerator;
import agh.isc.oop.project.model.util.SimulationCSVSaver;

import java.util.ArrayList;
import java.util.List;

public class Simulation implements Runnable {

    private final AbstractWorldMap map;
    private final SimulationConfig config;
    private final List<Animal> aliveAnimals = new ArrayList<>();
    private final List<Animal> deadAnimals = new ArrayList<>();
    private final GenomeGenerator genomeGenerator;
    //private final SimulationCSVSaver csvSaver;
    private boolean isRunning = true;
    private boolean isPaused = false; // Nowe pole do obsługi pauzy
    private final AnimalFactory animalFactory;
    private int currentDay = 0;

    private SimulationStatTracker statTracker;
    //do wywalenia jak skończę
    private final List<Integer> deadAnimalsLifespan = new ArrayList<>();

    public Simulation(SimulationConfig config, AbstractWorldMap map, String csvFilePath) {
        this.config = config;
        this.map = map;
        this.currentDay = 0;
        Genome.setConfig(config);
        Animal.setConfig(config);
        this.genomeGenerator = new GenomeGenerator(config.getGenomeLength());
        this.animalFactory = config.isAgingAnimalVariant() ? new AgingAnimalFactory() : new AnimalFactory();

        this.statTracker = new SimulationStatTracker(this);
        this.map.addObserver(statTracker);

        if(csvFilePath != null){
            this.map.addObserver(new SimulationCSVSaver(this, csvFilePath));
        }
        //to do wywalenia i jako observer do mapy
        //this.csvSaver = csvFilePath != null ? new SimulationCSVSaver(this, csvFilePath) : null;

        // Inicjalizacja zwierząt
        for (int i = 0; i < config.getStartAnimalCount(); i++) {
            Vector2d position = new Vector2d(
                    (int) (Math.random() * config.getMapWidth()),
                    (int) (Math.random() * config.getMapHeight())
            );
            Animal animal = animalFactory.createAnimal(position, genomeGenerator.generateGenome());
            try {
                map.place(animal);
                aliveAnimals.add(animal);
            } catch (IncorrectPositionException e) {
                System.err.println("Failed to place animal: " + e.getMessage());
            }
        }

        // Inicjalizacja trawy
        map.initializeGrass(config.getStartGrassCount());
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                synchronized (this) {
                    while (isPaused) {
                        wait(config.getDayDurationMs());
                    }
                }
                performDayCycle();
                currentDay++;
                Thread.sleep(config.getDayDurationMs());
            } catch (InterruptedException e) {
                System.err.println("Simulation interrupted.");
                isRunning = false;
                Thread.currentThread().interrupt();
            }
        }
    }


    private void performDayCycle() {
        List<Animal> diedThisCycle = new ArrayList<>();
        for (Animal animal : aliveAnimals) {
            if (animal.getEnergy() <= 0) {
                animal.die(currentDay);
                diedThisCycle.add(animal);
                //do wywalenia jak skończę
                deadAnimalsLifespan.add(currentDay - animal.getBirthDate());
            }
        }
        map.removeAnimals(diedThisCycle);
        aliveAnimals.removeAll(diedThisCycle);
        deadAnimals.addAll(diedThisCycle);

        for (Animal animal : aliveAnimals) {
            map.move(animal);
        }

        map.handleEating(config.getGrassEnergy());

        List<Animal> bornAnimals = map.handleReproduction(currentDay, config.getReproductionCost());
        aliveAnimals.addAll(bornAnimals);
        map.grassGrow(config.getDailyGrassGrowth());

        map.mapChanged();
        //to też docelowo ma być observer
        //if (csvSaver != null) {
        //    csvSaver.saveDayStatistics();
        //}
    }
    public void stop() {
        isRunning = false;
        resume();
    }

    public synchronized void pause() {
        isPaused = true;
    }

    public synchronized void resume() {
        isPaused = false;
        notifyAll();
    }

    public boolean isPaused() {
        return isPaused;
    }

    public List<Animal> getAliveAnimals() {
        return aliveAnimals;
    }

    public List<Animal> getDeadAnimals() {
        return deadAnimals;
    }

    public AbstractWorldMap getMap() {
        return map;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public SimulationConfig getConfig() {
        return config;
    }

    public double getAverageLifespan() {
        synchronized (deadAnimalsLifespan) {
            List<Integer> copyList = new ArrayList<>(deadAnimalsLifespan); // Create a copy of the list
            return copyList.stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0);
        }
    }


    public SimulationStatTracker getStatTracker() {
        return statTracker;
    }
}