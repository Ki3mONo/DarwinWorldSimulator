package agh.isc.oop.project.model.util;

import agh.isc.oop.project.model.MapChangeListener;
import agh.isc.oop.project.simulation.Simulation;
import agh.isc.oop.project.model.Animal;
import agh.isc.oop.project.model.AbstractWorldMap;
import agh.isc.oop.project.simulation.SimulationStatTracker;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SimulationCSVSaver implements MapChangeListener {
    private final Simulation simulation;
    private final String filePath;

    public SimulationCSVSaver(Simulation simulation, String filePath) {
        this.simulation = simulation;
        this.filePath = filePath;
    }

    public void mapChanged(AbstractWorldMap map) {
        saveDayStatistics();
    }

    public void saveDayStatistics() {
        boolean fileExists = new java.io.File(filePath).exists();
        try (FileWriter writer = new FileWriter(filePath, true);
             CSVPrinter csvPrinter = new CSVPrinter(writer,
                     CSVFormat.DEFAULT.withHeader("Day", "Animal Count", "Grass Count", "Average Lifespan"))) {

            if (!fileExists) {
                csvPrinter.printRecord("Dayx", "Animal Count", "Grass Count", "Average Lifespan");
            }

            int currentDay = simulation.getCurrentDay();
            SimulationStatTracker stats = simulation.getStatTracker();

            csvPrinter.printRecord(currentDay, stats.getAnimalCount(), stats.getGrassCount(), stats.getAverageLifespan());
            csvPrinter.flush();
        } catch (IOException e) {
            System.err.println("Error saving simulation statistics: " + e.getMessage());
        }
    }
}
