package agh.isc.oop.project.model.util;

import agh.isc.oop.project.model.elements.Animal;
import agh.isc.oop.project.model.elements.WorldElement;
import agh.isc.oop.project.model.map.AbstractWorldMap;
import agh.isc.oop.project.simulation.Simulation;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Klasa odpowiedzialna za śledzenie statystyk symulacji,
 * jest obserwatorem mapy.
 */
public class SimulationStatTracker implements MapChangeListener {
    Simulation simulation;
    private int animalCount=0;    //Tylko żywe
    private int grassCount=0;
    private int freeFields=0;
    private List<Integer> mostPopularGenes = List.of();
    private double averageEnergy=0;   //Tylko dla żywych
    private double averageLifespan=0; //Tylko dla martwych
    private double averageChildren=0; //Tylko dla żywych

    private List<StatsChangeListener> observers = new LinkedList<>();

    /**
     * Konstruktor zapisuje symulacje, której statystyki śledzimy
     * @param simulation
     */
    public SimulationStatTracker(Simulation simulation) {
        this.simulation = simulation;
    }

    /**
     * Metoda dodająca obserwatora
     * @param observer obserwator do dodania
     */
    public void addObserver(StatsChangeListener observer) {
        observers.add(observer);
    }

    /**
     * Metoda usuwająca obserwatora
     * @param observer obserwator do usunięcia
     */
    public void removeObserver(StatsChangeListener observer) {
        observers.remove(observer);
    }

    /**
     * Powiadomienie wszystkich obserwatorów o zmianie statystyk
     */
    private void statsChanged() {
        for (StatsChangeListener observer : observers) {
            observer.statsChanged(this, simulation.getCurrentDay());
        }
    }

    /**
     * Reakcja na zmianę mapy
     * aktualizuje statystyki oraz powiadamia obserwatorów
     * @param worldMap obserwowana mapa
     */
    @Override
    public void mapChanged(AbstractWorldMap worldMap) {
        updateAll();
        statsChanged();
    }

    /**
     * Aktualizacja wszystkich statystyk
     */
    private void updateAll() {
        updateAnimalCount();
        updateGrassCount();
        updateFreeFields();
        updateMostPopularGenes();
        updateAverageEnergy();
        updateAverageLifespan();
        updateAverageChildren();
    }

    //Metody aktualizujące poszczególne statystyki
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
        List<Animal> aliveAnimals = new ArrayList<>(simulation.getAliveAnimals());

        Map<List<Integer>, Long> genotypeFrequency = aliveAnimals.stream()
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

    //Gettery do odczytania aktualnych statystyk
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
