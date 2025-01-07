package agh.isc.oop.project.model;

public class Grass implements WorldElement{
    private final Vector2d position;
    private final int energyValue; // nie wiem czy to potrzebne

    public Grass(Vector2d position, int energyValue) {
        this.position = position;
        this.energyValue = energyValue;
    }

    public Grass(Vector2d position) {
        this(position, 10); // to pewnie do zmiany
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    public int getEnergyValue() {
        return energyValue;
    }

    @Override
    public String toString() {
        return "*"; //może sie przyda
    }
}
