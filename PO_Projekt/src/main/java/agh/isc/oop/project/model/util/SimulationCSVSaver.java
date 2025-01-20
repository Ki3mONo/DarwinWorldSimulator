package agh.isc.oop.project.model.util;

import agh.isc.oop.project.model.MapChangeListener;
import agh.isc.oop.project.simulation.Simulation;
import agh.isc.oop.project.model.Animal;
import agh.isc.oop.project.model.AbstractWorldMap;
import agh.isc.oop.project.simulation.SimulationStatTracker;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SimulationCSVSaver implements MapChangeListener {
    private final Simulation simulation;
    private final File file;


    //Jeśli plik nie jest pusty, to go wyczyści
    public SimulationCSVSaver(Simulation simulation, String filePath) {
        this.simulation = simulation;
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

    public void mapChanged(AbstractWorldMap map) {
        saveDayStatistics();
    }

    public void saveDayStatistics() {
        try (FileWriter writer = new FileWriter(file, true);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

            int currentDay = simulation.getCurrentDay();
            SimulationStatTracker stats = simulation.getStatTracker();

            csvPrinter.printRecord(currentDay, stats.getAnimalCount(), stats.getGrassCount(),
                    stats.getFreeFields(), stats.getMostPopularGenes(), stats.getAverageEnergy(),
                    stats.getAverageLifespan(), stats.getAverageChildren());
            csvPrinter.flush();
        } catch (IOException e) {
            System.err.println("Error saving simulation statistics: " + e.getMessage());
        }
    }
}
