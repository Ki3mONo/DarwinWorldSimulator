package agh.isc.oop.project.model;

public class WorldMapFactory {

    public static AbstractWorldMap createMap(MapType type, Vector2d mapSize, int initialGrassCount) {
        return switch (type) {
            case EQUATOR_FOREST -> new EquatorForestMap(mapSize, initialGrassCount);
            case CRAWLING_JUNGLE -> new CrawlingJungleMap(mapSize, initialGrassCount);
            default -> throw new IllegalArgumentException("Unsupported map type: " + type);
        };
    }


}
