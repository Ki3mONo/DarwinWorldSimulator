package agh.isc.oop.project.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenomeTest {

    @Test
    void reproductionTest(){
        Genome.setLength(5);
        Genome.setMinMutations(0);
        Genome.setMaxMutations(0);
        Animal.setInitialEnergy(10);

        Animal parent1 = new Animal(new Vector2d(1, 1), List.of(1, 2, 3, 4, 5));
        Animal parent2 = new Animal(new Vector2d(1, 1), List.of(1, 2, 3, 4, 5));

        Animal child = new Animal(parent1, parent2);

        assertEquals(List.of(1, 2, 3, 4, 5), child.getGenome().getGenome());
    }

    @Test
    void mutationTest(){
        Genome.setLength(5);
        Genome.setMinMutations(3);
        Genome.setMaxMutations(3);
        Animal.setInitialEnergy(10);

        Animal parent1 = new Animal(new Vector2d(1, 1), List.of(1, 2, 3, 4, 5));
        Animal parent2 = new Animal(new Vector2d(1, 1), List.of(1, 2, 3, 4, 5));

        Animal child = new Animal(parent1, parent2);

        int unchanged = 0;
        for (int i = 0; i < 5; i++) {
            if(child.getGenome().getGenome().get(i).equals(parent1.getGenome().getGenome().get(i))){
                unchanged++;
            }
        }
        assertEquals(2, unchanged);
    }

    @Test
    void getActiveGeneTest(){
        Genome.setLength(5);
        Genome genome = new Genome(List.of(1, 2, 3, 4, 5));

        assertEquals(genome.getGenome().get(genome.getCurrentGeneIndex()), genome.getActiveGene());
    }

    @Test
    void updateCurrentGeneTest(){
        Genome.setLength(5);
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
        Genome.setLength(8);
        Genome.setMinMutations(0);
        Genome.setMaxMutations(0);

        Animal.setInitialEnergy(10);
        Animal parent1 = new Animal(new Vector2d(1, 1), List.of(0, 1, 2, 3, 4, 5, 6, 7));
        Animal parent2 = new Animal(new Vector2d(1, 1), List.of(7, 6, 5, 4, 3, 2, 1, 0));
        Animal child12 = new Animal(parent1, parent2);

        Animal.setInitialEnergy(5);
        Animal parent3 = new Animal(new Vector2d(1, 1), List.of(0, 1, 2, 3, 4, 5, 6, 7));
        Animal.setInitialEnergy(10);
        Animal parent4 = new Animal(new Vector2d(1, 1), List.of(7, 6, 5, 4, 3, 2, 1, 0));
        Animal child34 = new Animal(parent3, parent4);


        assertTrue(child12.getGenome().getGenome().equals(List.of(0, 1, 2, 3, 3, 2, 1, 0))
        || child12.getGenome().getGenome().equals(List.of(7, 6, 5, 4, 4, 5, 6, 7))
        );
        assertTrue(child34.getGenome().getGenome().equals(List.of(0, 1, 5, 4, 3, 2, 1, 0))
                || child34.getGenome().getGenome().equals(List.of(7, 6, 5, 4, 3, 2, 6, 7))
        );
    }


}