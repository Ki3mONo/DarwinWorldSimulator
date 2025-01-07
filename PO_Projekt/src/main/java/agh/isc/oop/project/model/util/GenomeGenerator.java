package agh.isc.oop.project.model.util;

import agh.isc.oop.project.model.Genome;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenomeGenerator {
    private final int genomeLength;
    private final Random random;

    public GenomeGenerator(int genomeLength) {
        this.genomeLength = genomeLength;
        this.random = new Random();
    }

    public List<Integer> generateGenome() {
        List<Integer> genes = new ArrayList<>();
        for (int i = 0; i < genomeLength; i++) {
            genes.add(random.nextInt(8));
        }
        return genes;
    }

    public List<Integer> generateOffspringGenome(List<Integer> parent1Genes, List<Integer> parent2Genes) {
        List<Integer> genes = new ArrayList<>();
        int splitPoint = random.nextInt(genomeLength);

        // Combine genes from both parents
        genes.addAll(parent1Genes.subList(0, splitPoint));
        genes.addAll(parent2Genes.subList(splitPoint, genomeLength));

        // Perform random mutations
        mutateGenes(genes);

        return genes;
    }

    private void mutateGenes(List<Integer> genes) {
        int mutations = random.nextInt(3) + 1;
        for (int i = 0; i < mutations; i++) {
            int index = random.nextInt(genes.size());
            genes.set(index, random.nextInt(8));
        }
    }
}
