package agh.isc.oop.project.simulation;

import agh.isc.oop.project.model.map.MapType;

/**
 * Klasa konfiguracji symulacji, która zawiera w sobie parametry symulacji.
 */
public class SimulationConfig {
    //WorldMap
    private final int mapWidth;
    private final int mapHeight;
    private final int startGrassCount;
    private final int grassEnergy;
    private final int dailyGrassGrowth;
    private final int startAnimalCount;
    //Animal
    private final int initialEnergy;
    private final int reproductionEnergy;
    private final int reproductionCost;
    private final int moveCost;
    private final boolean agingAnimalVariant;
    //Genome
    private final int minMutations;
    private final int maxMutations;
    private final int genomeLength;
    //Simulation
    private final long dayDurationMs;
    private final String csvFilePath;
    private final MapType mapType;

    /**
     * Konstruktor konfiguracji symulacji, który tworzy nową konfigurację symulacji na podstawie podanych parametrów.
     * @param mapType typ mapy
     * @param mapWidth szerokość mapy
     * @param mapHeight wysokość mapy
     * @param startGrassCount początkowa liczba trawy
     * @param grassEnergy energia trawy
     * @param dailyGrassGrowth dzienny przyrost trawy
     * @param startAnimalCount początkowa liczba zwierząt
     * @param initialEnergy początkowa energia zwierząt
     * @param reproductionEnergy energia potrzebna do rozmnażania
     * @param reproductionCost koszt rozmnażania
     * @param moveCost koszt ruchu
     * @param agingAnimalVariant czy wariant z postarzaniem zwierząt
     * @param minMutations minimalna liczba mutacji
     * @param maxMutations maksymalna liczba mutacji
     * @param genomeLength długość genomu
     * @param dayDurationMs długość dnia w milisekundach
     * @param csvFilePath ścieżka do pliku CSV
     */
    public SimulationConfig(MapType mapType, int mapWidth, int mapHeight, int startGrassCount, int grassEnergy,
                            int dailyGrassGrowth, int startAnimalCount, int initialEnergy, int reproductionEnergy,
                            int reproductionCost, int moveCost, boolean agingAnimalVariant, int minMutations,
                            int maxMutations, int genomeLength, long dayDurationMs, String csvFilePath) {
        this.mapType = mapType;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.startGrassCount = startGrassCount;
        this.grassEnergy = grassEnergy;
        this.dailyGrassGrowth = dailyGrassGrowth;
        this.startAnimalCount = startAnimalCount;
        this.initialEnergy = initialEnergy;
        this.reproductionEnergy = reproductionEnergy;
        this.reproductionCost = reproductionCost;
        this.moveCost = moveCost;
        this.agingAnimalVariant = agingAnimalVariant;
        this.minMutations = minMutations;
        this.maxMutations = maxMutations;
        this.genomeLength = genomeLength;
        this.dayDurationMs = dayDurationMs;
        this.csvFilePath = csvFilePath;
    }

    //Gettery
    public int getMapWidth() { return mapWidth; }
    public int getMapHeight() { return mapHeight; }
    public int getStartGrassCount() { return startGrassCount; }
    public int getGrassEnergy() { return grassEnergy; }
    public int getDailyGrassGrowth() { return dailyGrassGrowth; }
    public int getStartAnimalCount() { return startAnimalCount; }
    public int getInitialEnergy() { return initialEnergy; }
    public int getReproductionEnergy() {
        return reproductionEnergy;
    }
    public int getReproductionCost() { return reproductionCost; }
    public int getMoveCost() { return moveCost; }
    public int getMinMutations() { return minMutations; }
    public int getMaxMutations() { return maxMutations; }
    public int getGenomeLength() { return genomeLength; }
    public long getDayDurationMs() { return dayDurationMs; }
    public boolean isAgingAnimalVariant() {
        return agingAnimalVariant;
    }
    public String getCsvFilePath() {
        return csvFilePath;
    }
    public MapType getMapType(){
        return mapType;
    }
}
