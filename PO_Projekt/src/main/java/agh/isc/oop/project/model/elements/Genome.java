package agh.isc.oop.project.model.elements;

import agh.isc.oop.project.model.util.GenomeGenerator;
import agh.isc.oop.project.simulation.SimulationConfig;

import java.util.List;

import java.util.Random;

/**
 * Klasa reprezentuje genom zwierzaka.
 * Przechowuje listę genów oraz indeks obecnie aktywnego genu.
 * Klasa ma również konfigurację symulacji jako statyczny atrybut.
 * Do tworzenia genomu korzysta z klasy GenomeGenerator
 */
public class Genome {
    private static SimulationConfig config;

    public static void setConfig(SimulationConfig config) {
        Genome.config = config;
    }

    private static final Random rand = new Random();

    private final List<Integer> genome;

    private int currentGeneIndex;

    private final GenomeGenerator genomeGenerator = new GenomeGenerator(config.getGenomeLength());

    /**
     * Konstruktor do genomu startowych zwierząt
     * @param genome lista genów zwierzaka
     */
    public Genome(List<Integer> genome) {
        this.currentGeneIndex = rand.nextInt(0, config.getGenomeLength());
        this.genome = genome;
    }

    /**
     * Konstruktor do genomu urodzonych zwierzaków.
     * Przyjmuje obu rodziców i na ich podstawie
     * GenomeGenerator tworzy genom ich dziecka
     * @param parent1 jeden z rodziców
     * @param parent2 drugi z rodziców
     */
    public Genome(Animal parent1, Animal parent2) {
        this.currentGeneIndex = rand.nextInt(0, config.getGenomeLength());

        this.genome = genomeGenerator.generateOffspringGenome(parent1, parent2,
                rand.nextInt(config.getMinMutations(), config.getMaxMutations() + 1));
    }


    /**
     * Zwraca wartość obecnie aktywnego genu
     * @return obecnie aktywny gen
     */
    public int getActiveGene(){
        return genome.get(currentGeneIndex);
    }

    /**
     * Odpowiada za aktywowanie i dezaktywowanie genów.
     * Aktywny gen to ten, który w liście genów ma indeks
     * currentGeneIndex. Metoda zmienia ten indeks na kolejny
     * po prawej lub pierwszy, jeżeli aktywny jest ostatni gen.
     */
    public void updateCurrentGeneIndex(){
        currentGeneIndex = (currentGeneIndex + 1) % config.getGenomeLength();
    }

    /**
     * Metoda sumująca wszystkie geny,
     * która służy do wylosowania ikony danego zwierzaka
     * @return suma wszystkich genów
     */
    int genomeSum(){
        return genome.stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Metoda toString na potrzeby wypisania w statystykach
     * dominującego genomu
     * @return string z listy genów
     */
    @Override
    public String toString() {
        return genome.toString();
    }

    /**
     * Zwraca listę genów
     * @return lista genów
     */
    public List<Integer> getGeneList() {
        return genome;
    }

    /**
     * Zwraca indeks obecnie aktywnego genu
     * @return indeks aktywnego genu
     */
    public int getCurrentGeneIndex() {
        return currentGeneIndex;
    }

}
