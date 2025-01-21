package agh.isc.oop.project.simulation;

import agh.isc.oop.project.model.*;
import agh.isc.oop.project.model.elements.AgingAnimalFactory;
import agh.isc.oop.project.model.elements.Animal;
import agh.isc.oop.project.model.elements.AnimalFactory;
import agh.isc.oop.project.model.elements.Genome;
import agh.isc.oop.project.model.map.AbstractWorldMap;
import agh.isc.oop.project.model.util.GenomeGenerator;
import agh.isc.oop.project.model.util.SimulationCSVSaver;
import agh.isc.oop.project.model.util.SimulationStatTracker;
import agh.isc.oop.project.model.util.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class Simulation implements Runnable {

    private final AbstractWorldMap map;
    private final SimulationConfig config;
    private final List<Animal> aliveAnimals = new ArrayList<>();
    private final List<Animal> deadAnimals = new ArrayList<>();
    private final GenomeGenerator genomeGenerator;
    private boolean isRunning = true;
    private boolean isPaused = false; // Nowe pole do obsługi pauzy
    private final AnimalFactory animalFactory;
    private int currentDay = 0;

    private SimulationStatTracker statTracker;

    public Simulation(SimulationConfig config, AbstractWorldMap map, String csvFilePath) {
        this.config = config;
        this.map = map;
        this.currentDay = 0;
        Genome.setConfig(config);
        Animal.setConfig(config);
        this.genomeGenerator = new GenomeGenerator(config.getGenomeLength());
        this.animalFactory = config.isAgingAnimalVariant() ? new AgingAnimalFactory() : new AnimalFactory();

        //Statystyki obserwują zmiany mapy
        this.statTracker = new SimulationStatTracker(this);
        this.map.addObserver(statTracker);

        if(csvFilePath != null){
            //CSV saver obserwuje zmiany statystyk
            statTracker.addObserver(new SimulationCSVSaver(csvFilePath));
        }

        generateAnimalsOnMap(config, map);
    }

    private void generateAnimalsOnMap(SimulationConfig config, AbstractWorldMap map) {
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
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                synchronized (this) {
                    while (isPaused) {
                        wait(1000);
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
            }
        }
        map.removeAnimals(diedThisCycle);
        aliveAnimals.removeAll(diedThisCycle);
        deadAnimals.addAll(diedThisCycle);

        for (Animal animal : aliveAnimals) {
            map.move(animal);
        }

        map.handleEating();

        List<Animal> bornAnimals = map.handleReproduction(currentDay);
        aliveAnimals.addAll(bornAnimals);
        map.grassGrow(config.getDailyGrassGrowth());

        map.mapChanged();
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

    public SimulationStatTracker getStatTracker() {
        return statTracker;
    }
}