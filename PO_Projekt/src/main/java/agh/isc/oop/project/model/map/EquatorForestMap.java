package agh.isc.oop.project.model.map;


import agh.isc.oop.project.model.util.Vector2d;
import agh.isc.oop.project.simulation.SimulationConfig;

import java.util.Random;

/**
 * Klasa reprezentująca mapę w wariancie zalesione równiki.
 * Trawa rośnie najczęściej na równiku.
 * Uwaga: losowanie trawy nie gwarantuje postawienia
 * dokładnej ilości trawy. Losujemy pozycję, a jeśli jest zajęta,
 * to nie stawiamy tam trawy. Tym sposobem wzrost trawy jest
 * dużo bardziej naturalny i sam się balansuje.
 */
public class EquatorForestMap extends AbstractWorldMap {

    /**
     * Konstruktor wywołuje konstruktora AbstractWorldMap
     * oraz inicjalizuje trawę
     * @param config konfiguracja symulacji
     */
    public EquatorForestMap(SimulationConfig config) {
        super(config);
        initializeGrass(config.getStartGrassCount());
    }

    /**
     * Metoda odpowiadająca za postawienie startowej trawy,
     * tutaj jest identyczna jak metoda odpowiedzialna
     * za dzienny wzrost trawy
     * @param initialGrassSize ilość pól trawy na start
     */
    @Override
    public void initializeGrass(int initialGrassSize) {
        grassGrow(initialGrassSize);
    }

    /**
     * Metoda odpowiedzialna za dzienny wzrost trawy.
     * Losuje tyle pól, ile przekazano w parametrze daily growth i
     * jeżeli się da, to stawia tam trawę.
     * Około 80% trawy jest stawiane na równiku
     * @param dailyGrowth ilość trawy rosnącej jednego dnia
     */
    @Override
    public void grassGrow(int dailyGrowth){
        Random random = new Random();
        //Obliczenie zasięgu równika (około 20% mapy)
        int equatorStart = 2 * mapSize.getY() / 5;
        int equatorEnd = 3 * mapSize.getY() / 5 + 1;

        int equatorGrowth = (int) (dailyGrowth * 0.8);
        int otherGrowth = dailyGrowth - equatorGrowth;

        //Losowanie pozycji na równiku
        for (int i = 0; i < equatorGrowth; i++) {
            int x = random.nextInt(mapSize.getX());
            int y = equatorStart + random.nextInt(equatorEnd - equatorStart);
            Vector2d position = new Vector2d(x, y);
            super.putGrass(position);
        }

        //Losowanie pozycji poza równikiem
        for (int i = 0; i < otherGrowth; i++) {
            int x = random.nextInt(mapSize.getX());
            int y;
            do {
                y = random.nextInt(mapSize.getY());
            } while (y >= equatorStart && y < equatorEnd);

            Vector2d position = new Vector2d(x, y);
            super.putGrass(position);
        }
    }

}