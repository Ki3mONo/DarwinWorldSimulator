package agh.isc.oop.project.simulation;

import agh.isc.oop.project.model.map.MapType;

public class SimulationConfigBuilder {
    //WorldMap
    private MapType mapType;
    private int mapWidth;
    private int mapHeight;
    private int startGrassCount;
    private int grassEnergy;
    private int dailyGrassGrowth;
    private int startAnimalCount;
    //Animal
    private int initialEnergy;
    private int reproductionEnergy;
    private int reproductionCost;
    private int moveCost;
    private boolean agingAnimalVariant;
    //Genome
    private int minMutations;
    private int maxMutations;
    private int genomeLength;
    //Simulation
    private long dayDurationMs;
    private String csvFilePath;

    public SimulationConfig build(){
        return new SimulationConfig(mapType, mapWidth, mapHeight, startGrassCount, grassEnergy, dailyGrassGrowth,
                startAnimalCount, initialEnergy, reproductionEnergy, reproductionCost, moveCost,
                agingAnimalVariant, minMutations, maxMutations, genomeLength, dayDurationMs, csvFilePath);
    }

    public void setMapType(MapType mapType){
        this.mapType = mapType;
    }
    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }

    public void setStartGrassCount(int startGrassCount) {
        this.startGrassCount = startGrassCount;
    }

    public void setGrassEnergy(int grassEnergy) {
        this.grassEnergy = grassEnergy;
    }

    public void setDailyGrassGrowth(int dailyGrassGrowth) {
        this.dailyGrassGrowth = dailyGrassGrowth;
    }

    public void setStartAnimalCount(int startAnimalCount) {
        this.startAnimalCount = startAnimalCount;
    }

    public void setInitialEnergy(int initialEnergy) {
        this.initialEnergy = initialEnergy;
    }

    public void setReproductionEnergy(int reproductionEnergy) {
        this.reproductionEnergy = reproductionEnergy;
    }

    public void setReproductionCost(int reproductionCost) {
        this.reproductionCost = reproductionCost;
    }

    public void setMoveCost(int moveCost) {
        this.moveCost = moveCost;
    }

    public void setAgingAnimalVariant(boolean agingAnimalVariant) {
        this.agingAnimalVariant = agingAnimalVariant;
    }

    public void setMinMutations(int minMutations) {
        this.minMutations = minMutations;
    }

    public void setMaxMutations(int maxMutations) {
        this.maxMutations = maxMutations;
    }

    public void setGenomeLength(int genomeLength) {
        this.genomeLength = genomeLength;
    }

    public void setDayDurationMs(long dayDurationMs) {
        this.dayDurationMs = dayDurationMs;
    }

    public void setCsvFilePath(String filePath) {
        this.csvFilePath = filePath;
    }
}
