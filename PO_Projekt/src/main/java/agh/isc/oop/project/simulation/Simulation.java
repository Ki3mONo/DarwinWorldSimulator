package agh.isc.oop.project.simulation;
import agh.isc.oop.project.model.*;
import agh.isc.oop.project.model.util.GenomeGenerator;

import java.util.ArrayList;
import java.util.List;

public class Simulation implements Runnable{
    private final AbstractWorldMap map;
    private final SimulationConfig config;
    private final List<Animal> animals = new ArrayList<>();
    private final GenomeGenerator genomeGenerator;
    private boolean isRunning = true;
    private final AnimalFactory animalFactory;
    private int currentDay = 0;

    public Simulation(SimulationConfig config, AbstractWorldMap map) {
        this.config = config;
        this.map = map;
        this.genomeGenerator = new GenomeGenerator(config.getGenomeLength());
        this.animalFactory = config.isAgingAnimalVariant() ? new AgingAnimalFactory() : new AnimalFactory();

        //nie wiem czy przenosić to do metody, bo to chyba tylko tymczasowe
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
        //to już jest w konstruktorze mapy?
        map.initializeGrass(config.getStartGrassCount());
    }

    @Override
    public void run() {
        while (isRunning) {
            performDayCycle();
            currentDay++;
            try {
                Thread.sleep(config.getDayDurationMs());
            } catch (InterruptedException e) {
                System.err.println("Simulation interrupted.");
                isRunning = false;
            }
        }
    }

    private void performDayCycle() {
        animals.removeIf(animal -> {
            if (animal.getEnergy() <= 0) {
                map.mapChanged("Animal died at: " + animal.getPosition());
                return true;
            }
            return false;
        });

        for (Animal animal : animals) {
            map.move(animal);
        }

        map.handleEating(config.getGrassEnergy());

        map.handleReproduction(currentDay, config.getReproductionEnergy());

        map.grassGrow(config.getDailyGrassGrowth());
    }

    public void stop() {
        isRunning = false;
    }

    public List<Animal> getAnimals() {
        return animals;
    }
}
