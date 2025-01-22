package agh.isc.oop.project.app.helper;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa pomocnicza do obsługi siatki mapy w oknie symulacji.
 * Zawiera metody pomocnicze do obliczania rozmiaru komórki siatki.
 */
public class SimulationMapWindowGridHelper {

    /**
     * Metoda obliczająca rozmiar komórki siatki.
     * @param container Kontener, w którym znajduje się siatka.
     * @param mapWidth Szerokość mapy.
     * @param mapHeight Wysokość mapy.
     * @return Rozmiar komórki siatki.
     */
    public static double calculateCellSize(StackPane container, int mapWidth, int mapHeight) {
        double width = container.getWidth();
        double height = container.getHeight();
        return Math.min(width / mapWidth, height / mapHeight);
    }

    /**
     * Metoda tworząca ograniczenia kolumn siatki.
     * @param cellSize Rozmiar komórki siatki.
     * @param mapWidth Szerokość mapy.
     * @return Lista ograniczeń kolumn siatki.
     */
    public static List<ColumnConstraints> createColumnConstraints(double cellSize, int mapWidth) {
        List<ColumnConstraints> constraints = new ArrayList<>();
        for (int i = 0; i < mapWidth; i++) {
            ColumnConstraints column = new ColumnConstraints(cellSize);
            column.setHgrow(Priority.ALWAYS);
            column.setFillWidth(true);
            constraints.add(column);
        }
        return constraints;
    }

    /**
     * Metoda tworząca ograniczenia wierszy siatki.
     * @param cellSize Rozmiar komórki siatki.
     * @param mapHeight Wysokość mapy.
     * @return Lista ograniczeń wierszy siatki.
     */
    public static List<RowConstraints> createRowConstraints(double cellSize, int mapHeight) {
        List<RowConstraints> constraints = new ArrayList<>();
        for (int i = 0; i < mapHeight; i++) {
            RowConstraints row = new RowConstraints(cellSize);
            row.setVgrow(Priority.ALWAYS);
            row.setFillHeight(true);
            constraints.add(row);
        }
        return constraints;
    }
}
