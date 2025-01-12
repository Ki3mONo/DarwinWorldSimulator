package agh.isc.oop.project.model;

import agh.isc.oop.project.model.*;
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
        return inBounds(position) && !isOccupied(position);
    }

    public void place(Animal animal) throws IncorrectPositionException {
        Vector2d position = adjustPosition(animal.getPosition());
        if (!inBounds(position)) {
            throw new IncorrectPositionException(position);
        }
        animals.computeIfAbsent(position, k -> new ArrayList<>()).add(animal);
        mapChanged("Animal placed at: " + position);
    }

    public Vector2d adjustPosition(Vector2d position) {
        int x = (position.getX() + mapSize.getX()) % mapSize.getX();
        int y = position.getY();

        if (y < 0 || y >= mapSize.getY()) {
            y = position.getY() < 0 ? 0 : mapSize.getY() - 1; // Bieguny
        }
        return new Vector2d(x, y);
    }
    public List<WorldElement> objectAt(Vector2d position) {
        Vector2d adjustedPosition = adjustPosition(position);
        return worldElements.getOrDefault(adjustedPosition, Collections.emptyList());
    }

    public Grass getGrassAt(Vector2d grassPosition) {
        return grassMap.get(grassPosition);
    }

    public void move(Animal animal) throws IncorrectPositionException {
        //todo
    }
    public void removeGrass(Vector2d grassPosition) {
        grassMap.remove(grassPosition);
    }
}
