package agh.isc.oop.project.model;

import agh.isc.oop.project.simulation.SimulationConfig;

public class WorldMapFactory {

    public static AbstractWorldMap createMap(MapType type, SimulationConfig config) {
        return switch (type) {
            case EQUATOR_FOREST -> new EquatorForestMap(config);
            case CRAWLING_JUNGLE -> new CrawlingJungleMap(config);
            default -> throw new IllegalArgumentException("Unsupported map type: " + type);
        };
    }


}
