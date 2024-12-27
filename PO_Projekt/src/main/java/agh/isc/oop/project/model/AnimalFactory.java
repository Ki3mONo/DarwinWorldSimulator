package agh.isc.oop.project.model;

import java.util.List;

public class AnimalFactory {
    //Generowanie startowych zwierząt
    public Animal generateAnimal(Vector2d position, List<Integer> geneList) {
        return new Animal(position, geneList);
    }

    //Tworzenie urodzonych zwierząt
    public Animal createAnimal(Animal parent1, Animal parent2) {
        return new Animal(parent1, parent2);
    }

}
