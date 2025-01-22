package agh.isc.oop.project.model.elements;

import agh.isc.oop.project.model.util.AnimalIcons;
import agh.isc.oop.project.model.util.Vector2d;
import agh.isc.oop.project.model.map.AbstractWorldMap;
import agh.isc.oop.project.model.map.MapDirection;
import agh.isc.oop.project.simulation.SimulationConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Klasa reprezentująca pojedynczego zwierzaka.
 * Zawiera informacje o jego pozycji, energii i genomie
 * oraz wszystkie informacje potrzebne do obliczania
 * statystyk tego zwierzaka, jeżeli użytkownik zechce
 * go śledzić.
 * Przyjmuje również konfigurację jako atrybut statyczny.
 */
public class Animal implements WorldElement {
    protected static SimulationConfig config;

    /**
     * Metoda do ustawienia konfiguracji dla całej klasy.
     * @param config konfiguracja do ustawienia
     */
    public static void setConfig(SimulationConfig config) {
        Animal.config = config;
    }

    private Vector2d position;
    private MapDirection orientation;
    protected int energy;

    boolean alive = true;
    private final Genome genome;
    private final int birthDate;
    private int deathDay = -1; // -1 oznacza, że zwierzę jeszcze nie zmarło
    private int grassEaten = 0; // Liczba zjedzonych roślin

    //Ikona danego zwierzaka (dla urozmaicenia jest kilka różnych)
    private final AnimalIcons cachedAnimalIcon;

    private List<Animal> children = new ArrayList<>();

    /**
     * Konstruktor, który służy do tworzenia startowych zwierzaków.
     * Przyjmuje pozycję zwierzaka oraz listę jego genów
     * @param position startowa pozycja zwierzaka
     * @param geneList lista genów zwierzaka
     */
    public Animal(Vector2d position, List<Integer> geneList) {
        this.position = position;
        //Kierunek, w którym zwierzak jest zwrócony ma być losowy
        this.orientation = MapDirection.getRandomDirection();
        //Stworzenie genomu z podanej listy genów
        this.genome = new Genome(geneList);
        //Ustawienie startowej energii
        this.energy = config.getInitialEnergy();
        //Data urodzenia startowego zwierzaka to zawsze dzień 0
        this.birthDate = 0;
        //Zapisanie ikony tego zwierzaka
        this.cachedAnimalIcon = computeAnimalIcon();
    }

    /**
     * Konstruktor, który służy do tworzenia zwierząt urodzonych
     * w toku symulacji.
     * @param parent1 jeden z rodziców
     * @param parent2 drugi z rodziców
     * @param currentDay obecny dzień
     */
    public Animal(Animal parent1, Animal parent2, int currentDay) {
        //Pozycja zwierzaka tak sama, jak rodziców
        this.position = new Vector2d(parent1.position.getX(), parent1.position.getY());
        //Orientacja zawsze losowa
        this.orientation = MapDirection.getRandomDirection();
        //Stworzenie genomu na podstawie genomów obu rodziców
        this.genome = new Genome(parent1, parent2);
        //Energia dziecka to energia stracona przez obu rodziców podczas reprodukcji
        this.energy = 2 * config.getReproductionCost();
        //Data urodzenia to obecny dzień
        this.birthDate = currentDay;
        //Wylosowanie i zapisanie ikony dla tego zwierzaka
        this.cachedAnimalIcon = computeAnimalIcon();
    }

    /**
     * Zwraca energię zwierzaka
     * @return energia zwierzaka
     */
    public int getEnergy() {
        return energy;
    }

    /**
     * Zwraca genom zwierzaka.
     * @return genom zwierzaka
     */
    public Genome getGenome() {
        return genome;
    }

    /**
     * Implementuje metodę z interfejsu WorldElement,
     * zwraca pozycję zwierzaka.
     * @return pozycja zwierzaka
     */
    @Override
    public Vector2d getPosition() {
        return position;
    }

    /**
     * Metoda odpowiadająca za obliczenie nowej pozycji zwierzaka przy ruchu.
     * Przyjmuje mapę, aby sprawdzić, czy może się przesunąć
     * na oblicząną pozycję. Jeżeli nie, to nie zmienia pozycji, a
     * jedynie się obraca.
     * @param map mapa, na której znajduje się zwierzak
     * @return pozycja, na którą przeszedł zwierzak lub
     * stara, jeśli tylko się obrócił
     */
    public Vector2d move(AbstractWorldMap map) {
        if (!isAlive()) {
            return position; // Jeśli zwierzę nie żyje, nie może się poruszać
        }
        //Zużycie energii na ruch
        energy -= config.getMoveCost();

        //Obrót zgodnie z aktywnym genem
        orientation = orientation.turnBy(genome.getActiveGene());

        //Aktywacja kolejnego genu
        genome.updateCurrentGeneIndex();

        //Obliczenie nowej pozycji
        Vector2d newPosition = position.add(orientation.toUnitVector());

        //Sprawdzenie, czy zwierzak przejdzie przez zalętloną krawędź mapy
        if (newPosition.getX() == -1 || newPosition.getX() == config.getMapWidth()) {
            newPosition = adjustPosition(newPosition);
        }

        //Jeżeli da się przejść na tę pozycję, czyli obie współrzędne są w granicach mapy
        if (map.canMoveTo(newPosition)) {
            position = newPosition; //zmień pozycję na nową
        } else {
            orientation = orientation.reverse();    //obróć się w przeciwną stronę
        }

        return position;    // zwróć aktualną pozycję zwierzaka po ruchu
    }

    /**
     * Metoda odpowiadająca za przeliczanie pozycji
     * za prawą lub lewą krawędzią mapy na pozycję na
     * odpowiednio lewej lub prawej krawędzi
     * @param position pozycja ze współrzędną x poza granicami mapy
     * @return pozycja z pozycją x w granicach mapy (y dalej może być poza)
     */
    private Vector2d adjustPosition(Vector2d position) {
        int x = position.getX();
        if (x == -1)
            x = config.getMapWidth() - 1;
        else if (x == config.getMapWidth())
            x = 0;
        return new Vector2d(x, position.getY());
    }

    /**
     * Zwraca dzień urodzenia zwierzaka
     * @return dzień urodzenia
     */
    public int getBirthDate() {
        return birthDate;
    }

    /**
     * Obsługuje proces jedzenia z perspektywy tylko zwierzaka.
     * Dodaje energię oraz zwiększa licznik zjedzonej trawy
     */
    public void eat() {
        energy += config.getGrassEnergy();
        grassEaten++;
    }

    /**
     * Obsługuje stratę energii zwierzaka na reprodukcję
     */
    public void loseReproductionEnergy() {
        energy -= config.getReproductionCost();
    }

    /**
     * Zwraca energię, przy której pasek zdrowia zwierzaka
     * będzie w pełni wypełniony.
     * UWAGA! - Nie ma narzuconego ograniczenia na energię zwierzaka,
     * ta metoda służy jedynie do dostosowania pasków zdrowia.
     * @return Energia, przy której pasek zdrowia będzie w pełni wypełniony,
     * arbitralnie ustawiona na podwojoną energię startową
     */
    public int getMaxEnergy() {
        return 2*config.getInitialEnergy();
    }

    /**
     * Zwraca ścieżkę do ikony zwierzaka
     * @return ścieżka do ikony zwierzaka
     */
    @Override
    public String getResourceName() {
        return cachedAnimalIcon.getRecourseName();
    }

    /**
     * Wylosowanie obrazka do przedstawiania tego zwierzaka
     * @return obrazek wylosowany spośród dostępnych
     */
    private AnimalIcons computeAnimalIcon() {
        int mod = genome.genomeSum() % 20;
        return switch (mod) {
            case 1, 2, 3 -> AnimalIcons.BOCIAN;
            case 4, 5, 6 -> AnimalIcons.KURCZAK;
            case 7, 8, 9 -> AnimalIcons.KRUK;
            case 10, 11, 12, 13, 14 -> AnimalIcons.PINGWIN;
            case 15, 16, 17, 18, 19 -> AnimalIcons.KURA;
            default -> AnimalIcons.SOWONIEDZWIEDZ;
        };
    }

    /**
     * Zwraca liczbę zjedzonych roślin przez zwierzaka.
     */
    public int getGrassEaten() {
        return grassEaten;
    }

    /**
     * Sprawdza, czy zwierzę jest żywe (czy ma dodatnią ilość energii).
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Zwraca wiek zwierzaka na podstawie bieżącego dnia symulacji.
     */
    public int getAge(int currentDay) {
        return isAlive() ? (currentDay - birthDate) : (deathDay - birthDate);
    }

    /**
     * Ustawia dzień śmierci zwierzaka.
     */
    public void die(int currentDay) {
        if (isAlive()) {
            this.alive=false;
            energy = 0;
            this.deathDay = currentDay-birthDate;
        }
    }

    /**
     * Zwraca dzień śmierci zwierzaka
     * @return dzień śmierci zwierzaka
     */
    public int getDeathDay() {
        return deathDay;
    }

    /**
     * Dodaje dziecko do listy dzieci zwierzaka
     * @param child dziecko do dodania
     */
    public void addChild(Animal child) {
        this.children.add(child);
    }

    /**
     * Zwraca listę dzieci zwierzaka
     * @return lista dzieci zwierzaka
     */
    public List<Animal> getChildren() {
        return children;
    }

    /**
     * Zwraca liczbę dzieci zwierzaka
     * @return liczba dzieci zwierzaka
     */
    public int getChildrenCount() {
        return children.size();
    }

    /**
     * Liczy wszystkich przodków zwierzaka.
     * Przez możliwość "kazirodztwa", metoda
     * zbiera wszystkich przodków do hashsetu,
     * aby uniknąć powtórzeń, a potem zwraca jego długość
     * @return liczba potomków zwierzaka
     */
    public int getDescendantsCount() {
        Set<Animal> uniqueDescendants = new HashSet<>();
        collectDescendants(this, uniqueDescendants);
        return uniqueDescendants.size();
    }

    /**
     * Funkcja dodaje do podanego hashsetu wszystkich potomków podanego zwierzaka
     * @param animal Zwierzak, którego potomków dodajemy
     * @param uniqueDescendants Hashset, do którego dodajemy potomków
     */
    private void collectDescendants(Animal animal, Set<Animal> uniqueDescendants) {
        for (Animal child : animal.getChildren()) {
            if (uniqueDescendants.add(child)) {
                collectDescendants(child, uniqueDescendants);
            }
        }
    }
}
