package agh.isc.oop.project.model.map;

import agh.isc.oop.project.model.util.Vector2d;

import java.util.Random;

/**
 * Enum reprezentujący możliwe kierunki, w których zwierzak jest obrócony
 */
public enum MapDirection {
    NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST;

    // Wszystko potrzebne do generowania losowych kierunków.
    // Takie rozwiązanie, żeby nie trzeba było używać values()
    // za każdym razem, gdy generujemy losowy kierunek.
    private static final MapDirection[] VALUES = MapDirection.values();
    private static final int LENGTH = VALUES.length;
    private static final Random rand = new Random();

    /**
     * Metoda zwracająca losowy kierunek
     * @return losowy kierunek
     */
    public static MapDirection getRandomDirection() {
        return VALUES[rand.nextInt(LENGTH)];
    }

    /**
     * Metoda wykonująca n obrotów o 45 stopni w prawo
     * i zwracająca wynikową orientację
     * @param n liczba obrotów
     * @return orientacja po obrocie
     */
    public MapDirection turnBy(int n){
        // Logika jak wyżej, tylko obracamy się n razy
        return VALUES[(this.ordinal() + n) % LENGTH];
    }

    /**
     * Metoda zwracająca odwrotny kierunek
     * @return odwrotny kierunek
     */
    public MapDirection reverse(){
        // Logika identyczna jak wyżej, tylko odwrotny kierunek
        // oznacza przesunięcie o połowę ilości kierunków
        return VALUES[(this.ordinal() + LENGTH / 2)% LENGTH];
    }

    /**
     * Przetłumaczenie orientacji na jednostkowy wektor,
     * o jaki przesunie się zwierze, wykonując krok do przodu i
     * będąc obrócone w danym kierunku
     * @return jednostkowy wektor odpowiadający danej orientacji
     */
    public Vector2d toUnitVector(){
        return switch (this) {
            case NORTH -> new Vector2d(0, 1);
            case NORTHEAST -> new Vector2d(1, 1);
            case EAST -> new Vector2d(1, 0);
            case SOUTHEAST -> new Vector2d(1, -1);
            case SOUTH -> new Vector2d(0, -1);
            case SOUTHWEST -> new Vector2d(-1, -1);
            case WEST -> new Vector2d(-1, 0);
            case NORTHWEST -> new Vector2d(-1, 1);
        };
    }
}
