package agh.isc.oop.project.model;

import agh.isc.oop.project.model.util.Vector2d;

public class IncorrectPositionException extends Exception{
    public IncorrectPositionException(Vector2d position) {
        super("Position " + position + " is not correct");
    }
}
