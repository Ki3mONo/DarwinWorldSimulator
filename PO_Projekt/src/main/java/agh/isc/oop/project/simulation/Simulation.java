package agh.isc.oop.project.simulation;

import agh.isc.oop.project.model.*;
import agh.isc.oop.project.model.util.GenomeGenerator;
import agh.isc.oop.project.model.util.SimulationCSVSaver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Simulation implements Runnable {

    private final AbstractWorldMap map;
    private final SimulationConfig config;
    private final List<Animal> animals = new ArrayList<>();
    private final GenomeGenerator genomeGenerator;
    private final SimulationCSVSaver csvSaver;
    private boolean isRunning = true;
    private boolean isPaused = false; // Nowe pole do obsługi pauzy
    private final AnimalFactory animalFactory;
    private int currentDay = 0;

    private final List<Integer> deadAnimalsLifespan = new ArrayList<>();

    public Simulation(SimulationConfig config, AbstractWorldMap map, String csvFilePath) {
        this.config = config;
        this.map = map;
        this.currentDay = 0;
        Genome.setConfig(config);
        Animal.setConfig(config);
        this.genomeGenerator = new GenomeGenerator(config.getGenomeLength());
        this.animalFactory = config.isAgingAnimalVariant() ? new AgingAnimalFactory() : new AnimalFactory();
        this.csvSaver = csvFilePath != null ? new SimulationCSVSaver(this, csvFilePath) : null;

        // Inicjalizacja zwierząt
        for (int i = 0; i < config.getStartAnimalCount(); i++) {
            Vector2d position = new Vector2d(
                    (int) (Math.random() * config.getMapWidth()),
                    (int) (Math.random() * config.getMapHeight())
            );
            Animal animal = animalFactory.createAnimal(position, genomeGenerator.generateGenome());
            try {
                map.place(animal);
                animals.add(animal);
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
        List<Animal> deadAnimals = new ArrayList<>();
        for (Animal animal : animals) {
            if (animal.getEnergy() <= 0) {
                animal.die(currentDay);
                deadAnimals.add(animal);
                deadAnimalsLifespan.add(currentDay - animal.getBirthDate());
                map.mapChanged("Animal died at: " + animal.getPosition());
            }
        }
        map.removeAnimals(deadAnimals);
        animals.removeAll(deadAnimals);

        for (Animal animal : animals) {
            map.move(animal);
        }

        map.handleEating(config.getGrassEnergy());
        List<Animal> bornAnimals = map.handleReproduction(currentDay, config.getReproductionCost());
        animals.addAll(bornAnimals);
        map.grassGrow(config.getDailyGrassGrowth());
        if (csvSaver != null) {
            csvSaver.saveDayStatistics();
        }
        map.mapChanged("Day " + currentDay + " finished");
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

    public List<Animal> getAnimals() {
        return animals;
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


}