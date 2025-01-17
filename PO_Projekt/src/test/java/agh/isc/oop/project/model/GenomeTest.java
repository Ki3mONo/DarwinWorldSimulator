package agh.isc.oop.project.model;

import agh.isc.oop.project.simulation.SimulationConfig;
import agh.isc.oop.project.simulation.SimulationConfigBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenomeTest {

    @Test
    void reproductionTest(){
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setInitialEnergy(10);
        builder.setGenomeLength(5);
        builder.setAgingAnimalVariant(false);
        SimulationConfig config = builder.build();

        Animal.setConfig(config);
        Genome.setConfig(config);

        Animal parent1 = new Animal(new Vector2d(1, 1), List.of(1, 2, 3, 4, 5));
        Animal parent2 = new Animal(new Vector2d(1, 1), List.of(1, 2, 3, 4, 5));

        Animal child = new Animal(parent1, parent2, 0);

        assertEquals(List.of(1, 2, 3, 4, 5), child.getGenome().getGeneList());
    }

    @Test
    void mutationTest(){
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setInitialEnergy(10);
        builder.setMinMutations(3);
        builder.setMaxMutations(3);
        builder.setGenomeLength(5);
        builder.setAgingAnimalVariant(false);
        SimulationConfig config = builder.build();
        Animal.setConfig(config);
        Genome.setConfig(config);

        Animal parent1 = new Animal(new Vector2d(1, 1), List.of(1, 2, 3, 4, 5));
        Animal parent2 = new Animal(new Vector2d(1, 1), List.of(1, 2, 3, 4, 5));

        Animal child = new Animal(parent1, parent2, 0);

        int unchanged = 0;
        for (int i = 0; i < 5; i++) {
            if(child.getGenome().getGeneList().get(i).equals(parent1.getGenome().getGeneList().get(i))){
                unchanged++;
            }
        }
        assertEquals(2, unchanged);
    }

    @Test
    void getActiveGeneTest(){
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setGenomeLength(5);
        builder.setAgingAnimalVariant(false);
        SimulationConfig config = builder.build();
        Animal.setConfig(config);
        Genome.setConfig(config);

        Genome genome = new Genome(List.of(1, 2, 3, 4, 5));

        assertEquals(genome.getGeneList().get(genome.getCurrentGeneIndex()), genome.getActiveGene());
    }

    @Test
    void updateCurrentGeneTest(){
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setGenomeLength(5);
        builder.setAgingAnimalVariant(false);
        SimulationConfig config = builder.build();
        Animal.setConfig(config);
        Genome.setConfig(config);

        Genome genome = new Genome(List.of(1, 2, 3, 4, 5));
        int current = genome.getCurrentGeneIndex();

        for (int i = 0; i < 5; i++) {
            genome.updateCurrentGeneIndex();
            assertEquals((current + 1) % 5, genome.getCurrentGeneIndex());
            current = genome.getCurrentGeneIndex();
        }
    }

    @Test
    void geneInheritanceTest(){
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setInitialEnergy(10);
        builder.setGenomeLength(8);
        builder.setAgingAnimalVariant(false);
        SimulationConfig config = builder.build();
        Animal.setConfig(config);
        Genome.setConfig(config);

        //oboje energia 10
        Animal parent1 = new Animal(new Vector2d(1, 1), List.of(0, 1, 2, 3, 4, 5, 6, 7));
        Animal parent2 = new Animal(new Vector2d(1, 1), List.of(7, 6, 5, 4, 3, 2, 1, 0));
        Animal child12 = new Animal(parent1, parent2, 0);

        builder.setInitialEnergy(5);
        Animal.setConfig(builder.build());
        //energia 5
        Animal parent3 = new Animal(new Vector2d(1, 1), List.of(0, 1, 2, 3, 4, 5, 6, 7));

        builder.setInitialEnergy(10);
        Animal.setConfig(builder.build());
        //energia 10
        Animal parent4 = new Animal(new Vector2d(1, 1), List.of(7, 6, 5, 4, 3, 2, 1, 0));
        Animal child34 = new Animal(parent3, parent4, 0);


        assertTrue(child12.getGenome().getGeneList().equals(List.of(0, 1, 2, 3, 3, 2, 1, 0))
        || child12.getGenome().getGeneList().equals(List.of(7, 6, 5, 4, 4, 5, 6, 7))
        );
        assertTrue(child34.getGenome().getGeneList().equals(List.of(0, 1, 5, 4, 3, 2, 1, 0))
                || child34.getGenome().getGeneList().equals(List.of(7, 6, 5, 4, 3, 2, 6, 7))
        );
    }


}