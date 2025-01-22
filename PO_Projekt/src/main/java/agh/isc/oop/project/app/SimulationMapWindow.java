package agh.isc.oop.project.app;

import agh.isc.oop.project.simulation.Simulation;
import agh.isc.oop.project.simulation.SimulationConfig;
import agh.isc.oop.project.simulation.SimulationEngine;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Klasa reprezentująca okno pojedynczej symulacji.
 * <p>
 * Okno symulacji pozwala na wyświetlenie mapy symulacji oraz sterowanie symulacją.
 * </p>
 */
public class SimulationMapWindow {
    /**
     * Minimalna szerokość okna symulacji.
     */
    private static final double MIN_WIDTH = 900;
    /**
     * Minimalna wysokość okna symulacji.
     */
    private static final double MIN_HEIGHT = 600;
    /**
     * Obiekt klasy Stage reprezentujący okno symulacji.
     */
    private final Stage stage;
    /**
     * Obiekt klasy Simulation reprezentujący symulację.
     */
    private final Simulation simulation;
    /**
     * Obiekt klasy SimulationConfig reprezentujący konfigurację symulacji.
     */
    private final SimulationConfig config;
    /**
     * Obiekt klasy SimulationMapWindowController reprezentujący kontroler okna symulacji.
     */
    private SimulationMapWindowController controller;
    /**
     * Obiekt klasy SimulationEngine reprezentujący silnik symulacji.
     */
    private final SimulationEngine engine;

    /**
     * Konstruktor klasy SimulationMapWindow.
     * <p>
     * Tworzy nowe okno pojedynczej symulacji.
     * </p>
     *
     * @param simulation Symulacja, którą ma reprezentować okno
     * @param config     Konfiguracja symulacji
     * @param engine     Silnik symulacji
     */
    public SimulationMapWindow(Simulation simulation, SimulationConfig config, SimulationEngine engine) {
        this.simulation = simulation;
        this.config = config;
        this.stage = new Stage();
        this.engine = engine;
        initialize();
    }

    /**
     * Metoda inicjalizująca okno symulacji.
     */
    private void initialize() {
        Parent root = loadFXML();
        if(root != null) {
            configureController();
            configureScene(root);
            configureStage();
        }
    }

    /**
     * Ładuje plik FXML i zwraca główny węzeł.
     *
     * @return Główny węzeł (root) lub null w przypadku błędu ładowania
     */
    private Parent loadFXML() {
        try {
            // Ładowanie pliku FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SimulationMapWindow.fxml"));

            // Pobranie głównego węzła
            Parent root = loader.load();

            // Pobranie kontrolera
            this.controller = loader.getController();

            // Zwrócenie głównego węzła
            return root;
        } catch (IOException e) {
            // W przypadku błędu ładowania pliku FXML
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Inicjalizacja kontrolera okna symulacji.
     */
    private void configureController() {
        if(controller != null) {
            controller.initialize(simulation, config);
        }
    }

    /**
     * Konfiguruje scenę oraz ustawia ją w etapie.
     *
     * @param root Główny węzeł sceny
     */
    private void configureScene(Parent root) {
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    /**
     * Konfiguruje właściwości okna (etapu).
     */
    private void configureStage() {
        // Ustawienie tytułu okna symulacji zgodnie z identyfikatorem mapy
        stage.setTitle("DarwinWorld – Symulacja " + simulation.getMap().getID());

        // Ustawienie ikony aplikacji
        Image appIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icon.png")));

        // Dodanie ikony do okna
        stage.getIcons().add(appIcon);

        // Ustawienie minimalnych rozmiarów okna
        stage.setMinWidth(MIN_WIDTH);  // Minimalna szerokość okna
        stage.setMinHeight(MIN_HEIGHT); // Minimalna wysokość okna

        // Ustawienie akcji zamknięcia okna
        stage.setOnCloseRequest(e -> simulation.stop());
    }

    /**
     * Wyświetla okno symulacji i dodaje symulację do silnika, który ją rozpoczyna.
     */
    public void showAndStart() {
        stage.show();
        engine.addSimulation(simulation);
    }
}
