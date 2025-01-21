package agh.isc.oop.project.model.util;

import agh.isc.oop.project.simulation.Simulation;
import agh.isc.oop.project.model.map.AbstractWorldMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SimulationCSVSaver implements StatsChangeListener {
    private final File file;

    //Jeśli plik nie jest pusty, to go wyczyści
    public SimulationCSVSaver(String filePath) {
        this.file = new File(filePath);
        try(FileWriter writer = new FileWriter(file, false)){
            if (!file.exists())
                file.createNewFile();

            writer.write("Day,Animal Count,Grass Count,Free Fields,Most Popular Genes," +
                    "Average Energy,Average Lifespan,Average children count\n");

        } catch (IOException e) {
            System.err.println("Error creating file: " + filePath);
        }
    }

    public void statsChanged(SimulationStatTracker stats, int currentDay) {
        saveDayStatistics(stats, currentDay);
    }

    public void saveDayStatistics(SimulationStatTracker stats, int currentDay) {
        try (FileWriter writer = new FileWriter(file, true);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

            csvPrinter.printRecord(currentDay, stats.getAnimalCount(), stats.getGrassCount(),
                    stats.getFreeFields(), stats.getMostPopularGenes(), stats.getAverageEnergy(),
                    stats.getAverageLifespan(), stats.getAverageChildren());
            csvPrinter.flush();
        } catch (IOException e) {
            System.err.println("Error saving simulation statistics: " + e.getMessage());
        }
    }
}
