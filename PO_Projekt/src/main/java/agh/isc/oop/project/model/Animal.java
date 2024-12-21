package agh.isc.oop.project.model;

import java.util.List;

public class Animal implements WorldElement{
    private static int initialEnergy;

    private static int requiredBreedingEnergy;

    private static int lostBreedingEnergy;

    private static int eatingEnergy;

    public static void setInitialEnergy(int initialEnergy) {
        Animal.initialEnergy = initialEnergy;
    }

    public static void setRequiredBreedingEnergy(int requiredBreedingEnergy) {
        Animal.requiredBreedingEnergy = requiredBreedingEnergy;
    }

    public static void setLostBreedingEnergy(int lostBreedingEnergy) {}

    public static void setEatingEnergy(int eatingEnergy) {
        Animal.eatingEnergy = eatingEnergy;
    }


    private Vector2d position;
    private MapDirection orientation;

    private int energy;

    private Genome genome;

    //Liczba dni życia zwierzaka
    private int age;

    //Do generowania początkowych zwierząt
    public Animal(Vector2d position, List<Integer> geneList) {
        this.position = position;
        this.genome = new Genome(geneList);

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


}
