package agh.isc.oop.project.model.util;

import java.util.Objects;

/**
 * Klasa reprezentująca pozycje na mapie 2d
 */
public class Vector2d {

    private final int x;
    private final int y;

    public Vector2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //Gettery do współrzędnych
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Odpowiednik przeładowania operatora a <= b
     * @param other porównywany wektor
     * @return wartość logiczna x1 <= x2 && y1 <= y2
     */
    public boolean precedes(Vector2d other){
        return x <= other.getX() && y <= other.getY();
    }

    /**
     * Odpowiednik przeładowania operatora a >= b
     * @param other porównywany wektor
     * @return wartość logiczna x1 >= x2 && y1 >= y2
     */
    public boolean follows(Vector2d other){
        return x >= other.getX() && y >= other.getY();
    }

    /**
     * Odpowiednik przeładowania operatora a + b
     * @param other dodawany wektor
     * @return nowy wetkor o współrzędnych (x1 + x2, y1 + y2)
     */
    public Vector2d add(Vector2d other){
        return new Vector2d(x + other.getX(), y + other.getY());
    }

    /**
     * Odpowiednik przeładowania operatora a - b
     * @param other odejmowany wektor
     * @return nowy wetkor o współrzędnych (x1 - x2, y1 - y2)
     */
    public Vector2d subtract(Vector2d other){
        return new Vector2d(x - other.getX(), y - other.getY());
    }

    /**
     * Wyznacza prawy górny róg prostokąta określonego
     * przez wektory this i other
     * @param other drugi wektor
     * @return nowy wektor o współrzędnych (max(x1, x2), max(y1, y2))
     */
    public Vector2d upperRight(Vector2d other){
        return new Vector2d(Math.max(x, other.getX()), Math.max(y, other.getY()));
    }

    /**
     * Wyznacza lewy dolny róg prostokąta określonego
     * przez wektory this i other
     * @param other drugi wektor
     * @return nowy wektor o współrzędnych (min(x1, x2), min(y1, y2))
     */
    public Vector2d lowerLeft(Vector2d other){
        return new Vector2d(Math.min(x, other.getX()), Math.min(y, other.getY()));
    }

    /**
     * Odpowiednik przeładowania operatora -a
     * @return nowy wektor o współrzędnych (-x, -y)
     */
    public Vector2d opposite(){
        return new Vector2d(-x, -y);
    }

    /**
     * Metoda equals zwraca True, jeżeli x1 == x2 i y1 == y2
     * @param other porównywany wektor
     * @return wartość logiczna x1 == x2 && y1 == y2
     */
    @Override
    public boolean equals(Object other){
        if(this == other){
            return true;
        }
        if(!(other instanceof Vector2d)){
            return false;
        }
        Vector2d otherVector = (Vector2d)other;
        return x == otherVector.getX() && y == otherVector.getY();
    }

    @Override
    public int hashCode(){
        return Objects.hash(x, y);
    }
}
