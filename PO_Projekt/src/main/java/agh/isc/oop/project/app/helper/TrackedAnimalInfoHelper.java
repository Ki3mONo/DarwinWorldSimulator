package agh.isc.oop.project.app.helper;

import agh.isc.oop.project.model.elements.Animal;
import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 * Klasa pomocnicza do aktualizacji etykiet związanych ze śledzonym zwierzęciem.
 */
public class TrackedAnimalInfoHelper {

    /**
     * Aktualizuje etykiety związane ze śledzonym zwierzęciem.
     *
     * @param trackedAnimal      śledzone zwierzę (nie może być null)
     * @param genotypeLabel      etykieta wyświetlająca genom
     * @param activeGeneLabel    etykieta wyświetlająca aktywny gen
     * @param energyLabel        etykieta wyświetlająca energię
     * @param grassEatenLabel    etykieta wyświetlająca liczbę zjedzonych roślin
     * @param childrenLabel      etykieta wyświetlająca liczbę dzieci
     * @param descendantsLabel   etykieta wyświetlająca liczbę potomków
     * @param ageLabel           etykieta wyświetlająca wiek
     * @param deathDayLabel      etykieta wyświetlająca dzień śmierci
     * @param currentDay         bieżący dzień symulacji
     */
    public static void updateTrackedAnimalInfo(Animal trackedAnimal, Label genotypeLabel, Label activeGeneLabel,
                                               Label energyLabel, Label grassEatenLabel, Label childrenLabel,
                                               Label descendantsLabel, Label ageLabel, Label deathDayLabel,
                                               int currentDay) {
        Platform.runLater(() -> {
            genotypeLabel.setText("Genom: " + trackedAnimal.getGenome().toString());
            activeGeneLabel.setText("Aktywny gen: " + trackedAnimal.getGenome().getActiveGene());
            energyLabel.setText("Energia: " + trackedAnimal.getEnergy());
            grassEatenLabel.setText("Zjedzone rośliny: " + trackedAnimal.getGrassEaten());
            childrenLabel.setText("Dzieci: " + trackedAnimal.getChildrenCount());
            descendantsLabel.setText("Potomkowie: " + trackedAnimal.getDescendantsCount());
            ageLabel.setText(trackedAnimal.isAlive()
                    ? "Wiek: " + trackedAnimal.getAge(currentDay)
                    : "Wiek: Nie żyje");
            deathDayLabel.setText(trackedAnimal.isAlive()
                    ? "Dzień śmierci: Żyje"
                    : "Dzień śmierci: " + trackedAnimal.getDeathDay());
        });
    }

    /**
     * Resetuje etykiety śledzonego zwierzęcia.
     *
     * @param genotypeLabel    etykieta genomu
     * @param activeGeneLabel  etykieta aktywnego genu
     * @param energyLabel      etykieta energii
     * @param grassEatenLabel  etykieta liczby zjedzonych roślin
     * @param childrenLabel    etykieta liczby dzieci
     * @param descendantsLabel etykieta liczby potomków
     * @param ageLabel         etykieta wieku
     * @param deathDayLabel    etykieta dnia śmierci
     */
    public static void resetTrackedAnimalInfo(Label genotypeLabel, Label activeGeneLabel, Label energyLabel,
                                              Label grassEatenLabel, Label childrenLabel, Label descendantsLabel,
                                              Label ageLabel, Label deathDayLabel) {
        Platform.runLater(() -> {
            genotypeLabel.setText("Genom: -");
            activeGeneLabel.setText("Aktywny gen: -");
            energyLabel.setText("Energia: -");
            grassEatenLabel.setText("Zjedzone rośliny: -");
            childrenLabel.setText("Dzieci: -");
            descendantsLabel.setText("Potomkowie: -");
            ageLabel.setText("Wiek: -");
            deathDayLabel.setText("Dzień śmierci: -");
        });
    }
}
