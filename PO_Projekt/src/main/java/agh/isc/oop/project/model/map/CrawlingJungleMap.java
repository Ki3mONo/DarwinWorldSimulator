package agh.isc.oop.project.model.map;

import agh.isc.oop.project.model.util.Vector2d;
import agh.isc.oop.project.simulation.SimulationConfig;

import java.util.*;

public class CrawlingJungleMap extends AbstractWorldMap{
    private final Random random = new Random();


    public CrawlingJungleMap(SimulationConfig config) {
        super(config);
        initializeGrass(config.getStartGrassCount());
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
