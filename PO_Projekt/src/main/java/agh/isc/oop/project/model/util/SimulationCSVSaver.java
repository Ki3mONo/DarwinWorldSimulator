package agh.isc.oop.project.model.util;

import agh.isc.oop.project.simulation.Simulation;
import agh.isc.oop.project.model.Animal;
import agh.isc.oop.project.model.AbstractWorldMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SimulationCSVSaver {
    private final Simulation simulation;
    private final String filePath;

    public SimulationCSVSaver(Simulation simulation, String filePath) {
        this.simulation = simulation;
        this.filePath = filePath;
    }

    public void saveDayStatistics() {
        boolean fileExists = new java.io.File(filePath).exists();
        try (FileWriter writer = new FileWriter(filePath, true);
             CSVPrinter csvPrinter = new CSVPrinter(writer,
                     CSVFormat.DEFAULT.withHeader("Day", "Animal Count", "Grass Count", "Average Lifespan"))) {

            if (!fileExists) {
                csvPrinter.printRecord("Day", "Animal Count", "Grass Count", "Average Lifespan");
            }

            int currentDay = simulation.getCurrentDay();
            AbstractWorldMap map = simulation.getMap();
            List<Animal> animals = simulation.getAnimals();
            long grassCount = map.getGrassCount();
            double averageLifespan = simulation.getAverageLifespan();

            csvPrinter.printRecord(currentDay, animals.size(), grassCount, averageLifespan);
            csvPrinter.flush();
        } catch (IOException e) {
            System.err.println("Error saving simulation statistics: " + e.getMessage());
        }
    }
}
