package agh.isc.oop.project.simulation;

import agh.isc.oop.project.model.elements.Animal;
import agh.isc.oop.project.model.elements.Genome;
import agh.isc.oop.project.model.elements.Grass;
import agh.isc.oop.project.model.elements.WorldElement;
import agh.isc.oop.project.model.map.AbstractWorldMap;
import agh.isc.oop.project.model.map.CrawlingJungleMap;
import agh.isc.oop.project.model.util.SimulationStatTracker;
import agh.isc.oop.project.model.util.Vector2d;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulationStatTrackerTest {
    @Test
    void emptyInitTest(){
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setMapHeight(6);
        builder.setMapWidth(6);
        builder.setStartAnimalCount(5);
        builder.setStartGrassCount(0);
        builder.setInitialEnergy(10);
        builder.setReproductionEnergy(5);
        builder.setReproductionCost(1);
        builder.setMoveCost(0);
        builder.setGenomeLength(1);

        SimulationConfig config = builder.build();

        AbstractWorldMap map = new CrawlingJungleMap(config);

        Simulation sim = new Simulation(config, map, null);

        SimulationStatTracker tracker = sim.getStatTracker();

        assertEquals(0, tracker.getAnimalCount());
        assertEquals(0, tracker.getGrassCount());
        assertEquals(0, tracker.getFreeFields());
        assertEquals(List.of(), tracker.getMostPopularGenes());
        assertEquals(0, tracker.getAverageEnergy());
        assertEquals(0, tracker.getAverageLifespan());
        assertEquals(0, tracker.getAverageChildren());
    }

    @Test
    void countingTest(){
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setMapHeight(6);
        builder.setMapWidth(6);
        builder.setStartAnimalCount(5);
        builder.setStartGrassCount(15);
        builder.setInitialEnergy(10);
        builder.setReproductionEnergy(5);
        builder.setReproductionCost(1);
        builder.setMoveCost(0);
        builder.setGenomeLength(1);
        builder.setDailyGrassGrowth(0);

        SimulationConfig config = builder.build();

        AbstractWorldMap map = new CrawlingJungleMap(config);

        Simulation sim = new Simulation(config, map, null);

        SimulationStatTracker tracker = sim.getStatTracker();

        tracker.mapChanged(map);
        assertEquals(5, tracker.getAnimalCount());

        try {
            Field grassMap = AbstractWorldMap.class.getDeclaredField("grassMap");
            grassMap.setAccessible(true);
            assertEquals(((HashMap<Vector2d, Grass>)grassMap.get(map)).size(), tracker.getGrassCount());

            Field worldElements = AbstractWorldMap.class.getDeclaredField("worldElements");
            worldElements.setAccessible(true);
            assertEquals(config.getMapHeight() * config.getMapWidth() -
                    ((HashMap<Vector2d, List<WorldElement>>)worldElements.get(map)).size()
                    ,tracker.getFreeFields());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(5, tracker.getAnimalCount());
    }

    @Test
    void popularGenesTest(){
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setMapHeight(6);
        builder.setMapWidth(6);
        builder.setStartAnimalCount(0);
        builder.setStartGrassCount(0);
        builder.setInitialEnergy(10);
        builder.setReproductionEnergy(5);
        builder.setReproductionCost(1);
        builder.setMoveCost(0);
        builder.setGenomeLength(3);
        builder.setDailyGrassGrowth(0);

        SimulationConfig config = builder.build();
        Animal.setConfig(config);
        Genome.setConfig(config);
        AbstractWorldMap map = new CrawlingJungleMap(config);

        Simulation sim = new Simulation(config, map, null);

        SimulationStatTracker tracker = sim.getStatTracker();

        Animal animal1 = new Animal(new Vector2d(0, 0), List.of(0, 0, 0));
        Animal animal2 = new Animal(new Vector2d(5, 5), List.of(1, 0, 2));
        Animal animal3 = new Animal(new Vector2d(0, 2), List.of(2, 1, 0));
        Animal animal4 = new Animal(new Vector2d(5, 3), List.of(2, 2, 2));
        Animal animal5 = new Animal(new Vector2d(5, 4), List.of(3, 2, 1));
        Animal animal6 = new Animal(new Vector2d(5, 5), List.of(3, 5, 3));
        Animal animal7 = new Animal(new Vector2d(5, 2), List.of(3, 2 ,1));
        Animal animal8 = new Animal(new Vector2d(5, 4), List.of(3, 2, 1));
        Animal animal9 = new Animal(new Vector2d(5, 5), List.of(1, 0, 2));
        Animal animal10 = new Animal(new Vector2d(5, 2), List.of(3, 2 ,1));

        List<Animal> animals = List.of(animal1, animal2, animal3, animal4, animal5,
                animal6, animal7, animal8, animal9, animal10);

        try {
            Field simAnimals = sim.getClass().getDeclaredField("aliveAnimals");
            simAnimals.setAccessible(true);
            simAnimals.set(sim, animals);
            for(Animal animal : animals) {
                map.place(animal);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        tracker.mapChanged(map);

        assertEquals(List.of(3, 2, 1), tracker.getMostPopularGenes());
    }

    @Test
    void averageEnergyTest(){
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setMapHeight(6);
        builder.setMapWidth(6);
        builder.setStartAnimalCount(0);
        builder.setStartGrassCount(0);
        builder.setInitialEnergy(10);
        builder.setReproductionEnergy(5);
        builder.setReproductionCost(1);
        builder.setMoveCost(0);
        builder.setGenomeLength(3);
        builder.setDailyGrassGrowth(0);

        SimulationConfig config = builder.build();
        Animal.setConfig(config);
        Genome.setConfig(config);
        AbstractWorldMap map = new CrawlingJungleMap(config);

        Simulation sim = new Simulation(config, map, null);

        SimulationStatTracker tracker = sim.getStatTracker();

        Animal animal1 = new Animal(new Vector2d(0, 0), List.of(0, 0, 0));
        Animal animal2 = new Animal(new Vector2d(5, 5), List.of(1, 0, 2));
        Animal animal3 = new Animal(new Vector2d(0, 2), List.of(2, 1, 0));
        Animal animal4 = new Animal(new Vector2d(5, 3), List.of(2, 2, 2));
        Animal animal5 = new Animal(new Vector2d(5, 4), List.of(3, 2, 1));
        Animal animal6 = new Animal(new Vector2d(5, 5), List.of(3, 5, 3));
        Animal animal7 = new Animal(new Vector2d(5, 2), List.of(3, 2 ,1));
        Animal animal8 = new Animal(new Vector2d(5, 4), List.of(3, 2, 1));
        Animal animal9 = new Animal(new Vector2d(5, 5), List.of(1, 0, 2));
        Animal animal10 = new Animal(new Vector2d(5, 2), List.of(3, 2 ,1));

        List<Animal> animals = List.of(animal1, animal2, animal3, animal4, animal5,
                animal6, animal7, animal8, animal9, animal10);

        try {
            Field simAnimals = sim.getClass().getDeclaredField("aliveAnimals");
            simAnimals.setAccessible(true);
            simAnimals.set(sim, animals);
            for(Animal animal : animals) {
                map.place(animal);
            }

            Field energy = Animal.class.getDeclaredField("energy");
            energy.setAccessible(true);
            energy.set(animal1, 10);
            energy.set(animal2, 20);
            energy.set(animal3, 30);
            energy.set(animal4, 40);
            energy.set(animal5, 69);
            energy.set(animal6, 21);
            energy.set(animal7, 37);
            energy.set(animal8, 42);
            energy.set(animal9, 10);
            energy.set(animal10, 99);

            tracker.mapChanged(map);
            assertEquals(37.8, tracker.getAverageEnergy());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void averageChildrenTest(){
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setMapHeight(6);
        builder.setMapWidth(6);
        builder.setStartAnimalCount(0);
        builder.setStartGrassCount(0);
        builder.setInitialEnergy(10);
        builder.setReproductionEnergy(5);
        builder.setReproductionCost(1);
        builder.setMoveCost(0);
        builder.setGenomeLength(3);
        builder.setDailyGrassGrowth(0);


        SimulationConfig config = builder.build();
        Animal.setConfig(config);
        Genome.setConfig(config);
        AbstractWorldMap map = new CrawlingJungleMap(config);

        Simulation sim = new Simulation(config, map, null);

        SimulationStatTracker tracker = sim.getStatTracker();

        Animal animal1 = new Animal(new Vector2d(0, 0), List.of(0, 0, 0));
        Animal animal2 = new Animal(new Vector2d(0, 0), List.of(1, 0, 2));
        Animal animal3 = new Animal(new Vector2d(0, 2), List.of(2, 1, 0));
        Animal animal4 = new Animal(new Vector2d(0, 3), List.of(2, 2, 2));
        Animal animal5 = new Animal(new Vector2d(5, 4), List.of(3, 2, 1));
        Animal animal6 = new Animal(new Vector2d(5, 4), List.of(3, 5, 3));
        Animal animal7 = new Animal(new Vector2d(5, 2), List.of(3, 2 ,1));
        Animal animal8 = new Animal(new Vector2d(5, 2), List.of(3, 2, 1));
        Animal animal9 = new Animal(new Vector2d(5, 2), List.of(1, 0, 2));
        Animal animal10 = new Animal(new Vector2d(5, 2), List.of(3, 2 ,1));

        List<Animal> animals = new ArrayList<>(List.of(animal1, animal2, animal3, animal4, animal5,
                animal6, animal7, animal8, animal9, animal10));


        try{
            Field simAnimals = sim.getClass().getDeclaredField("aliveAnimals");
            simAnimals.setAccessible(true);
            simAnimals.set(sim, animals);
            for(Animal animal : animals) {
                map.place(animal);
            }

            List<Animal> bornAnimals = map.handleReproduction(2137);
            assertEquals(4, bornAnimals.size());
            ((List<Animal>)simAnimals.get(sim)).addAll(bornAnimals);

            tracker.mapChanged(map);
            assertEquals((double) 8/14, tracker.getAverageChildren());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test void averageLifespanTest(){
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setMapHeight(6);
        builder.setMapWidth(6);
        builder.setStartAnimalCount(0);
        builder.setStartGrassCount(0);
        builder.setInitialEnergy(10);
        builder.setReproductionEnergy(500);
        builder.setReproductionCost(1);
        builder.setMoveCost(10);
        builder.setGenomeLength(1);
        builder.setDailyGrassGrowth(0);

        SimulationConfig config = builder.build();
        Animal.setConfig(config);
        Genome.setConfig(config);
        AbstractWorldMap map = new CrawlingJungleMap(config);

        Simulation sim = new Simulation(config, map, null);

        SimulationStatTracker tracker = sim.getStatTracker();

        Animal animal1 = new Animal(new Vector2d(1, 1), List.of(0));
        Animal animal2 = new Animal(new Vector2d(3, 4), List.of(0));
        Animal animal3 = new Animal(new Vector2d(2, 2), List.of(0));
        Animal animal4 = new Animal(new Vector2d(4, 1), List.of(0));
        Animal animal5 = new Animal(new Vector2d(2, 3), List.of(0));

        List<Animal> animals = new ArrayList<>(List.of(animal1, animal2, animal3, animal4, animal5));
        try {
            Field simAnimals = sim.getClass().getDeclaredField("aliveAnimals");
            simAnimals.setAccessible(true);
            simAnimals.set(sim, animals);
            for(Animal animal : animals) {
                map.place(animal);
            }

            Field energy = Animal.class.getDeclaredField("energy");
            energy.setAccessible(true);
            energy.set(animal1, 50);
            energy.set(animal2, 10);    //umrze po 1 ruchu (czyli na początku dnia 1)
            energy.set(animal3, 20);    //umrze po 2 ruchu (czyli na początku dnia 2)[indeksowane do 0]
            energy.set(animal4, 60);
            energy.set(animal5, 80);

            Method dayCycle = Simulation.class.getDeclaredMethod("performDayCycle");
            dayCycle.setAccessible(true);

            Field currentDay = Simulation.class.getDeclaredField("currentDay");
            currentDay.setAccessible(true);

            dayCycle.invoke(sim);
            //koniec dnia 0
            assertEquals(0, tracker.getAverageLifespan());

            currentDay.set(sim, 1);
            dayCycle.invoke(sim);
            //koniec dnia 1
            assertEquals(1, tracker.getAverageLifespan());

            currentDay.set(sim, 2);
            dayCycle.invoke(sim);
            //koniec dnia 2
            assertEquals(1.5, tracker.getAverageLifespan());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }
}