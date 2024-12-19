package agh.isc.oop.project.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Vector2dTest {
    @Test
    void coordinatesInit(){
        Vector2d v1 = new Vector2d(1,2);
        assertEquals(1, v1.getX());
        assertEquals(2, v1.getY());
    }
    @Test
    public void equalsTest(){
        Vector2d v1 = new Vector2d(1,2);

        assertEquals(v1, v1);
        assertEquals(new Vector2d(1,2), v1);
        assertNotEquals(new Vector2d(2,2), v1);
        assertNotEquals(new Vector2d(1,1), v1);
        assertFalse(v1.equals(1));
    }
    @Test
    public void toStringTest(){
        Vector2d v1 = new Vector2d(2,4);
        assertEquals("(2, 4)", v1.toString());
    }
    @Test
    public void percedesTest(){
        Vector2d v1 = new Vector2d(3,4);

        assertTrue(v1.precedes(v1));
        assertTrue(v1.precedes(new Vector2d(5,6)));
        assertTrue(v1.precedes(new Vector2d(3, 5)));
        assertFalse(v1.precedes(new Vector2d(2, 3)));
        assertFalse(v1.precedes(new Vector2d(3, 2)));
    }
    @Test
    void followsTest(){
        Vector2d v1 = new Vector2d(2,4);

        assertTrue(v1.follows(v1));
        assertTrue(v1.follows(new Vector2d(2, 3)));
        assertTrue(v1.follows(new Vector2d(1, 3)));
        assertFalse(v1.follows(new Vector2d(3, 2)));
        assertFalse(v1.follows(new Vector2d(3, 5)));
    }
    @Test
    void upperRightTest(){
        Vector2d v1 = new Vector2d(2,4);

        assertEquals(new Vector2d(4, 4), v1.upperRight(new Vector2d(4, 2)));
        assertEquals(new Vector2d(2, 4), v1.upperRight(new Vector2d(2, 2)));
        assertEquals(new Vector2d(4, 4), v1.upperRight(new Vector2d(4, 4)));
        assertEquals(new Vector2d(2,4), v1.upperRight(new Vector2d(2, 4)));
    }
    @Test
    void lowerLeftTest(){
        Vector2d v1 = new Vector2d(1, 0);

        assertEquals(new Vector2d(1, 0), v1.lowerLeft(new Vector2d(1, 0)));
        assertEquals(new Vector2d(0, 0), v1.lowerLeft(new Vector2d(0, 1)));
        assertEquals(new Vector2d(1, 0), v1.lowerLeft(new Vector2d(1, 1)));
        assertEquals(new Vector2d(0, 0), v1.lowerLeft(new Vector2d(0, 0)));
    }
    @Test
    void addTest(){
        Vector2d v1 = new Vector2d(2,4);
        assertEquals(new Vector2d(3, 3), v1.add(new Vector2d(1, -1)));
    }
    @Test
    void subtractTest(){
        Vector2d v1 = new Vector2d(2,4);
        assertEquals(new Vector2d(3, 3), v1.subtract(new Vector2d(-1, 1)));
    }
    @Test
    void oppositeTest(){
        Vector2d v1 = new Vector2d(-1, 1);
        Vector2d v2 = new Vector2d(-1, -2);

        assertEquals(new Vector2d(1, -1), v1.opposite());
        assertEquals(new Vector2d(1, 2), v2.opposite());
    }
}
