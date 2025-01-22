package agh.isc.oop.project;
import agh.isc.oop.project.app.SimulationApp;
import javafx.application.Application;

/** Główna klasa aplikacji, uruchamiająca okno symulacji */
public class DarwinWorld {
    public static void main(String[] args) {
        Application.launch(SimulationApp.class, args);
    }
}