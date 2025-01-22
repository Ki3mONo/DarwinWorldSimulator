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
    @Override
    public void grassGrow(int dailyGrowth) {
        // ograniczenie liczby kandydatów na preferowane miejsce wzrostu trawy
        int totalFields = mapSize.getX() * mapSize.getY();
        int maxCandidates = (int) (0.2 * totalFields);

        // dla każdego wzrostu trawy
        for (int i = 0; i < dailyGrowth; i++) {
            // losowanie pozycji dla nowej trawy
            Vector2d newPlantPosition;

            // jeśli na mapie są jakieś trawy, żeby było obok czego losować
            //          z dużym prawdopodobieństwem (80%) losujemy w pobliżu innej trawy
            if (!grassMap.isEmpty() && random.nextDouble() < 0.8) {
                // zbior kandydatów na nową trawę
                Set<Vector2d> candidateSet = new HashSet<>();
                // próby znalezienia kandydata
                int attempts = 0;
                // dopóki nie mamy wystarczającej liczby kandydatów lub nie sprawdziliśmy wszystkich pól
                while (candidateSet.size() < maxCandidates && attempts < totalFields) {
                    // losujemy istniejącą trawę
                    List<Vector2d> existingPlants = new ArrayList<>(grassMap.keySet());
                    Vector2d basePlant = existingPlants.get(random.nextInt(existingPlants.size()));
                    //kwadrat dookoła rośliny basePlant
                    int dx = random.nextInt(3) - 1;
                    int dy = random.nextInt(3) - 1;
                    //jeżeli dx i dy są równe 0 to znaczy że losujemy tą samą pozycję, więc nie dodajemy jej do kandydatów
                    if (dx == 0 && dy == 0) {
                        attempts++;
                        continue;
                    }
                    // losujemy pozycję w kwadracie dookoła rośliny z uwzględnieniem poprawienia pozycji
                    Vector2d candidate = adjustPosition(basePlant.add(new Vector2d(dx, dy)));

                    // jeśli miejsce kandydata jest wolne, to dodajemy go do zbioru kandydatów
                    if (!grassMap.containsKey(candidate)) {
                        // dodajemy kandydata do zbioru kandydatów
                        candidateSet.add(candidate);
                    }
                    // zwiększamy liczbę prób
                    attempts++;
                }
                // jeśli udało się znaleźć kandydatów, to losujemy ze zbioru kandydatów
                if (!candidateSet.isEmpty()) {
                    // losujemy z kandydatów
                    List<Vector2d> candidateList = new ArrayList<>(candidateSet);
                    newPlantPosition = candidateList.get(random.nextInt(candidateList.size()));
                } else {
                    // jeśli nie udało się znaleźć kandydata, to losujemy w dowolnym miejscu
                    newPlantPosition = new Vector2d(random.nextInt(mapSize.getX()), random.nextInt(mapSize.getY()));
                }
            } else {
                // jeśli nie ma traw, to losujemy w dowolnym miejscu lub są, ale wylosowaliśmy 20% szansy
                newPlantPosition = new Vector2d(random.nextInt(mapSize.getX()), random.nextInt(mapSize.getY()));
            }
            // stawiamy trawę na wylosowanej pozycji
            super.putGrass(newPlantPosition);

        }
    }

    /**
     * Metoda poprawiająca pozycje poza mapą,
     * dla współrzędnych x zwraca współrzędną na drugim końcu mapy
     * (zapętlanie lewej i prawej krawędzi mapy),
     * a dla y zwraca pozycję przy krawędzi, za którą pozycja wychodzi.
     * Jest używana do obliczania pól sąsiadujących z daną rośliną.
     * @param position pozycja do poprawienia
     * @return poprawiona pozycja
     */
    public Vector2d adjustPosition(Vector2d position) {
        int x = (position.getX() + mapSize.getX()) % mapSize.getX();
        int y = position.getY();
        if (y < 0 || y >= mapSize.getY()) {
            y = Math.max(0, Math.min(y, mapSize.getY() - 1));
        }
        return new Vector2d(x, y);
    }

}