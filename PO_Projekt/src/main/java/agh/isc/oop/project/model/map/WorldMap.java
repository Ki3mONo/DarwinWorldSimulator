package agh.isc.oop.project.model.map;

import agh.isc.oop.project.model.elements.Animal;
import agh.isc.oop.project.model.IncorrectPositionException;
import agh.isc.oop.project.model.util.Vector2d;
import agh.isc.oop.project.model.elements.WorldElement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorldMap {

    boolean canMoveTo(Vector2d position);

    void place(Animal animal) throws IncorrectPositionException;


    boolean isOccupied(Vector2d position);

    Optional<List<WorldElement>> objectAt(Vector2d position);


    void mapChanged();

    UUID getID();



}
