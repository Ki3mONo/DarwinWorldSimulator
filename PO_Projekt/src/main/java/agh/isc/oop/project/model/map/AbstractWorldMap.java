package agh.isc.oop.project.model.map;

import agh.isc.oop.project.model.*;
import agh.isc.oop.project.model.elements.*;
import agh.isc.oop.project.model.util.MapChangeListener;
import agh.isc.oop.project.model.util.Vector2d;
import agh.isc.oop.project.simulation.SimulationConfig;

import java.util.*;

/**
 * Abstrakcyjna klasa reprezentująca mapę świata, która obsługuje
 * stawianie, ruszanie i usuwanie obiektów świata na mapie
 * oraz interakcje między tymi obiektami (jedzenie trawy, rozmnażanie).
 * Zawiera konfigurację symulacji, mapy zwierząt i traw
 * na ich pozycje na mapie oraz parametry opisujące symulację
 * w klasie SimulationConfig. Przyjmuje obserwatorów MapChangeListener,
 * którzy są powiadamiani w klasie Simulation po każdym
 * dniowym cyklu.
 * Klasy dziedziczące implementują metody odpowiedzialne za wzrost trawy,
 * który zależy od wybranego wariantu mapy.
 */

public abstract class AbstractWorldMap {
    protected UUID mapID;
    protected Vector2d mapSize;
    protected Vector2d leftBottomCorner;
    protected Vector2d rightUpperCorner;
    protected HashMap<Vector2d, List<Animal>> animals = new HashMap<>();
    protected HashMap<Vector2d, Grass> grassMap = new HashMap<>();
    protected HashMap<Vector2d, List<WorldElement>> worldElements = new HashMap<>();
    private final List<MapChangeListener> observing = new LinkedList<>();
    private final SimulationConfig config;
    private final AnimalFactory animalFactory;

    private Map<Vector2d, Integer> grassGrowthHistory = new HashMap<>();

    /**
     * Konstruktor mapy, który tworzy nową mapę na podstawie konfiguracji
     * @param config konfiguracja symulacji
     */
    public AbstractWorldMap(SimulationConfig config) {
        this.config = config;
        this.mapID = UUID.randomUUID();
        this.mapSize = new Vector2d(config.getMapWidth(), config.getMapHeight());
        this.leftBottomCorner = new Vector2d(0, 0);
        this.rightUpperCorner = new Vector2d(config.getMapWidth() - 1, config.getMapHeight() - 1);
        this.animalFactory = config.isAgingAnimalVariant() ? new AgingAnimalFactory() : new AnimalFactory();
    }

    /**
     * Metoda dodająca obserwatora mapy.
     * @param mapChangeListener dodawany obserwator
     */
    public void addObserver(MapChangeListener mapChangeListener) {
        observing.add(mapChangeListener);
    }

    /**
     * Metoda usuwająca obserwatora mapy.
     * @param mapChangeListener obserwator do usunięcia
     */
    public void removeObserver(MapChangeListener mapChangeListener) {
        observing.remove(mapChangeListener);
    }

    /**
     * Metoda, która powiadamia wszystkich obserwatorów o zmianie mapy.
     */
    public void mapChanged() {
        for (MapChangeListener mapChangeListener : observing) {
            mapChangeListener.mapChanged(this);
        }
    }

    /**
     * Metoda zwracająca hashmapę zwierząt, która
     * mapuje listy zwierząt z danej pozycji, na tę pozycję.
     * @return Hashmapa zwierząt na mapie
     */
    public Map<Vector2d, List<Animal>> getAnimals() {
        return animals;
    }

    /**
     * Metoda usuwająca z mapy wszystkie zwierzęta podane
     * w przyjmowanej liście.
     * @param animalsToRemove lista zwierząt do usunięcia
     */
    public void removeAnimals(List<Animal> animalsToRemove) {
        for (Animal animal : animalsToRemove) {
            Vector2d position = animal.getPosition();
            this.animals.get(position).remove(animal);
            this.worldElements.get(position).remove(animal);
        }
    }

    /**
     * Metoda zwracająca hashmapę elementów (zwierząt i traw), która
     * mapuje listy elementów świata z danej pozycji, na tę pozycję.
     * @return Hashmapa elementów świata na mapie
     */
    public HashMap<Vector2d, List<WorldElement>> getWorldElements() {
        return worldElements;
    }

    /**
     * Metoda zwracająca identyfikator mapy.
     * @return ID mapy
     */
    public UUID getID() {
        return mapID;
    }

    /**
     * Metoda sprawdzająca, czy dana pozycja jest w granicach mapy.
     * @param position pozycja do sprawdzenia
     * @return True, jeśli pozycja jest w granicach mapy, w przeciwnym wypadku false
     */
    protected boolean inBounds(Vector2d position) {
        return this.leftBottomCorner.precedes(position) && this.rightUpperCorner.follows(position);
    }

    /**
     * Metoda do implementacji w konkretnych mapach,
     * która odpowiada za dodanie startowej trawy.
     * @param initialGrassSize ilość pól trawy na start
     */
    public abstract void initializeGrass(int initialGrassSize);

    /**
     * Metoda do implementacji w konkretnych mapach,
     * która odpowiada za dzienny wzrost trawy.
     * @param dailyGrowth ilość trawy rosnącej jednego dnia
     */
    public abstract void grassGrow(int dailyGrowth);

    /**
     * Metoda sprawdzająca, czy zwierzak może się ruszyć na podane pole
     * @param position pozycja do sprawdzenia
     * @return True lub False, w zależności od tego, czy zwierzak może się tam ruszyć
     */
    public boolean canMoveTo(Vector2d position) {
        return inBounds(position);
    }

    /**
     * Metoda dodająca nowego zwierzaka do mapy (początkowe zwierzaki
     * oraz te urodzone w trakcie symulacji). Jeżeli zwierze można
     * postawić na mapie (jego pole jest w granicach mapy),
     * to zapisuje je do hashmap zwierząt i elementów,
     * w przeciwnym wypadku rzuca IncorrectPositionException
     * @param animal zwierzak do postawienia
     * @throws IncorrectPositionException wyjątek informujący o
     * próbie postawienia zwierzaka poza mapą
     */
    public void place(Animal animal) throws IncorrectPositionException {
        Vector2d position = animal.getPosition();
        if (!inBounds(position)) {
            throw new IncorrectPositionException(position);
        }
        animals.computeIfAbsent(position, k -> new ArrayList<>()).add(animal);
        worldElements.computeIfAbsent(position, k -> new ArrayList<>()).add(animal);
    }

    /**
     * Metoda usuwająca trawę z podanej pozycji.
     * Jeżeli nie ma tam trawy, to niczego nie robi.
     * @param grassPosition pozycja, z której usuwa trawę
     */
    public void removeGrass(Vector2d grassPosition) {
        Grass grass = grassMap.remove(grassPosition);
        worldElements.get(grassPosition).remove(grass);
    }

    /**
     * Metoda przesuwająca zwierzaki na mapie.
     * Wyciąga zwierzaka z hashmap oraz stawia ponownie,
     * na nowej pozycji, obliczonej przez metodę move
     * z klasy Animal.
     * @param animal zwierzak do przesunięcia
     */
    public void move(Animal animal) {
        //Stara pozycja zwierzaka
        Vector2d oldPosition = animal.getPosition();
        //Wyciągnięcie zwierzaka z obu hashmap
        animals.get(oldPosition).remove(animal);
        worldElements.getOrDefault(oldPosition, new ArrayList<>()).remove(animal);

        //Obliczenie nowej pozycji
        Vector2d newPosition = animal.move(this);
        //Ustawienie zwierzaka na nowej pozycji
        animals.computeIfAbsent(newPosition, k -> new ArrayList<>()).add(animal);
        worldElements.computeIfAbsent(newPosition, k -> new ArrayList<>()).add(animal);
    }

    /**
     * Metoda zwracająca listę elementów świata na podanej pozycji.
     * @param position pozycja, z której chcemy pozyskać elementy
     * @return Optional z listą elementów lub pusty,
     * jeśli na podanej pozycji niczego nie ma.
     */
    public Optional<List<WorldElement>> objectAt(Vector2d position) {
        return Optional.ofNullable(worldElements.get(position));
    }

    /**
     * Metoda zwracająca trawę na podanej pozycji.
     * @param grassPosition pozycja, z której chcemy pozyskać trawę
     * @return Optional z trawą lub pusty,
     * jeśli na podanej pozycji nie ma trawy.
     */
    public Optional<Grass> getGrassAt(Vector2d grassPosition) {
        return Optional.ofNullable(grassMap.get(grassPosition));
    }

    /**
     * Metoda obsługująca etap konsumpcji trawy.
     * Dla każdej pozycji trawy na mapie znajduje
     * zwierzaka, któremu należy się trawa, zgodnie z treścią:
     * najsilniejszy, jeśli jest remis to najstarszy, jeśli
     * jest remis to o największej liczbie dzieci, jeśli
     * dalej jest remis to losowy.
     * Zwycięski zwierzak zjada trawę, a na końcu
     * wszystkie zjedzone trawy są usuwany
     */
    public void handleEating() {
        List<Vector2d> grassEaten = new ArrayList<>();

        for (Vector2d position : new ArrayList<>(grassMap.keySet())) {

            List<Animal> animalsHere = animals.get(position);
            if (animalsHere == null || animalsHere.isEmpty()) {
                continue; // Jeśli na tym polu nie ma zwierząt, pomijamy je
            }
            //Energia najsilniejszego zwierzaka na obecnym polu
            int maxEnergy = animalsHere.stream()
                    .mapToInt(Animal::getEnergy)
                    .max()
                    .orElse(0);

            //Odfiltrowanie najsilniejszych zwierzaków
            List<Animal> strongestAnimals = animalsHere.stream()
                    .filter(a -> a.getEnergy() == maxEnergy)
                    .toList();

            //Dzień urodzenia najstarszego zwierzaka spośród najsilniejszych
            int oldestBirthDate = strongestAnimals.stream()
                    .mapToInt(Animal::getBirthDate)
                    .min()
                    .orElse(Integer.MAX_VALUE);

            //Odfiltrowanie najstarszych zwierzaków
            List<Animal> oldestAnimals = strongestAnimals.stream()
                    .filter(a -> a.getBirthDate() == oldestBirthDate)
                    .toList();

            //Maksymalna liczba dzieci najsilniejszych i najstarszych zwierzaków
            int maxChildren = oldestAnimals.stream()
                    .mapToInt(Animal::getChildrenCount)
                    .max()
                    .orElse(0);

            //Odfiltrowanie zwierzaków o największej liczbie dzieci
            List<Animal> topAnimals = oldestAnimals.stream()
                    .filter(a -> a.getChildrenCount() == maxChildren)
                    .toList();

            //Wybranie losowo zwierzaka, spośród wszystkich odfiltrowanych
            Animal winner = topAnimals.get(new Random().nextInt(topAnimals.size()));

            //Zwycięski zwierzak zjada trawę
            winner.eat();
            //Zapisanie trawy z tej pozycji jako zjedzonej
            grassEaten.add(position);
        }
        //Usunięcie zjedzonej trawy, poza pętlą
        //aby uniknąć concurrent modification exception
        grassEaten.forEach(this::removeGrass);
    }

    /**
     * Metoda obsługująca reprodukcję zwierzaków.
     * Dla każdej pozycji, na której mogą być zwierzaki
     * (czyli keySet z mapy animals) sortuje wszystkie zwierzaki
     * w kolejności rozmnażania sprecyzowanej w treści zadania,
     * potem bierze kolejne pary zwierzaków z tej listy,
     * tworzy ich potomka i dodaje go do mapy na obecnej pozycji.
     * @param currentDay obecny dzień, potrzebny do ustawienia dnia urodzenia dziecka
     * @return Lista wszystkich zwierzaków urodzonych podczas tego cyklu reprodukcji
     */
    public List<Animal> handleReproduction(int currentDay) {
        List<Animal> bornAnimals = new ArrayList<>();
        //Dla każdej pozycji z keySet
        animals.keySet().forEach(position -> {
            //Sortowanie zwierzaków w kolejności do reprodukcji
            List<Animal> candidates = animals.get(position).stream()
                    .filter(a -> a.getEnergy() >= config.getReproductionEnergy())
                    .sorted(Comparator.comparingInt(Animal::getEnergy).reversed()
                            .thenComparing(Animal::getBirthDate)
                            .thenComparing(Animal::getChildrenCount).reversed())
                    .toList();

            for (int i = 0; i + 1 < candidates.size(); i += 2) {
                //Wybranie pary rodziców
                Animal parent1 = candidates.get(i);
                Animal parent2 = candidates.get(i + 1);

                //Stworzenie dziecka wybranych rodziców
                Animal child = animalFactory.createAnimal(parent1, parent2, currentDay);

                //Ustawienie dziecka na mapie
                try {
                    place(child);
                    bornAnimals.add(child);
                } catch (IncorrectPositionException e) {
                    System.err.println("Failed to place offspring: " + e.getMessage());
                }

                //Rodzice tracą energię po reprodukcji
                parent1.loseReproductionEnergy();
                parent2.loseReproductionEnergy();

                //Dziecko jest dodawane do listy dzieci obu rodziców
                parent1.addChild(child);
                parent2.addChild(child);
            }
        });
        return bornAnimals;
    }

    /**
     * Metoda zwracająca ilość trawy obecnie na mapie
     * @return rozmiar hashmapy z trawą
     */
    public int getGrassCount() {
        return grassMap.size();
    }

    /**
     * Metoda dodająca nową trawę do mapy na podanej pozycji.
     * Jeżeli na wybranej pozycji jest już trawa, to niczego nie robi.
     * @param position pozycja, na której wstawiamy trawę
     */
    void putGrass(Vector2d position) {
        if (!grassMap.containsKey(position)) { // Jeśli pole nie było zajęte → trawa faktycznie rośnie
            grassGrowthHistory.put(position, grassGrowthHistory.getOrDefault(position, 0) + 1);
            Grass grass = new Grass(position);
            worldElements.computeIfAbsent(position, k -> new ArrayList<>()).add(grass);
            grassMap.put(position, grass);
        }
    }

    /**
     * Metoda zwracająca mapę preferowanych pól trawy, czyli takich, które znajdują się w
     * top 20% pod względem wartości wzrostu. Preferowane pola to te, które mają wartość
     * wzrostu równą lub wyższą od wyliczonego progu.
     * @return Map<Vector2d, Integer> - mapa preferowanych pól trawy
     */
    public Map<Vector2d, Integer> getPreferredGrassFields() {
        // Mapa, która będzie przechowywać pola trawy uznane za preferowane.
        Map<Vector2d, Integer> preferredFields = new HashMap<>();

        //Kopiujemy historię wzrostu trawy
        Map<Vector2d, Integer> allGrassGrowthHistory = new HashMap<>(grassGrowthHistory);

        //Tworzymy listę wzrostów trawy
        List <Integer> growths = new ArrayList<>(allGrassGrowthHistory.values());

        //Jeśli nie ma wzrostów, zwracamy pustą mapę
        if (growths.isEmpty()) {
            return preferredFields;
        }

        //Sortujemy wzrosty trawy od największego do najmniejszego, aby wybrać top 20%
        growths.sort(Collections.reverseOrder());


        int count = growths.size();
        //Obliczamy indeks progu top 20% wzrostu trawy
        //Jeśli jest mniej niż 5 wzrostów, to bierzemy 1, jeśli więcej, to 20% z liczby wszystkich wzrostów
        int thresholdIndex = Math.min(count - 1, (int) Math.floor(count * 0.2));

        // Pobieramy wartość wzrostu odpowiadającą obliczonemu indeksowi
        int thresholdValue = growths.get(thresholdIndex);

        //Dodajemy do mapy preferowanych pól trawy te, które mają wzrost powyżej progu lub równy
        for (Map.Entry<Vector2d, Integer> entry : allGrassGrowthHistory.entrySet()) {
            if (entry.getValue() >= thresholdValue) {
                preferredFields.put(entry.getKey(), entry.getValue());
            }
        }
        //Zwracamy mapę preferowanych pól trawy
        return preferredFields;
    }
}
