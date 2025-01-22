package agh.isc.oop.project.model.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Klasa odpowiedzialna za zapis statystyk do pliku CSV,
 * jeżeli użytkownik zdecyduje się na taką opcję.
 * Jest ona obserwatorem klasy SimulationStatsTracker
 */
public class SimulationCSVSaver implements StatsChangeListener {
    private final File file;

    /**
     * Konstruktor przyjmuje ścieżkę do pliku, do którego ma zapisywać statystyki.
     * Jeżeli taki plik już istnieje, to jego zawartość zostanie nadpisana.
     * @param filePath ścieżka do pliku
     */
    public SimulationCSVSaver(String filePath) {
        this.file = new File(filePath);

        //Otwarcie strumienia zapisu oraz wpisanie do pliku nagółka csv
        try(FileWriter writer = new FileWriter(file, false)){
            if (!file.exists())
                file.createNewFile();

            writer.write("Day,Animal Count,Grass Count,Free Fields,Most Popular Genes," +
                    "Average Energy,Average Lifespan,Average children count\n");

        } catch (IOException e) {
            System.err.println("Error creating file: " + filePath);
        }
    }

    /**
     * Metoda reagująca na powiadomienie o zmianie statystyk
     * wywołuje metodę saveDayStatistics
     * @param stats obserwowane statystyki
     * @param currentDay obecny dzień
     */
    public void statsChanged(SimulationStatTracker stats, int currentDay) {
        saveDayStatistics(stats, currentDay);
    }

    /**
     * Metoda dopisująca aktualne statystyki do pliku
     * @param stats obserwowane statystyki
     * @param currentDay obecny dzień
     */
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
