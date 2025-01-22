package agh.isc.oop.project.model.util;

import agh.isc.oop.project.model.elements.WorldElement;

/**
 * Klasa reprezentująca element z wieloma zwierzętami z mapy symulacji.
 * Do celów pomocniczych wizualizacji.
 */
public class ManyAnimals implements WorldElement {

    @Override
    public Vector2d getPosition() {
        return null;
    }

    @Override
    public String getResourceName() {
        return "/animals/many_animals.png";
    }

}
