package agh.isc.oop.project.model.elements;

import agh.isc.oop.project.model.util.Vector2d;

import java.util.List;

/**
 * Klasa realizuje wzorzec abstract factory.
 * Dziedziczy po klasie AnimalFactory i nadpisuje
 * obie jej metody tak, aby tworzyły obiekty AgingAnimal
 */
public class AgingAnimalFactory extends AnimalFactory {
    /**
     * Tworzenie zwierząt używając pierwszego konstruktora
     * @param position startowa pozycja
     * @param geneList list genów
     * @return nowy obiekt klasy AgingAnimal
     */
    @Override
    public Animal createAnimal(Vector2d position, List<Integer> geneList) {
        return new AgingAnimal(position, geneList);
    }

    /**
     * Tworzenie zwierząt używając drugiego konstruktora
     * @param parent1 jeden z rodziców
     * @param parent2 drugi z rodziców
     * @param currentDay obecny dzień
     * @return nowy obiekt klasy AgingAnimal
     */
    @Override
    public Animal createAnimal(Animal parent1, Animal parent2, int currentDay) {
        return new AgingAnimal(parent1, parent2, currentDay);
    }
}
