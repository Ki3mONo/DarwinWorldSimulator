package agh.isc.oop.project.model.elements;

import agh.isc.oop.project.model.util.Vector2d;

import java.util.List;

public class AnimalFactory {
    //Generowanie startowych zwierząt
    public Animal createAnimal(Vector2d position, List<Integer> geneList) {
        return new Animal(position, geneList);
    }

    //Tworzenie urodzonych zwierząt
    public Animal createAnimal(Animal parent1, Animal parent2, int currentDay) {
        return new Animal(parent1, parent2, currentDay);
    }

}
