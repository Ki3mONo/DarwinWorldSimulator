package agh.isc.oop.project.model.util;

import agh.isc.oop.project.model.map.AbstractWorldMap;

/**
 * Interfejs dla obserwatorów mapy,
 * realizacja wzorca obserwator.
 */
public interface MapChangeListener {
    /**
     * Metoda, która jest reakcją na powiadomienie o zmianie mapy
     * @param worldMap obserwowana mapa
     */
    void mapChanged(AbstractWorldMap worldMap);
}
