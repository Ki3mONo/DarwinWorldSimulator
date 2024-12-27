package agh.isc.oop.project.model;

import java.util.List;
import java.util.Random;

// Zwierzak do wariantu symulacji ze starzeniem się zwierząt
public class AgingAnimal extends Animal {
    private double missMoveProbability = 0;

    public AgingAnimal(Vector2d position, List<Integer> geneList) {
        super(position, geneList);
    }

    public AgingAnimal(Animal parent1, Animal parent2) {
        super(parent1, parent2);
    }

    @Override
    public void move(){
        Random rand = new Random();
        //Prawdopodobieństwo, że liczba z przedziału od 0 do 1
        // będzie mniejsza niż x, wynosi dokładnie x
        if (rand.nextDouble() < missMoveProbability){
            energy -= moveEnergy;
        }
        else{
            super.move();
        }

        if (missMoveProbability < 0.8) {
            //Nie wiem, czy mamy podawać wartość o ile to zwiększamy
            missMoveProbability += 0.01;
        }
    }
}
