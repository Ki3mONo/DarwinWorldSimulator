package agh.isc.oop.project.model.map;

import agh.isc.oop.project.model.*;
import agh.isc.oop.project.model.elements.Animal;
import agh.isc.oop.project.model.elements.Genome;
import agh.isc.oop.project.model.elements.Grass;
import agh.isc.oop.project.model.util.Vector2d;
import agh.isc.oop.project.simulation.SimulationConfig;
import agh.isc.oop.project.simulation.SimulationConfigBuilder;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


//Używam refleksji, żeby obejść losową inicjalizajcę,
//inaczej nie da się tego testować

public class WorldMapTest {

    @Test
    void placeAnimal(){
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setMapHeight(6);
        builder.setMapWidth(6);
        builder.setStartGrassCount(0);
        builder.setInitialEnergy(10);
        builder.setMoveCost(0);
        builder.setGenomeLength(1);

        SimulationConfig config = builder.build();
        Animal.setConfig(config);
        Genome.setConfig(config);

        AbstractWorldMap map = new CrawlingJungleMap(config);

        Animal animal1 = new Animal(new Vector2d(1, 1), List.of(0));
        Animal animal2 = new Animal(new Vector2d(2, 1), List.of(0));
        Animal animal3 = new Animal(new Vector2d(2, 5), List.of(0));
        Animal animal4 = new Animal(new Vector2d(2, 1), List.of(0));    //postawiony na innym
        Animal animal5 = new Animal(new Vector2d(2, 6), List.of(0));    //poza mapą
        Animal animal6 = new Animal(new Vector2d(-1, 1), List.of(0));   //poza mapą


        assertDoesNotThrow(() -> map.place(animal1));
        assertDoesNotThrow(() -> map.place(animal2));
        assertDoesNotThrow(() -> map.place(animal3));
        assertDoesNotThrow(() -> map.place(animal4));
        assertThrows(IncorrectPositionException.class, () -> map.place(animal5));
        assertThrows(IncorrectPositionException.class, () -> map.place(animal6));

        assertTrue(map.objectAt(new Vector2d(1, 1)).get().contains(animal1));
        assertTrue(map.objectAt(new Vector2d(2, 1)).get().contains(animal2));

    }

    @Test
    void moveAnimal(){
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setMapHeight(6);
        builder.setMapWidth(6);
        builder.setStartGrassCount(0);
        builder.setInitialEnergy(10);
        builder.setMoveCost(0);
        builder.setGenomeLength(1);

        SimulationConfig config = builder.build();
        Animal.setConfig(config);
        Genome.setConfig(config);

        Animal animal1 = new Animal(new Vector2d(0, 0), List.of(0));
        Animal animal2 = new Animal(new Vector2d(5, 5), List.of(0));
        Animal animal3 = new Animal(new Vector2d(0, 2), List.of(0));
        Animal animal4 = new Animal(new Vector2d(5, 3), List.of(0));

        AbstractWorldMap map = new CrawlingJungleMap(config);

        try {
            Field orientation = animal1.getClass().getDeclaredField("orientation");
            orientation.setAccessible(true);
            orientation.set(animal1, MapDirection.SOUTH);
            orientation.set(animal2, MapDirection.NORTH);
            orientation.set(animal3, MapDirection.WEST);
            orientation.set(animal4, MapDirection.EAST);

            map.place(animal1);
            map.place(animal2);
            map.place(animal3);
            map.place(animal4);

            //Idą na bieguny
            map.move(animal1);
            map.move(animal2);

            //Teleportują się na drugi koniec
            map.move(animal3);
            map.move(animal4);

            assertEquals(MapDirection.NORTH, orientation.get(animal1)); //odwrócony kierunek
            assertEquals(MapDirection.SOUTH, orientation.get(animal2)); //odwrócony kierunek
            assertEquals(MapDirection.WEST, orientation.get(animal3));  //ten sam kierunek
            assertEquals(MapDirection.EAST, orientation.get(animal4));  //ten sam kierunek

            assertEquals(new Vector2d(0, 0), animal1.getPosition());
            assertEquals(new Vector2d(5, 5), animal2.getPosition());
            assertEquals(new Vector2d(5, 2), animal3.getPosition());
            assertEquals(new Vector2d(0, 3), animal4.getPosition());


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void eatingGrass(){
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setMapHeight(6);
        builder.setMapWidth(6);
        builder.setStartGrassCount(0);
        builder.setInitialEnergy(10);
        builder.setMoveCost(0);
        builder.setGenomeLength(1);
        builder.setGrassEnergy(5);

        SimulationConfig config = builder.build();
        Animal.setConfig(config);
        Genome.setConfig(config);

        AbstractWorldMap map = new CrawlingJungleMap(config);

        map.grassMap.put(new Vector2d(2, 2), new Grass(new Vector2d(2, 2)));
        map.grassMap.put(new Vector2d(2, 3), new Grass(new Vector2d(2, 3)));
        map.grassMap.put(new Vector2d(5, 4), new Grass(new Vector2d(5, 4)));

        Animal animal1 = new Animal(new Vector2d(2, 2), List.of(0));
        Animal animal2 = new Animal(new Vector2d(5, 4), List.of(0));
        builder.setInitialEnergy(25);
        Animal.setConfig(builder.build());
        Animal animal3 = new Animal(new Vector2d(5, 4), List.of(0));

        try{
            map.place(animal1);
            map.place(animal2);
            map.place(animal3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        map.handleEating();

        assertEquals(15, animal1.getEnergy());  //zjadł, był jedyny
        assertEquals(10, animal2.getEnergy());  //bez zmiany, bo jest słabszy
        assertEquals(30, animal3.getEnergy());  //zjadł, bo był silniejszy

        assertTrue(map.getGrassAt(new Vector2d(2, 2)).isEmpty());
        assertTrue(map.getGrassAt(new Vector2d(2, 3)).isPresent()); // ta trawa nie była zjedzona
        assertTrue(map.getGrassAt(new Vector2d(5, 4)).isEmpty());
    }

    @Test
    void handleReproduction(){
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setMapHeight(6);
        builder.setMapWidth(6);
        builder.setStartGrassCount(0);
        builder.setInitialEnergy(20);
        builder.setMoveCost(0);
        builder.setGenomeLength(4);
        builder.setMinMutations(0);
        builder.setMaxMutations(0);
        builder.setReproductionEnergy(10);
        builder.setReproductionCost(3);

        SimulationConfig config = builder.build();
        Animal.setConfig(config);
        Genome.setConfig(config);

        AbstractWorldMap map = new CrawlingJungleMap(config);

        Animal animal1 = new Animal(new Vector2d(2, 2), List.of(1, 1, 1, 1));
        Animal animal2 = new Animal(new Vector2d(2, 2), List.of(2, 2, 2, 2));
        Animal animal3 = new Animal(new Vector2d(2, 2), List.of(3, 3, 3, 3));

        //e = 20
        Animal animal4 = new Animal(new Vector2d(5, 4), List.of(4, 4, 4, 4));
        builder.setInitialEnergy(40);
        Animal.setConfig(builder.build());
        //e = 40
        Animal animal5 = new Animal(new Vector2d(5, 4), List.of(5, 5, 5, 5));
        builder.setInitialEnergy(35);
        Animal.setConfig(builder.build());
        //e = 35
        Animal animal6 = new Animal(new Vector2d(5, 4), List.of(6, 6, 6, 6));
        builder.setInitialEnergy(22);
        Animal.setConfig(builder.build());
        //e = 22
        Animal animal7 = new Animal(new Vector2d(5, 4), List.of(7, 7, 7, 7));

        // na polu (5, 4) powinny się rozmnażać 5 z 6 i 4 z 7

        try{
            map.place(animal1);
            map.place(animal2);
            map.place(animal3);
            map.place(animal4);
            map.place(animal5);
            map.place(animal6);
            map.place(animal7);
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        map.handleReproduction(2137);

        List<Animal> kidsV54 =  map.getAnimals().get(new Vector2d(5, 4))
                .stream()
                .filter(animal -> animal != animal4 && animal != animal5 &&
                animal != animal6 && animal != animal7)
                .toList();

        List<Integer> kid1 = kidsV54.get(0).getGenome().getGeneList();
        List<Integer> kid2 = kidsV54.get(1).getGenome().getGeneList();

        //To sprawdza, czy rodzą się dzieci z dobrych par rodziców
        assertTrue(((kid1.equals(List.of(5, 5, 5, 6)) || kid1.equals(List.of(6, 5, 5, 5)))
        && (kid2.equals(List.of(4, 7, 7, 7)) || kid2.equals(List.of(7, 7, 7, 4))))
        ||
        ((kid2.equals(List.of(5, 5, 5, 6)) || kid2.equals(List.of(6, 5, 5, 5)))
        &&
        (kid1.equals(List.of(4, 7, 7, 7)) || kid1.equals(List.of(7, 7, 7, 4)))));

        //Na tym polu mają być zwierzęta 1, 2, 3 i dziecko,
        //jeden ze zwierzaków się nie rozmnaża
        assertEquals(4, map.getAnimals().get(new Vector2d(2, 2)).size());

        assertEquals(17, animal4.getEnergy());
        assertEquals(37, animal5.getEnergy());
        assertEquals(32, animal6.getEnergy());
        assertEquals(19, animal7.getEnergy());

    }
}
