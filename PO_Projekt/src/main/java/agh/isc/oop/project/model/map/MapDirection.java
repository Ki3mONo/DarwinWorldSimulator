package agh.isc.oop.project.model.map;

import agh.isc.oop.project.model.util.Vector2d;

import java.util.Random;

public enum MapDirection {
    NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST;

    // Wszystko potrzebne do generowania losowych kierunków.
    // Takie rozwiązanie, żeby nie trzeba było używać values()
    // za każdym razem, gdy generujemy losowy kierunek.
    private static final MapDirection[] VALUES = MapDirection.values();
    private static final int LENGTH = VALUES.length;
    private static final Random rand = new Random();

    public static MapDirection getRandomDirection() {
        return VALUES[rand.nextInt(LENGTH)];
    }

    //Next — jedno przesunięcie o 45 stopni
    public MapDirection next(){
        // Stałe kierunków są zdefiniowane po kolei, zgodnie z kierunkiem
        // wskazówek zegara, więc next może zwracać następny kierunek
        // lub w przypadku ostatniego, pierwszy
        return VALUES[(this.ordinal() + 1)% LENGTH];
    }

    public MapDirection turnBy(int n){
        // Logika jak wyżej, tylko obracamy się n razy
        return VALUES[(this.ordinal() + n) % LENGTH];
    }

    public MapDirection reverse(){
        // Logika identyczna jak wyżej, tylko odwrotny kierunek
        // oznacza przesunięcie o połowę ilości kierunków
        return VALUES[(this.ordinal() + LENGTH / 2)% LENGTH];
    }

    //Przetłumaczenie kierunku, w jakim zwrócone jest zwierzę,
    //na wektor, o jaki ma się zmienić jego pozycja
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
