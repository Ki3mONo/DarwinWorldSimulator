package agh.isc.oop.project.model.elements;

import agh.isc.oop.project.model.util.Vector2d;

import java.util.List;

public class AgingAnimalFactory extends AnimalFactory {
    //Generowanie startowych zwierząt
    @Override
    public Animal createAnimal(Vector2d position, List<Integer> geneList) {
        return new AgingAnimal(position, geneList);
    }

    //Tworzenie urodzonych zwierząt
    @Override
    public Animal createAnimal(Animal parent1, Animal parent2, int currentDay) {
        return new AgingAnimal(parent1, parent2, currentDay);
    }
}
