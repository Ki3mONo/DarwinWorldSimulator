package agh.isc.oop.project.model;

import agh.isc.oop.project.simulation.SimulationConfig;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class AbstractWorldMap implements WorldMap {
    protected UUID mapID = UUID.randomUUID();
    protected Vector2d mapSize;
    protected Vector2d leftBottomCorner = new Vector2d(0, 0);
    protected Vector2d rightUpperCorner;
    protected ConcurrentHashMap<Vector2d, List<Animal>> animals = new ConcurrentHashMap<>();
    protected HashMap<Vector2d, Grass> grassMap = new HashMap<>();
    protected HashMap<Vector2d, List<WorldElement>> worldElements = new HashMap<>();
    private List<MapChangeListener> observing = new ArrayList<>();
    private final SimulationConfig config;
    private final AnimalFactory animalFactory;

    private Map<Vector2d, Integer> grassGrowthHistory = new HashMap<>();

    public AbstractWorldMap(SimulationConfig config) {
        this.config = config;
        this.mapID = UUID.randomUUID();
        this.mapSize = new Vector2d(config.getMapWidth(), config.getMapHeight());
        this.leftBottomCorner = new Vector2d(0, 0);
        this.rightUpperCorner = new Vector2d(config.getMapWidth() - 1, config.getMapHeight() - 1);
        this.animalFactory = config.isAgingAnimalVariant() ? new AgingAnimalFactory() : new AnimalFactory();
    }

    public void addObserver(MapChangeListener mapChangeListener) {
        observing.add(mapChangeListener);
    }

    public void removeObserver(MapChangeListener mapChangeListener) {
        observing.remove(mapChangeListener);
    }

    public void mapChanged() {
        for (MapChangeListener mapChangeListener : observing) {
            mapChangeListener.mapChanged(this);
        }
    }

    public Map<Vector2d, List<Animal>> getAnimals() {
        return animals;
    }

    public void removeAnimals(List<Animal> animalsToRemove) {
        for (Animal animal : animalsToRemove) {
            Vector2d position = animal.getPosition();
            this.animals.get(position).remove(animal);
            this.worldElements.get(position).remove(animal);
        }
    }
    public HashMap<Vector2d, List<WorldElement>> getWorldElements() {
        return worldElements;
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
        return animals.containsKey(position) || grassMap.containsKey(position);
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
        worldElements.computeIfAbsent(position, k -> new ArrayList<>()).add(animal);
    }

    public void removeGrass(Vector2d grassPosition) {
        Grass grass = grassMap.remove(grassPosition);
        worldElements.get(grassPosition).remove(grass);
    }

    public void move(Animal animal) {
        Vector2d oldPosition = animal.getPosition();
        animals.get(oldPosition).remove(animal);
        worldElements.getOrDefault(oldPosition, new ArrayList<>()).remove(animal);

        Vector2d newPosition = animal.move(this);
        animals.computeIfAbsent(newPosition, k -> new ArrayList<>()).add(animal);
        worldElements.computeIfAbsent(newPosition, k -> new ArrayList<>()).add(animal);
    }

    public Optional<List<WorldElement>> objectAt(Vector2d position) {
        return Optional.ofNullable(worldElements.get(adjustPosition(position)));
    }

    public Optional<Grass> getGrassAt(Vector2d grassPosition) {
        return Optional.ofNullable(grassMap.get(grassPosition));
    }

    public Vector2d adjustPosition(Vector2d position) {
        int x = (position.getX() + mapSize.getX()) % mapSize.getX();
        int y = position.getY();
        if (y < 0 || y >= mapSize.getY()) {
            y = Math.max(0, Math.min(y, mapSize.getY() - 1));
        }
        return new Vector2d(x, y);
    }

    public void handleEating(int grassEnergy) {
        List<Vector2d> grassEaten = new ArrayList<>();

        for (Vector2d position : new ArrayList<>(grassMap.keySet())) {

            List<Animal> animalsHere = animals.get(position);
            if (animalsHere == null || animalsHere.isEmpty()) {
                continue; // Jeśli na tym polu nie ma zwierząt, pomijamy je
            }

            int maxEnergy = animalsHere.stream()
                    .mapToInt(Animal::getEnergy)
                    .max()
                    .orElse(0);

            List<Animal> strongestAnimals = animalsHere.stream()
                    .filter(a -> a.getEnergy() == maxEnergy)
                    .toList();

            int oldestBirthDate = strongestAnimals.stream()
                    .mapToInt(Animal::getBirthDate)
                    .min()
                    .orElse(Integer.MAX_VALUE);

            List<Animal> oldestAnimals = strongestAnimals.stream()
                    .filter(a -> a.getBirthDate() == oldestBirthDate)
                    .toList();

            int maxChildren = oldestAnimals.stream()
                    .mapToInt(Animal::getChildrenCount)
                    .max()
                    .orElse(0);

            List<Animal> topAnimals = oldestAnimals.stream()
                    .filter(a -> a.getChildrenCount() == maxChildren)
                    .toList();

            Animal winner = topAnimals.get(new Random().nextInt(topAnimals.size()));

            winner.eat(grassEnergy);
            grassEaten.add(position);
        }

        grassEaten.forEach(this::removeGrass);
    }

    public List<Animal> handleReproduction(int currentDay, int reproductionEnergy) {
        List<Animal> bornAnimals = new ArrayList<>();
        animals.keySet().forEach(position -> {
            //Tutaj kolejność identyczna jak przy jedzeniu
            List<Animal> candidates = animals.get(position).stream()
                    .filter(a -> a.getEnergy() >= reproductionEnergy)
                    .sorted(Comparator.comparingInt(Animal::getEnergy).reversed()
                            .thenComparing(Animal::getBirthDate)
                            .thenComparing(Animal::getChildrenCount).reversed())
                    .toList();

            for (int i = 0; i + 1 < candidates.size(); i += 2) {
                Animal parent1 = candidates.get(i);
                Animal parent2 = candidates.get(i + 1);
                Animal child = animalFactory.createAnimal(parent1, parent2, currentDay);
                try {
                    place(child);
                    bornAnimals.add(child);
                } catch (IncorrectPositionException e) {
                    System.err.println("Failed to place offspring: " + e.getMessage());
                }

                parent1.loseReproductionEnergy();
                parent2.loseReproductionEnergy();
                parent1.addChild(child);
                parent2.addChild(child);
            }
        });
        return bornAnimals;
    }

    public int getAnimalCount() {
        return animals.values().stream().mapToInt(List::size).sum();
    }

    public int getGrassCount() {
        return grassMap.size();
    }


    void putGrass(Vector2d position) {
        grassGrowthHistory.put(position, grassGrowthHistory.getOrDefault(position, 0) + 1);
        if (!grassMap.containsKey(position)) {
            Grass grass = new Grass(position);
            worldElements.computeIfAbsent(position, k -> new ArrayList<>()).add(grass);
            grassMap.put(position, grass);
        }
    }


    public Set<Vector2d> getPreferredGrassFields() {
        Set<Vector2d> preferredFields = new HashSet<>();

        Map<Vector2d, Integer> allGrassGrowthHistory = new HashMap<>(grassGrowthHistory);

        int maxGrowths = allGrassGrowthHistory.values().stream()
                .max(Integer::compare)
                .orElse(0);

        for (Map.Entry<Vector2d, Integer> entry : allGrassGrowthHistory.entrySet()) {
            if (entry.getValue() == maxGrowths) {
                preferredFields.add(entry.getKey());
            }
        }

        return preferredFields;
    }



    public double getAverageChildrenCount() {
        synchronized (animals) {
            List<Animal> allAnimals = animals.values().stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            int totalChildren = allAnimals.stream()
                    .mapToInt(Animal::getChildrenCount)
                    .sum();

            int animalCount = getAnimalCount();
            return animalCount > 0 ? (double) totalChildren / animalCount : 0;
        }
    }
}
