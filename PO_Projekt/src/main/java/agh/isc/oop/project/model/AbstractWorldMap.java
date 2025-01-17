package agh.isc.oop.project.model;

import java.util.*;

public abstract class AbstractWorldMap implements WorldMap {
    protected UUID mapID;
    protected Vector2d mapSize;
    protected Vector2d leftBottomCorner = new Vector2d(Integer.MIN_VALUE, Integer.MIN_VALUE);
    protected Vector2d rightUpperCorner = new Vector2d(Integer.MAX_VALUE, Integer.MAX_VALUE);
    protected Map<Vector2d, List<Animal>> animals = new HashMap<>();
    protected Map<Vector2d, Grass> grassMap = new HashMap<>();
    protected Map<Vector2d, List<WorldElement>> worldElements = new HashMap<>();
    private List<MapChangeListener> observing = new ArrayList<>();

    //Chyba dobrze będzie też tu dodać parametr config, tak jak w animalu i genomie

    public void addObserver(MapChangeListener mapChangeListener) {
        observing.add(mapChangeListener);
    }

    public void removeObserver(MapChangeListener mapChangeListener) {
        observing.remove(mapChangeListener);
    }

    public void mapChanged(String message) {
        for (MapChangeListener mapChangeListener : observing) {
            mapChangeListener.mapChanged(this, message);
        }
    }

    protected Map<Vector2d, List<Animal>> getAnimals() {
        return animals;
    }

    protected Map<Vector2d, List<WorldElement>> getWorldElements() {
        return worldElements;
    }

    public Boundary getCurrentBounds() {
        return new Boundary(leftBottomCorner, rightUpperCorner);
    }
    public UUID getID() {
        return mapID;
    }

    protected boolean inBounds(Vector2d position) {
        return this.leftBottomCorner.precedes(position) && this.rightUpperCorner.follows(position);
    }
    public abstract void initializeGrass(int initialGrassSize);

    public abstract void grassGrow(int dailyGrowth);
    public boolean isOccupied(Vector2d position) {
        return animals.containsKey(position);
    }

    public boolean canMoveTo(Vector2d position) {
        return inBounds(position);
    }

    public void place(Animal animal) throws IncorrectPositionException {
        Vector2d position = animal.getPosition();
        if (!inBounds(position)) {
            throw new IncorrectPositionException(position);
        }
        animals.computeIfAbsent(position, k -> new ArrayList<>()).add(animal);
        mapChanged("Animal placed at: " + position);
    }

    //Przeniosłem całkowicie przeliczanie pozycji do animala, więc
    //w sumie nie wiem czy cała ta metoda nie jest do usunięcia,
    //bo imo trochę chaos wprowadza, że nie wiadomo kiedy dostajesz
    //normalną pozycję, a kiedy do ajustowania
    public Vector2d adjustPosition(Vector2d position) {
        int x = (position.getX() + mapSize.getX()) % mapSize.getX();
        int y = position.getY();

        if (y < 0 || y >= mapSize.getY()) {
            y = position.getY() < 0 ? 0 : mapSize.getY() - 1; // Bieguny
        }
        return new Vector2d(x, y);
    }

    //worldElements chyba w ogóle nie jest potrzebne
    public List<WorldElement> objectAt(Vector2d position) {
        Vector2d adjustedPosition = adjustPosition(position);
        return worldElements.getOrDefault(adjustedPosition, Collections.emptyList());
    }

    public Optional<Grass> getGrassAt(Vector2d grassPosition) {
        return Optional.ofNullable(grassMap.get(grassPosition));
    }

    public void removeGrass(Vector2d grassPosition) {
        grassMap.remove(grassPosition);
    }

    public void move(Animal animal){
        animals.get(animal.getPosition()).remove(animal);
        //newPosition dla czytelności
        Vector2d newPosition = animal.move(this);

        animals.computeIfAbsent(newPosition, k -> new ArrayList<>()).add(animal);
    }

    public void handleEating(int grassEnergy){
        //Trawa do usunięcia później,
        //inaczej poleci ConcurrentModificationException
        List<Vector2d> grassEaten = new LinkedList<>();

        //jedzenie trawy
        for (Vector2d position : grassMap.keySet()) {
            Animal animal;
            try {
                animal = animals.getOrDefault(position, List.of()).stream()
                .max(Comparator.comparing(Animal::getEnergy)).orElseThrow();
            } catch (NoSuchElementException e) {
                //Nie ma żadnego zwierzaka na tej pozycji
                continue;
            }
            animal.eat(grassEnergy);
            grassEaten.add(position);
            mapChanged("Grass consumed at: " + position);
        }

        //usuwanie trawy z mapy
        for (Vector2d position : grassEaten) {
            removeGrass(position);
        }

    }

    public void handleReproduction(int currentDay, int reproductionEnergy){
        for (Vector2d position : animals.keySet()) {
            //Lista zwierząt gotowych do reprodukcji na tej pozycji,
            // posortowana po energii malejąco
            List<Animal> animalsHere = animals.get(position).stream()
            .filter(a -> a.getEnergy() >= reproductionEnergy)
            .sorted(Comparator.comparing(Animal::getEnergy, (e1, e2) -> Integer.compare(e2, e1)))
            .toList();

            int i = 0;
            while (i + 1 < animalsHere.size()) {
                Animal parent1 = animalsHere.get(i);
                Animal parent2 = animalsHere.get(i + 1);

                try {
                    place(new Animal(parent1, parent2, currentDay));
                    mapChanged("Animal born at: " + position);
                } catch (IncorrectPositionException e) {
                    //Nie może tu polecieć, bo rodzice już są na mapie
                    throw new RuntimeException();
                }

                parent1.loseReproductionEnergy();
                parent2.loseReproductionEnergy();

                i+=2;
            }
        }
    }
}
