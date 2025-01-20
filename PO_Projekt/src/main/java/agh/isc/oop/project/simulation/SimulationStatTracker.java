package agh.isc.oop.project.simulation;

import agh.isc.oop.project.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class SimulationStatTracker implements MapChangeListener {
    Simulation simulation;
    private int animalCount;    //Tylko żywe
    private int grassCount;
    private int freeFields;
    private List<Integer> mostPopularGenes;
    private double averageEnergy;   //Tylko dla żywych
    private double averageLifespan; //Tylko dla martwych
    private double averageChildren; //Tylko dla żywych

    public SimulationStatTracker(Simulation simulation) {
        this.simulation = simulation;
        updateAll();
    }

    @Override
    public void mapChanged(AbstractWorldMap worldMap) {
        updateAll();
    }

    private void updateAll() {
        updateAnimalCount();
        updateGrassCount();
        updateFreeFields();
        updateMostPopularGenes();
        updateAverageEnergy();
        updateAverageLifespan();
        updateAverageChildren();
    }

    private void updateAnimalCount(){
        animalCount = simulation.getAliveAnimals().size();
    }

    private void updateGrassCount(){
        grassCount = simulation.getMap().getGrassCount();
    }

    private void updateFreeFields(){
        HashMap<Vector2d, List<WorldElement>> worldElements = simulation.getMap().getWorldElements();
        int occupiedFields = (int) worldElements.keySet().stream()
                .filter(k -> worldElements.get(k) != null && !worldElements.get(k).isEmpty())
                .count();

        freeFields = simulation.getConfig().getMapHeight() *
                simulation.getConfig().getMapWidth() - occupiedFields;
    }

    private void updateMostPopularGenes(){
        List<Animal> allAnimals = new ArrayList<>(simulation.getAliveAnimals());
        allAnimals.addAll(simulation.getDeadAnimals());
        Map<List<Integer>, Long> genotypeFrequency = allAnimals.stream()
                .collect(Collectors.groupingBy(a -> a.getGenome().getGeneList(), Collectors.counting()));

        mostPopularGenes = genotypeFrequency.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(Collections.emptyList());
    }

    private void updateAverageEnergy(){
        averageEnergy = simulation.getAliveAnimals().stream()
                .mapToInt(Animal::getEnergy)
                .average()
                .orElse(0);
    }

    private void updateAverageLifespan(){
        averageLifespan = simulation.getDeadAnimals().stream()
                .mapToInt(Animal::getDeathDay)
                .average()
                .orElse(0);
    }

    private void updateAverageChildren(){
        averageChildren = simulation.getAliveAnimals().stream()
                .mapToInt(Animal::getChildrenCount)
                .average()
                .orElse(0);
    }

    public int getAnimalCount() {
        return animalCount;
    }

    public int getGrassCount() {
        return grassCount;
    }

    public int getFreeFields() {
        return freeFields;
    }

    public List<Integer> getMostPopularGenes() {
        return mostPopularGenes;
    }

    public double getAverageEnergy() {
        return averageEnergy;
    }

    public double getAverageLifespan() {
        return averageLifespan;
    }

    public double getAverageChildren() {
        return averageChildren;
    }
}
