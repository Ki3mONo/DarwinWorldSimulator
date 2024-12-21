package agh.isc.oop.project.model;

import java.util.ArrayList;
import java.util.List;

import java.util.Random;

// Klasa reprezentuje genom zwierzaka
public class Genome {
    private static int length;
    public static void setLength(int length) {
        Genome.length = length;
    }

    private final List<Integer> genome;

    private int currentGene;

    //Do genów zwierząt generowanych na początku
    public Genome(List<Integer> genome) {
        this.genome = genome;
    }

    //Do genów urodzonych zwierząt
    public Genome(Animal parent1, Animal parent2) {
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

        Random rand = new Random();
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

        // Tutaj mutacje

        this.genome = temp;
    }



}
