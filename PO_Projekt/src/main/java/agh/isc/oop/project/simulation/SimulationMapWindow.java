package agh.isc.oop.project.simulation;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class SimulationMapWindow {
    private final Stage stage;         // Okno symulacji
    private final Simulation simulation;
    private final SimulationConfig config;
    private SimulationMapWindowController controller;
    private final SimulationEngine engine;

    public SimulationMapWindow(Simulation simulation, SimulationConfig config, SimulationEngine engine) {
        this.simulation = simulation;
        this.config = config;
        this.stage = new Stage();
        this.engine = engine;
        initialize();
    }

    private void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SimulationMapWindow.fxml"));
            Parent root = loader.load();

            // Pobieramy kontroler i przekazujemy symulację oraz konfigurację
            controller = loader.getController();
            controller.initialize(simulation, config);

            Scene scene = new Scene(root);
            stage.setTitle("DarwinWorld – Symulacja " + simulation.getMap().getID());
            Image appIcon = new Image(getClass().getResourceAsStream("/icons/icon.png"));
            stage.getIcons().add(appIcon);
            stage.setScene(scene);
            stage.setMinWidth(900);  // Minimalna szerokość okna
            stage.setMinHeight(600); // Minimalna wysokość okna

            // Listener zamknięcia okna - zatrzymuje symulację
            stage.setOnCloseRequest(e -> {
                simulation.stop();
                engine.stopAll();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAndStart() {
        stage.show();
        engine.addSimulation(simulation);
        engine.runAsync();
    }

    public void pauseSimulation() {
        if (controller != null) {
            controller.pauseSimulation();
        }
    }

    public void resumeSimulation() {
        if (controller != null) {
            controller.resumeSimulation();
        }
    }
}
