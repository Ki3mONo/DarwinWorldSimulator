package agh.isc.oop.project.model.elements;

import agh.isc.oop.project.model.util.Vector2d;
import agh.isc.oop.project.model.map.AbstractWorldMap;
import agh.isc.oop.project.model.map.MapDirection;
import agh.isc.oop.project.simulation.SimulationConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Animal implements WorldElement {
    protected static SimulationConfig config;

    public static void setConfig(SimulationConfig config) {
        Animal.config = config;
    }

    private Vector2d position;
    private MapDirection orientation;
    protected int energy;

    boolean alive = true;
    private final Genome genome;
    private final int birthDate;
    private int deathDay = -1; // -1 oznacza, że zwierzę jeszcze nie zmarło
    private int grassEaten = 0; // Liczba zjedzonych roślin

    private List<Animal> children = new ArrayList<>();

    public void addChild(Animal child) {
        this.children.add(child);
    }

    public List<Animal> getChildren() {
        return children;
    }

    private final String cachedAnimalIcon;


    public Animal(Vector2d position, List<Integer> geneList) {
        this.position = position;
        this.orientation = MapDirection.getRandomDirection();
        this.genome = new Genome(geneList);
        this.energy = config.getInitialEnergy();
        this.birthDate = 0;
        this.cachedAnimalIcon = computeAnimalIcon();
    }

    // Konstruktor dla dzieci
    public Animal(Animal parent1, Animal parent2, int currentDay) {
        this.position = new Vector2d(parent1.position.getX(), parent1.position.getY());
        this.orientation = MapDirection.getRandomDirection();
        this.genome = new Genome(parent1, parent2);
        this.energy = 2 * config.getReproductionCost();
        this.birthDate = currentDay;
        this.cachedAnimalIcon = computeAnimalIcon();
    }

    public int getEnergy() {
        return energy;
    }

    public Genome getGenome() {
        return genome;
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    public Vector2d move(AbstractWorldMap map) {
        if (!isAlive()) {
            return position; // Jeśli zwierzę nie żyje, nie może się poruszać
        }

        energy -= config.getMoveCost();
        orientation = orientation.turnBy(genome.getActiveGene());
        genome.updateCurrentGeneIndex();

        Vector2d newPosition = position.add(orientation.toUnitVector());

        if (newPosition.getX() == -1 || newPosition.getX() == config.getMapWidth()) {
            newPosition = adjustPosition(newPosition);
        }

        if (map.canMoveTo(newPosition)) {
            position = newPosition;
        } else {
            orientation = orientation.reverse();
        }

        return position;
    }

    private Vector2d adjustPosition(Vector2d position) {
        int x = position.getX();
        if (x == -1)
            x = config.getMapWidth() - 1;
        else if (x == config.getMapWidth())
            x = 0;
        return new Vector2d(x, position.getY());
    }

    public int getBirthDate() {
        return birthDate;
    }

    public void eat(int grassEnergy) {
        energy += grassEnergy;
        grassEaten++;
    }

    public void loseReproductionEnergy() {
        energy -= config.getReproductionCost();
    }

    public int getMaxEnergy() {
        return 2*config.getInitialEnergy();
    }
    @Override
    public String getResourceName() {
        return "/animals/" + cachedAnimalIcon;
    }

    private String computeAnimalIcon() {
        int mod = genome.genomeSum() % 20;
        return switch (mod) {
            case 1, 2, 3 -> "bocian.png";
            case 4, 5, 6 -> "kurczak.png";
            case 7, 8, 9 -> "kruk.png";
            case 10, 11, 12, 13, 14 -> "pingwin.png";
            case 15, 16, 17, 18, 19 -> "kura.png";
            default -> "sowoniedz.png";
        };
    }

    @Override
    public String toString() {
        return "A";
    }

    /**
     * Zwraca liczbę zjedzonych roślin przez zwierzaka.
     */
    public int getGrassEaten() {
        return grassEaten;
    }

    /**
     * Sprawdza, czy zwierzę jest żywe (czy ma dodatnią ilość energii).
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Zwraca wiek zwierzaka na podstawie bieżącego dnia symulacji.
     */
    public int getAge(int currentDay) {
        return isAlive() ? (currentDay - birthDate) : (deathDay - birthDate);
    }

    /**
     * Ustawia dzień śmierci zwierzaka.
     */
    public void die(int currentDay) {
        if (isAlive()) {
            this.alive=false;
            energy = 0;
            this.deathDay = currentDay-birthDate;
        }
    }

    public int getDeathDay() {
        return deathDay;
    }

    public int getChildrenCount() {
        return children.size();
    }
    public int getDescendantsCount() {
        Set<Animal> uniqueDescendants = new HashSet<>();
        collectDescendants(this, uniqueDescendants);
        return uniqueDescendants.size();
    }

    private void collectDescendants(Animal animal, Set<Animal> uniqueDescendants) {
        for (Animal child : animal.getChildren()) {
            if (uniqueDescendants.add(child)) {
                collectDescendants(child, uniqueDescendants);
            }
        }
    }
}
