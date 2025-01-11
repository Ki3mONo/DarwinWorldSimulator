package agh.isc.oop.project.model;

import java.util.List;
import java.util.Random;

public class Animal implements WorldElement{
    private static int initialEnergy;

    private static int requiredBreedingEnergy;

    private static int lostBreedingEnergy;

    private static int eatingEnergy;

    protected static int moveEnergy;

    public static void setInitialEnergy(int initialEnergy) {
        Animal.initialEnergy = initialEnergy;
    }

    public static void setRequiredBreedingEnergy(int requiredBreedingEnergy) {
        Animal.requiredBreedingEnergy = requiredBreedingEnergy;
    }

    public static void setLostBreedingEnergy(int lostBreedingEnergy) {
        Animal.lostBreedingEnergy = lostBreedingEnergy;
    }

    public static void setEatingEnergy(int eatingEnergy) {
        Animal.eatingEnergy = eatingEnergy;
    }

    public static void setMoveEnergy(int moveEnergy) {
        Animal.moveEnergy = moveEnergy;
    }

    private Vector2d position;
    private MapDirection orientation;

    protected int energy;

    private final Genome genome;

    //Dzień urodzenia zwierzaka
    private final int birthDate;

    //Do generowania początkowych zwierząt
    public Animal(Vector2d position, List<Integer> geneList) {
        this.position = position;
        this.orientation = MapDirection.getRandomDirection();
        this.genome = new Genome(geneList);
        this.energy = initialEnergy;
        this.birthDate = 0;
    }

    //Do tworzenia dzieci
    public Animal(Animal parent1, Animal parent2, int currentDay) {
        this.position = new Vector2d(parent1.position.getX(), parent1.position.getY());
        this.orientation = MapDirection.getRandomDirection();
        this.genome = new Genome(parent1, parent2);
        //Energia stracona przez rodziców trafia do dziecka
        this.energy = 2 * lostBreedingEnergy;
        this.birthDate = currentDay;
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


    //Przyjmuje mapę, żeby obliczyć nową pozycję zwierzaka.
    //Zwraca nową pozycję do zapisania w mapie
    public Vector2d move(AbstractWorldMap map){
        //Zużycie energii na ruch
        energy -= moveEnergy;

        // Obrót zwierzaka zgodnie z aktywnym genem
        orientation = orientation.turnBy(genome.getActiveGene());

        //Aktywacja kolejnego genu
        genome.updateCurrentGeneIndex();

        //Wyliczenie nowej pozycji, przeniesienie między prawą i lewą krawędzią mapy
        //załatwia funkcja adjustPosition()
        Vector2d newPosition = map.adjustPosition(position.add(orientation.toUnitVector()));

        //Jeśli nie da się tam ruszyć, to pozycja się nie zmienia, a zwierzak się obraca
        if (map.canMoveTo(newPosition)) {
            position = newPosition;
        } else {
            orientation = orientation.reverse();
        }

        return position;
    }

    public void eat(){
        energy += eatingEnergy;
    }

}
