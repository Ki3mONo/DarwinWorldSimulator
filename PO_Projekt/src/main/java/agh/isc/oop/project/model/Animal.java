package agh.isc.oop.project.model;

import agh.isc.oop.project.simulation.SimulationConfig;

import java.util.List;

public class Animal implements WorldElement{
    protected static SimulationConfig config;

    public static void setConfig(SimulationConfig config) {
        Animal.config = config;
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
        this.energy = config.getInitialEnergy();
        this.birthDate = 0;
    }

    //Do tworzenia dzieci
    public Animal(Animal parent1, Animal parent2, int currentDay) {
        this.position = new Vector2d(parent1.position.getX(), parent1.position.getY());
        this.orientation = MapDirection.getRandomDirection();
        this.genome = new Genome(parent1, parent2);
        //Energia stracona przez rodziców trafia do dziecka
        this.energy = 2 * config.getReproductionCost();
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
        energy -= config.getMoveCost();

        // Obrót zwierzaka zgodnie z aktywnym genem
        orientation = orientation.turnBy(genome.getActiveGene());

        //Aktywacja kolejnego genu
        genome.updateCurrentGeneIndex();

        //Wyliczenie nowej pozycji, przeniesienie między prawą i lewą krawędzią mapy
        //załatwia funkcja adjustPosition()
        Vector2d newPosition = position.add(orientation.toUnitVector());

        //Jeśli ma nastąpić przeniesienie dookoła kuli ziemskiej, to
        //ta metoda zwróci nową pozycję po drugiej stronie
        if (newPosition.getX() == -1 || newPosition.getX() == config.getMapWidth())
            newPosition = adjustPosition(newPosition);

        //Jeśli nie da się tam ruszyć, to pozycja się nie zmienia, a zwierzak się obraca
        if (map.canMoveTo(newPosition)) {
            position = newPosition;
        } else {
            orientation = orientation.reverse();
        }

        return position;
    }

    private Vector2d adjustPosition(Vector2d position){
        int x = position.getX();
        if (x == -1)
            x = config.getMapWidth() - 1;
        else if (x == config.getMapWidth())
            x = 0;

        return new Vector2d(x, position.getY());
    }

    public void eat(int grassEnergy){
        energy += grassEnergy;
    }

    public void loseReproductionEnergy(){
        energy -= config.getReproductionCost();
    }
}
