package agh.isc.oop.project.model;

import agh.isc.oop.project.model.util.GenomeGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.Random;

// Klasa reprezentuje genom zwierzaka
public class Genome {
    private static int length;

    private static int minMutations;

    private static int maxMutations;

    public static void setLength(int length) {
        Genome.length = length;
    }

    public static void setMinMutations(int minMutations) {
        Genome.minMutations = minMutations;
    }

    public static void setMaxMutations(int maxMutations) {
        Genome.maxMutations = maxMutations;
    }

    private static final Random rand = new Random();

    private final List<Integer> genome;

    private int currentGeneIndex;

    private final GenomeGenerator genomeGenerator = new GenomeGenerator(length);

    //Do genów zwierząt generowanych na początku
    public Genome(List<Integer> genome) {
        this.currentGeneIndex = rand.nextInt(0, length);
        this.genome = genome;
    }

    //Do genów urodzonych zwierząt
    public Genome(Animal parent1, Animal parent2) {
        this.currentGeneIndex = rand.nextInt(0, length);

        this.genome = genomeGenerator.generateOffspringGenome(parent1, parent2,
                rand.nextInt(minMutations, maxMutations + 1));
    }


    //Wartość aktywnego genu
    public int getActiveGene(){
        return genome.get(currentGeneIndex);
    }

    //Zmiany genów po kolei
    public void updateCurrentGeneIndex(){
        currentGeneIndex = (currentGeneIndex + 1) % length;
    }

    //Gettery na potrzeby testów
    public List<Integer> getGeneList() {
        return genome;
    }

    public int getCurrentGeneIndex() {
        return currentGeneIndex;
    }
}
