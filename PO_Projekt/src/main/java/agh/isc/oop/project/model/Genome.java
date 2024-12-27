package agh.isc.oop.project.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.Random;
import java.util.stream.IntStream;

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

    private final List<Integer> genome;

    private int currentGeneIndex;

    //Do genów zwierząt generowanych na początku
    public Genome(List<Integer> genome) {
        Random rand = new Random();
        this.currentGeneIndex = rand.nextInt(0, length);
        this.genome = genome;
    }

    //Do genów urodzonych zwierząt
    public Genome(Animal parent1, Animal parent2) {
        Random rand = new Random();
        this.currentGeneIndex = rand.nextInt(0, length);

        Animal strongerParent;
        Animal weakerParent;

        if (parent1.getEnergy() > parent2.getEnergy()) {
            strongerParent = parent1;
            weakerParent = parent2;
        } else {    //Jeśli mają taką samą energię, to ta kolejność nie ma znaczenia
            strongerParent = parent2;
            weakerParent = parent1;
        }

        //Całkowita energia rodziców
        int totalEnergy = strongerParent.getEnergy() + weakerParent.getEnergy();

        //Liczba genów dziedziczonych od słabszego rodzica
        int weakerParentShare = (weakerParent.getEnergy() / totalEnergy) * length;

        List<Integer> temp = new ArrayList<Integer>(length);

        final List<Integer> weakerParentGenome = weakerParent.getGenome().genome;
        final List<Integer> strongerParentGenome = strongerParent.getGenome().genome;

        boolean weakerFromLeft = rand.nextBoolean();

        if (weakerFromLeft) {
            for (int i = 0; i < weakerParentShare; i++) {
                temp.add(weakerParentGenome.get(i));
            }
            for (int i = weakerParentShare; i < length; i++) {
                temp.add(strongerParentGenome.get(i));
            }
        } else {
            for (int i = 0; i < length - weakerParentShare; i++) {
                temp.add(strongerParentGenome.get(i));
            }
            for (int i = length - weakerParentShare; i < length; i++) {
                temp.add(weakerParentGenome.get(i));
            }
        }

        this.genome = mutate(temp, rand.nextInt(minMutations, maxMutations + 1));;
    }

    //Mutacje genomu
    private List<Integer> mutate(List<Integer> genome, int mutationsNumber) {
        //Lista liczb od zera do length-1,
        List<Integer> indexes = new ArrayList<>(IntStream.rangeClosed(0, length - 1).boxed().toList());

        //Losowanie indeksów do zmiany (muszą być unikatowe, więc najlepiej przez shuffle)
        Collections.shuffle(indexes);

        Random rand = new Random();
        for (int i = 0; i < mutationsNumber; i++) {
            genome.set(indexes.get(i), rand.nextInt(8));
        }

        return genome;
    }

    //Wartość aktywnego genu
    public int getActiveGene(){
        return genome.get(currentGeneIndex);
    }

    //Zmiany genów po kolei
    public void updateCurrentGeneIndex(){
        currentGeneIndex = (currentGeneIndex + 1) % length;
    }

}
