package agh.isc.oop.project.simulation;

public class SimulationConfig {
    private final int mapWidth;
    private final int mapHeight;
    private final int startGrassCount;
    private final int grassEnergy;
    private final int dailyGrassGrowth;
    private final int startAnimalCount;
    private final int startEnergy;
    private final int reproductionEnergy;
    private final int reproductionCost;
    private final int minMutations;
    private final int maxMutations;
    private final int genomeLength;
    private final long dayDurationMs;

    public SimulationConfig(int mapWidth, int mapHeight, int startGrassCount, int grassEnergy,
                            int dailyGrassGrowth, int startAnimalCount, int startEnergy,
                            int reproductionEnergy, int reproductionCost, int minMutations,
                            int maxMutations, int genomeLength, long dayDurationMs) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.startGrassCount = startGrassCount;
        this.grassEnergy = grassEnergy;
        this.dailyGrassGrowth = dailyGrassGrowth;
        this.startAnimalCount = startAnimalCount;
        this.startEnergy = startEnergy;
        this.reproductionEnergy = reproductionEnergy;
        this.reproductionCost = reproductionCost;
        this.minMutations = minMutations;
        this.maxMutations = maxMutations;
        this.genomeLength = genomeLength;
        this.dayDurationMs = dayDurationMs;
    }

    public int getMapWidth() { return mapWidth; }
    public int getMapHeight() { return mapHeight; }
    public int getStartGrassCount() { return startGrassCount; }
    public int getGrassEnergy() { return grassEnergy; }
    public int getDailyGrassGrowth() { return dailyGrassGrowth; }
    public int getStartAnimalCount() { return startAnimalCount; }
    public int getStartEnergy() { return startEnergy; }
    public int getReproductionEnergy() { return reproductionEnergy; }
    public int getReproductionCost() { return reproductionCost; }
    public int getMinMutations() { return minMutations; }
    public int getMaxMutations() { return maxMutations; }
    public int getGenomeLength() { return genomeLength; }
    public long getDayDurationMs() { return dayDurationMs; }
}
