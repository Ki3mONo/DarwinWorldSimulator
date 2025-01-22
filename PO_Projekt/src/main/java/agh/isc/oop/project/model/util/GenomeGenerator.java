package agh.isc.oop.project.model.util;

import agh.isc.oop.project.model.elements.Animal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Klasa generująca genomy dla zwierząt.
 * Może wygenerować zupełnie losowy genom
 * dla startowych zwierzaków lub też genom
 * dziecka na podstawie genomu ich rodziców.
 */
public class GenomeGenerator {
    private final int genomeLength;
    private final Random random;

    /**
     * Konstruktor przyjmuje jedynie długość genomu.
     * @param genomeLength długość genomu
     */
    public GenomeGenerator(int genomeLength) {
        this.genomeLength = genomeLength;
        this.random = new Random();
    }

    /**
     * Metoda generująca w pełni losową listę genów
     * dla startowych zwierzaków
     * @return lista losowo wygenerowanych genów
     */
    public List<Integer> generateGenome() {
        List<Integer> genes = new ArrayList<>();
        for (int i = 0; i < genomeLength; i++) {
            genes.add(random.nextInt(8));
        }
        return genes;
    }

    /**
     * Metoda generująca listę genów zwierzaka,
     * na podstawie genów i energii jego rodziców.
     * @param parent1 jeden z rodziców
     * @param parent2 drugi z rodziców
     * @param mutationsNumber liczba mutacji
     * @return lista genów dziecka
     */
    public List<Integer> generateOffspringGenome(Animal parent1, Animal parent2, int mutationsNumber) {
        List<Integer> genes = new ArrayList<>();

        Animal strongerParent;
        Animal weakerParent;

        //Znalezienie silniejszego i słabszego rodzica
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
        int weakParentShare = (int) ((double) weakerParent.getEnergy() / (double) totalEnergy * genomeLength);

        //Losowanie, który z rodziców przekazuje lewą stronę genów, a który prawą
        if (random.nextBoolean()){
            //Geny słabszego rodzica od lewej strony
            int splitPoint = weakParentShare;

            genes.addAll(weakerParent.getGenome().getGeneList().subList(0, splitPoint));
            genes.addAll(strongerParent.getGenome().getGeneList().subList(splitPoint, genomeLength));

        } else {
            //Geny silniejszego rodzica z prawej
            int splitPoint = genomeLength - weakParentShare;

            genes.addAll(strongerParent.getGenome().getGeneList().subList(0, splitPoint));
            genes.addAll(weakerParent.getGenome().getGeneList().subList(splitPoint, genomeLength));
        }

        //Mutacja określonej liczby genów
        genes = mutate(genes, mutationsNumber);

        return genes;
    }

    /**
     * Metoda odpowiadająca za losowe mutacje genomu
     * @param genome lista genów do zmutowania
     * @param mutationsNumber liczba mutacji
     * @return lista genów, ze zmienioną określoną ilością genów
     */
    private List<Integer> mutate(List<Integer> genome, int mutationsNumber) {
        //Lista liczb od zera do length-1,
        List<Integer> indexes = new ArrayList<>(IntStream.rangeClosed(0, genomeLength - 1).boxed().toList());

        //Losowanie indeksów do zmiany
        Collections.shuffle(indexes);

        for (int i = 0; i < mutationsNumber; i++) {
            genome.set(indexes.get(i), (genome.get(indexes.get(i)) + random.nextInt(1, 8)) % 8);
        }

        return genome;
    }


}
