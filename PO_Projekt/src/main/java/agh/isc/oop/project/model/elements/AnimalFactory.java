package agh.isc.oop.project.model.elements;

import agh.isc.oop.project.model.util.Vector2d;

import java.util.List;

/**
 * Klasa realizująca wzorzec abstract factory.
 * Służy do tworzenia obiektów Animal.
 * Dziedziczy po niej klasa AgingAnimalFactory
 */
public class AnimalFactory {
    /**
     * Tworzenie zwierząt używając pierwszego konstruktora
     * @param position startowa pozycja
     * @param geneList list genów
     * @return nowy obiekt klasy Animal
     */
    public Animal createAnimal(Vector2d position, List<Integer> geneList) {
        return new Animal(position, geneList);
    }

    /**
     * Tworzenie zwierząt używając drugiego konstruktora
     * @param parent1 jeden z rodziców
     * @param parent2 drugi z rodziców
     * @param currentDay obecny dzień
     * @return nowy obiekt klasy Animal
     */
    public Animal createAnimal(Animal parent1, Animal parent2, int currentDay) {
        return new Animal(parent1, parent2, currentDay);
    }

}
