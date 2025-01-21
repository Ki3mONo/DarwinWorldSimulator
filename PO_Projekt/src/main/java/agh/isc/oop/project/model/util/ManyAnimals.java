package agh.isc.oop.project.model.util;

import agh.isc.oop.project.model.elements.WorldElement;

public class ManyAnimals implements WorldElement {
    long size;
    public ManyAnimals(long size){
        this.size = size;
    }
    @Override
    public Vector2d getPosition() {
        // Pozycja nie ma znaczenia – używamy domyślnej wartości
        return new Vector2d(0, 0);
    }

    @Override
    public String getResourceName() {
        return "/animals/many_animals.png";
    }

    @Override
    public String toString() {
        return "M";
    }

    public long getSize() {
        return size;
    }
}
