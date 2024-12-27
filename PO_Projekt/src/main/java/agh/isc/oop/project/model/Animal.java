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

    //Liczba dni życia zwierzaka
    protected int age = 0;

    //Do generowania początkowych zwierząt
    public Animal(Vector2d position, List<Integer> geneList) {
        this.position = position;
        this.orientation = MapDirection.getRandomDirection();
        this.genome = new Genome(geneList);
        this.energy = initialEnergy;
    }

    //Do tworzenia dzieci
    public Animal(Animal parent1, Animal parent2) {
        this.position = new Vector2d(parent1.position.getX(), parent1.position.getY());
        this.orientation = MapDirection.getRandomDirection();
        this.genome = new Genome(parent1, parent2);
        //Energia stracona przez rodziców trafia do dziecka
        this.energy = 2 * lostBreedingEnergy;
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

    public void move(){
        // Obrót zwierzaka zgodnie z aktywnym genem
        for (int i = 0; i < genome.getActiveGene(); i++) {
            orientation = orientation.next();
        }
        //Aktywacja kolejnego genu
        genome.updateCurrentGeneIndex();


        //Do dopisania, bo muszę wiedzieć, jak będzie wyglądała mapa.
        //Wydaje mi się, że dobre będzie zrobienie tutaj całej logiki ruchu,
        //obliczania gdzie pójdzie, czy może itd., i zwrócenie nowej pozycji do mapy
        energy -= moveEnergy;
    }

    public void eat(){
        energy += eatingEnergy;
    }

}
