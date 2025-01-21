package agh.isc.oop.project.model.elements;

import agh.isc.oop.project.model.map.AbstractWorldMap;
import agh.isc.oop.project.model.map.CrawlingJungleMap;
import agh.isc.oop.project.model.map.EquatorForestMap;
import agh.isc.oop.project.model.util.Vector2d;
import agh.isc.oop.project.simulation.SimulationConfig;
import agh.isc.oop.project.simulation.SimulationConfigBuilder;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AgingAnimalTest {

    @Test
    void checkAging(){
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

        AbstractWorldMap map = new EquatorForestMap(config);

        Animal animal1 = new AgingAnimal(new Vector2d(2, 2), List.of(4));
        try{
            map.place(animal1);
            map.move(animal1);

            Field missChance = AgingAnimal.class.getDeclaredField("missMoveProbability");
            missChance.setAccessible(true);
            assertEquals(0.01d, missChance.get(animal1));


            for (int i = 0; i < 80; i++) {
                map.move(animal1);
            }
            //zaokrąglenie, bo floaty się psują (zwraca 0.800000000000005)
            assertEquals(0.8d, Math.round((double)missChance.get(animal1) * 1000)/1000.0);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }




    }

}