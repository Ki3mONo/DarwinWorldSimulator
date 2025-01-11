package agh.isc.oop.project.model;

import java.util.List;
import java.util.Random;

// Zwierzak do wariantu symulacji ze starzeniem się zwierząt
public class AgingAnimal extends Animal {
    private double missMoveProbability = 0;

    public AgingAnimal(Vector2d position, List<Integer> geneList) {
        super(position, geneList);
    }

    public AgingAnimal(Animal parent1, Animal parent2, int currentDay) {
        super(parent1, parent2, currentDay);
    }

    @Override
    public Vector2d move(AbstractWorldMap map){
        Random rand = new Random();

        Vector2d newPosition = getPosition();

        //Prawdopodobieństwo, że liczba z przedziału od 0 do 1
        // będzie mniejsza niż x, wynosi dokładnie x
        if (rand.nextDouble() < missMoveProbability){
            energy -= moveEnergy;
        } else {
            //Odejmowanie energii jest już tam w środku
            newPosition = super.move(map);
        }

        if (missMoveProbability < 0.8) {
            //Nie wiem, czy mamy podawać wartość o ile to zwiększamy
            missMoveProbability += 0.01;
        }
        return newPosition;
    }
}
