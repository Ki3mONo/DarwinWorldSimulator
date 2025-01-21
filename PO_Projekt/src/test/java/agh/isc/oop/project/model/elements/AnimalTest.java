package agh.isc.oop.project.model.elements;

import agh.isc.oop.project.model.map.AbstractWorldMap;
import agh.isc.oop.project.model.map.CrawlingJungleMap;
import agh.isc.oop.project.model.map.MapDirection;
import agh.isc.oop.project.model.util.Vector2d;
import agh.isc.oop.project.simulation.Simulation;
import agh.isc.oop.project.simulation.SimulationConfig;
import agh.isc.oop.project.simulation.SimulationConfigBuilder;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {

    @Test
    void countGrassEaten(){
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setMapHeight(6);
        builder.setMapWidth(6);
        builder.setStartGrassCount(0);
        builder.setStartAnimalCount(0);
        builder.setInitialEnergy(10);
        builder.setMoveCost(0);
        builder.setGenomeLength(1);

        SimulationConfig config = builder.build();
        Animal.setConfig(config);
        Genome.setConfig(config);

        AbstractWorldMap map = new CrawlingJungleMap(config);

        Animal animal1 = new Animal(new Vector2d(2, 2), List.of(0));
        Animal animal2 = new Animal(new Vector2d(2, 2), List.of(0));
        Animal animal3 = new Animal(new Vector2d(5, 4), List.of(0));

        List<Animal> animals = new ArrayList<>(List.of(animal1, animal2, animal3));
        try{
            Field orientation = Animal.class.getDeclaredField("orientation");
            orientation.setAccessible(true);
            orientation.set(animal1, MapDirection.NORTH);
            orientation.set(animal2, MapDirection.EAST);
            orientation.set(animal3, MapDirection.SOUTH);

            for(Animal animal : animals){
                map.place(animal);
            }

            Method putGrass = AbstractWorldMap.class.getDeclaredMethod("putGrass", Vector2d.class);
            putGrass.setAccessible(true);
            //zje animal 1
            putGrass.invoke(map, new Vector2d(2, 3));
            putGrass.invoke(map, new Vector2d(2, 4));

            //zje animal 2
            putGrass.invoke(map, new Vector2d(3, 2));
            putGrass.invoke(map, new Vector2d(4, 2));

            //zje animal 3
            putGrass.invoke(map, new Vector2d(5, 3));
            putGrass.invoke(map, new Vector2d(5, 2));
            putGrass.invoke(map, new Vector2d(5, 1));

            //nikt nie zje
            putGrass.invoke(map, new Vector2d(0, 0));
            putGrass.invoke(map, new Vector2d(0, 1));

            for (int i = 0; i < 3; i++) {
                for (Animal animal : animals) {
                    map.move(animal);
                }
                map.handleEating();
            }

            assertEquals(2, animal1.getGrassEaten());
            assertEquals(2, animal2.getGrassEaten());
            assertEquals(3, animal3.getGrassEaten());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void countChildren(){
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setMapHeight(6);
        builder.setMapWidth(6);
        builder.setStartGrassCount(0);
        builder.setInitialEnergy(20);
        builder.setMoveCost(0);
        builder.setGenomeLength(4);
        builder.setMinMutations(0);
        builder.setMaxMutations(0);
        builder.setReproductionEnergy(1);
        builder.setReproductionCost(1);

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

        List<Animal> children1 =  map.handleReproduction(2137);
        //(2, 2) -> 1 dziecko
        //(5,4)-> dzieci 56 i 47
        assertTrue((animal5.getChildren().equals(List.of(children1.get(0))) && animal4.getChildren().equals(List.of(children1.get(1))))
                ||
                (animal5.getChildren().equals(List.of(children1.get(1))) && animal4.getChildren().equals(List.of(children1.get(0))))
        );
        assertEquals(1, animal4.getChildrenCount());
        assertEquals(1, animal5.getChildrenCount());
        assertEquals(1, animal6.getChildrenCount());
        assertEquals(1, animal7.getChildrenCount());

        List<Animal> children2 =  map.handleReproduction(2137);
        //(5,4)-> nowe dzieci 56 i 47 oraz dziecko ich dzieci
        //(2, 2) -> 1 dziecko tych samych rodziców oraz kolejne ich dziecka i tego trzeciego zwierzaka z początku
        assertEquals(3, animal5.getDescendantsCount());
        assertEquals(3, animal6.getDescendantsCount());

        assertEquals(3, animal4.getDescendantsCount());
        assertEquals(3, animal7.getDescendantsCount());
    }

    @Test
    void moveDead(){
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setMapHeight(6);
        builder.setMapWidth(6);
        builder.setStartGrassCount(0);
        builder.setInitialEnergy(20);
        builder.setMoveCost(0);
        builder.setGenomeLength(1);
        builder.setMinMutations(0);
        builder.setMaxMutations(0);
        builder.setReproductionEnergy(1);
        builder.setReproductionCost(1);

        SimulationConfig config = builder.build();
        Animal.setConfig(config);
        Genome.setConfig(config);

        AbstractWorldMap map = new CrawlingJungleMap(config);

        Animal animal1 = new Animal(new Vector2d(2, 2), List.of(0));
        try{
            map.place(animal1);

            Field energy = Animal.class.getDeclaredField("energy");
            energy.setAccessible(true);
            Field alive = Animal.class.getDeclaredField("alive");
            alive.setAccessible(true);

            energy.set(animal1, 0);
            alive.set(animal1, false);

            assertEquals(new Vector2d(2, 2), animal1.move(map));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}