package agh.isc.oop.project.model.elements;

import agh.isc.oop.project.model.util.Vector2d;
import agh.isc.oop.project.model.map.AbstractWorldMap;

import java.util.List;
import java.util.Random;

/**
 * Klasa reprezentująca zwierzaki w wariancie symulacji
 * ze starzejącymi się zwierzakami. Dziedziczy po klasie
 * Animal i nadpisuje tylko metodę move
 */
public class AgingAnimal extends Animal {
    //Prawdopodobieństwo ominięcia ruchu
    private double missMoveProbability = 0;

    Random rand = new Random();

    //Oba konstruktory robią to samo, co w Animal
    public AgingAnimal(Vector2d position, List<Integer> geneList) {
        super(position, geneList);
    }

    public AgingAnimal(Animal parent1, Animal parent2, int currentDay) {
        super(parent1, parent2, currentDay);
    }

    /**
     * Metoda odpowiadająca za obliczenie nowej pozycji zwierzaka przy ruchu.
     * Z prawdopodobieństwem missMoveProbability omija ruch i jedynie traci energię,
     * w przeciwnym wypadku wykonuje metodę move z klasy Animal.
     * Co każdy ruch zwiększa prawdopodobieństwo ominięcia ruchu o 0.01,
     * aż do wartości 0.8
     * @param map mapa, na której znajduje się zwierzak
     * @return nowa pozycja zwierzaka lub stara, jeżeli się nie ruszył
     */
    @Override
    public Vector2d move(AbstractWorldMap map){

        Vector2d newPosition = getPosition();

        //Prawdopodobieństwo, że liczba z przedziału od 0 do 1
        // będzie mniejsza niż x, wynosi dokładnie x
        if (rand.nextDouble() < missMoveProbability){
            energy -= config.getMoveCost();;
        } else {
            //Odejmowanie energii jest już tam w środku
            newPosition = super.move(map);
        }

        // Zwiększenie prawdopodobieństwa ominięcia ruchu
        if (missMoveProbability < 0.8) {
            //Ustawione na sztywno, bo w poleceniu nie było powiedziane,
            //czy to ma być parametr
            missMoveProbability += 0.01;
        }

        return newPosition;
    }
}
