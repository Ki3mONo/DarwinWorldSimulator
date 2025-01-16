package agh.isc.oop.project.model;

import agh.isc.oop.project.model.util.GenomeGenerator;
import agh.isc.oop.project.simulation.SimulationConfig;

import java.util.List;

import java.util.Random;

// Klasa reprezentuje genom zwierzaka
public class Genome {
    private static SimulationConfig config;

    public static void setConfig(SimulationConfig config) {
        Genome.config = config;
    }

    private static final Random rand = new Random();

    private final List<Integer> genome;

    private int currentGeneIndex;

    private final GenomeGenerator genomeGenerator = new GenomeGenerator(config.getGenomeLength());

    //Do genów zwierząt generowanych na początku
    public Genome(List<Integer> genome) {
        this.currentGeneIndex = rand.nextInt(0, config.getGenomeLength());
        this.genome = genome;
    }

    //Do genów urodzonych zwierząt
    public Genome(Animal parent1, Animal parent2) {
        this.currentGeneIndex = rand.nextInt(0, config.getGenomeLength());

        this.genome = genomeGenerator.generateOffspringGenome(parent1, parent2,
                rand.nextInt(config.getMinMutations(), config.getMaxMutations() + 1));
    }


    //Wartość aktywnego genu
    public int getActiveGene(){
        return genome.get(currentGeneIndex);
    }

    //Zmiany genów po kolei
    public void updateCurrentGeneIndex(){
        currentGeneIndex = (currentGeneIndex + 1) % config.getGenomeLength();
    }

    //Gettery na potrzeby testów
    public List<Integer> getGeneList() {
        return genome;
    }

    public int getCurrentGeneIndex() {
        return currentGeneIndex;
    }
}
