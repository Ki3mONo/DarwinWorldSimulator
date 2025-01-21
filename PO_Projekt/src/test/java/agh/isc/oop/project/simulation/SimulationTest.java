package agh.isc.oop.project.simulation;

import agh.isc.oop.project.model.elements.Animal;
import agh.isc.oop.project.model.elements.Genome;
import agh.isc.oop.project.model.map.AbstractWorldMap;
import agh.isc.oop.project.model.map.EquatorForestMap;
import agh.isc.oop.project.model.map.MapDirection;
import agh.isc.oop.project.model.util.Vector2d;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulationTest {

    @Test
    void runSimTest(){
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setMapHeight(6);
        builder.setMapWidth(6);
        builder.setStartGrassCount(0);
        builder.setInitialEnergy(10);
        builder.setReproductionEnergy(40);
        builder.setReproductionCost(15);
        builder.setGrassEnergy(100);
        builder.setMoveCost(10);
        builder.setGenomeLength(1);
        builder.setDailyGrassGrowth(0);

        SimulationConfig config = builder.build();

        Animal.setConfig(config);
        Genome.setConfig(config);

        //one mają jeść rośliny ze środkowego pasa i się rozmnożyć na (2, 2)
        Animal animal1 = new Animal(new Vector2d(0, 2), List.of(0));
        Animal animal2 = new Animal(new Vector2d(4, 2), List.of(0));
        //ten ma umrzeć po 1 ruchu
        Animal animal3 = new Animal(new Vector2d(5, 5), List.of(0));

        AbstractWorldMap map = new EquatorForestMap(config);

        Simulation sim = new Simulation(config, map, null);

        try{
            map.place(animal1);
            map.place(animal2);
            map.place(animal3);

            Field aliveAnimals = Simulation.class.getDeclaredField("aliveAnimals");
            Field deadAnimals = Simulation.class.getDeclaredField("deadAnimals");
            aliveAnimals.setAccessible(true);
            deadAnimals.setAccessible(true);
            aliveAnimals.set(sim, new ArrayList<>(List.of(animal1, animal2, animal3)));

            Field orientation = Animal.class.getDeclaredField("orientation");
            orientation.setAccessible(true);
            orientation.set(animal1, MapDirection.EAST);
            orientation.set(animal2, MapDirection.WEST);
            orientation.set(animal3, MapDirection.NORTH);

            Method placeGrass = AbstractWorldMap.class.getDeclaredMethod("putGrass", Vector2d.class);
            placeGrass.setAccessible(true);
            placeGrass.invoke(map, new Vector2d(1, 2));
            placeGrass.invoke(map, new Vector2d(2, 2));
            placeGrass.invoke(map, new Vector2d(3, 2));

            Method dayCycle = Simulation.class.getDeclaredMethod("performDayCycle");
            dayCycle.setAccessible(true);

            dayCycle.invoke(sim);
            //tutaj się rozmnożą
            dayCycle.invoke(sim);
            //rozejdą się bez rozmnażania(dziecko na pewno będzie miało za mało energii)
            dayCycle.invoke(sim);

            assertEquals(3, ((List<Animal>)aliveAnimals.get(sim)).size());
            assertEquals(1, ((List<Animal>)deadAnimals.get(sim)).size());

            assertEquals(new Vector2d(3, 2), animal1.getPosition());
            assertEquals(new Vector2d(1, 2), animal2.getPosition());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}