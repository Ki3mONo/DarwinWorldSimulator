package agh.isc.oop.project.model.map;

import agh.isc.oop.project.model.util.Vector2d;
import agh.isc.oop.project.simulation.SimulationConfig;

import java.util.*;

/**
 * Klasa reprezentująca mapę w wariancie pełzającej dżungli.
 * Trawa rośnie najczęściej w pobliżu innych traw.
 * Uwaga: losowanie trawy nie gwarantuje postawienia
 * dokładnej ilości trawy. Losujemy pozycję, a jeśli jest zajęta,
 * to nie stawiamy tam trawy. Tym sposobem wzrost trawy jest
 * dużo bardziej naturalny i sam się balansuje.
 */
public class CrawlingJungleMap extends AbstractWorldMap {
    private final Random random = new Random();

    /**
     * Konstruktor wywołuje konstruktora AbstractWorldMap
     * oraz inicjalizuje trawę
     * @param config konfiguracja symulacji
     */
    public CrawlingJungleMap(SimulationConfig config) {
        super(config);
        initializeGrass(config.getStartGrassCount());
    }

    /**
     * Metoda stawiająca początkowe trawy
     * @param initialGrassSize ilość trawy do postawienia
     */
    @Override
    public void initializeGrass(int initialGrassSize) {
        for (int i = 0; i < initialGrassSize; i++) {
            Vector2d position = new Vector2d(random.nextInt(mapSize.getX()), random.nextInt(mapSize.getY()));
            super.putGrass(position);
        }
    }

    /**
     * Metoda odpowiadająca za dzienny wzrost trawy
     * @param dailyGrowth ilość trawy rosnącej jednego dnia
     */
    //TODO - weź dopisz komentarze do środka bo ja wciąż nie bardzo rozumiem jak to działa XD
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