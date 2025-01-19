package agh.isc.oop.project.model;

import java.util.*;

public class CrawlingJungleMap extends AbstractWorldMap{
    private final Random random = new Random();


    public CrawlingJungleMap(Vector2d mapSize, int initialGrassCount) {
        super(mapSize);
        this.mapID = UUID.randomUUID();
        this.leftBottomCorner = new Vector2d(0, 0);
        this.rightUpperCorner = new Vector2d(mapSize.getX() - 1, mapSize.getY() - 1);
        initializeGrass(initialGrassCount);
    }


    @Override
    public void initializeGrass(int initialGrassSize) {
        for (int i = 0; i < initialGrassSize; i++) {
            Vector2d position = new Vector2d(random.nextInt(mapSize.getX()), random.nextInt(mapSize.getY()));
            super.putGrass(position);
        }
    }

    @Override
    public void grassGrow(int dailyGrowth) {
        List<Vector2d> currentPlantPositions = new ArrayList<>(grassMap.keySet());
        for (int i = 0; i < dailyGrowth; i++) {
            Vector2d newPlantPosition;

            if (!currentPlantPositions.isEmpty() && random.nextDouble() < 0.8) {
                Vector2d basePlant = currentPlantPositions.get(random.nextInt(currentPlantPositions.size()));
                int dx = random.nextInt(3) - 1;
                int dy = random.nextInt(3) - 1;
                newPlantPosition = adjustPosition(basePlant.add(new Vector2d(dx, dy)));
            } else {
                newPlantPosition = new Vector2d(random.nextInt(mapSize.getX()), random.nextInt(mapSize.getY()));
            }

            if (inBounds(newPlantPosition) && !grassMap.containsKey(newPlantPosition)) {
                super.putGrass(newPlantPosition);
            }
        }
    }
}
