package agh.isc.oop.project.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorldMap {

    boolean canMoveTo(Vector2d position);

    void place(Animal animal) throws IncorrectPositionException;


    boolean isOccupied(Vector2d position);

    Optional<List<WorldElement>> objectAt(Vector2d position);


    void mapChanged(String message);

    UUID getID();



}
