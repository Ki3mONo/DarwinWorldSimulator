package agh.isc.oop.project.model.elements;

import agh.isc.oop.project.model.util.Vector2d;

/**
 * Klasa reprezentująca trawę na mapie.
 * Przechowuje swoją pozycję oraz
 * implementuje interfejs WorldElement.
 */
public class Grass implements WorldElement {
    private final Vector2d position;

    public Grass(Vector2d position) {
        this.position = position;
    }

    /**
     * Implementuje metodę z interfejsu WorldElement,
     * zwraca pozycję trawy.
     * @return pozycja trawy
     */
    @Override
    public Vector2d getPosition() {
        return position;
    }

    /**
     * Implementuje metodę z interfejsu WorldElement,
     * zwraca ścieżkę do ikony trawy z resources
     * @return ścieżka do ikony trawy
     */
    @Override
    public String getResourceName() {
        return "/world/grass.png";
    }
}
