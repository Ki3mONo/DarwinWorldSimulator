package agh.isc.oop.project.model.map;

import agh.isc.oop.project.model.util.Vector2d;
import agh.isc.oop.project.simulation.SimulationConfig;

import java.util.*;

public class CrawlingJungleMap extends AbstractWorldMap {
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
        int totalFields = mapSize.getX() * mapSize.getY();
        int maxCandidates = (int) (0.2 * totalFields);

        for (int i = 0; i < dailyGrowth; i++) {
            Vector2d newPlantPosition;

            if (!grassMap.isEmpty() && random.nextDouble() < 0.8) {
                Set<Vector2d> candidateSet = new HashSet<>();
                int attempts = 0;
                while (candidateSet.size() < maxCandidates && attempts < totalFields) {
                    List<Vector2d> existingPlants = new ArrayList<>(grassMap.keySet());
                    Vector2d basePlant = existingPlants.get(random.nextInt(existingPlants.size()));
                    int dx = random.nextInt(3) - 1;
                    int dy = random.nextInt(3) - 1;
                    if (dx == 0 && dy == 0) {
                        attempts++;
                        continue;
                    }
                    Vector2d candidate = adjustPosition(basePlant.add(new Vector2d(dx, dy)));
                    if (!grassMap.containsKey(candidate)) {
                        candidateSet.add(candidate);
                    }
                    attempts++;
                }
                if (!candidateSet.isEmpty()) {
                    List<Vector2d> candidateList = new ArrayList<>(candidateSet);
                    newPlantPosition = candidateList.get(random.nextInt(candidateList.size()));
                } else {
                    newPlantPosition = new Vector2d(random.nextInt(mapSize.getX()), random.nextInt(mapSize.getY()));
                }
            } else {
                newPlantPosition = new Vector2d(random.nextInt(mapSize.getX()), random.nextInt(mapSize.getY()));
            }

            if (!grassMap.containsKey(newPlantPosition)) {
                super.putGrass(newPlantPosition);
            }
        }
    }

}