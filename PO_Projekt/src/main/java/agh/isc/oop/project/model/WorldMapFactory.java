package agh.isc.oop.project.model;

public class WorldMapFactory {

    public static WorldMap createMap(MapType type, Vector2d mapSize, int initialGrassCount) {
        switch (type) {
            case EQUATOR_FOREST:
                return new EquatorForestMap(mapSize, initialGrassCount);
            case CRAWLING_JUNGLE:
                return new CrawlingJungleMap(mapSize, initialGrassCount);
            default:
                throw new IllegalArgumentException("Unsupported map type: " + type);
        }
    }

    public enum MapType {
        EQUATOR_FOREST,
        CRAWLING_JUNGLE
    }
}
