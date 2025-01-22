package agh.isc.oop.project.model.elements;

import agh.isc.oop.project.model.util.Vector2d;

/**
 * Interfejs dla elementów świata, czyli trawy i zwierzaków
 */
public interface WorldElement {
    /**
     * Zwraca pozycję elementu
     * @return pozycja elementu
     */
    Vector2d getPosition();

    /**
     * Zwraca ścieżkę do ikony elementu w resources
     * @return ścieżka do ikony elementu
     */
    String getResourceName();
}
