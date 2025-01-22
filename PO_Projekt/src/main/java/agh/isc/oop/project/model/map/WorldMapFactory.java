package agh.isc.oop.project.model.map;

import agh.isc.oop.project.simulation.SimulationConfig;

/**
 * Klasa realizująca wzorzec factory,
 * służy do tworzenia map.
 */
public class WorldMapFactory {
    /**
     * Stworzenie nowej mapy danego typu
     * @param type typ tworzonej mapy
     * @param config konfiguracja symulacji
     * @return nowa mapa
     */
    public static AbstractWorldMap createMap(MapType type, SimulationConfig config) {
        return switch (type) {
            case EQUATOR_FOREST -> new EquatorForestMap(config);
            case CRAWLING_JUNGLE -> new CrawlingJungleMap(config);
            default -> throw new IllegalArgumentException("Unsupported map type: " + type);
        };
    }


}
