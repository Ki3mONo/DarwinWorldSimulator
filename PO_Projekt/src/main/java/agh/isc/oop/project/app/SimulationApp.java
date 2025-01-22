package agh.isc.oop.project.app;

import agh.isc.oop.project.app.helper.SimulationAppUIHelper;
import agh.isc.oop.project.model.map.AbstractWorldMap;
import agh.isc.oop.project.model.map.WorldMapFactory;
import agh.isc.oop.project.simulation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Klasa główna aplikacji JavaFx
 * <p>
 * Inicjalizująca okno główne aplikacji.
 * Zawiera metodę main, uruchamiającą aplikację.
 * Klasa dziedziczy po klasie Application z JavaFX.
 * </p>
 */
public class SimulationApp extends Application {
    private final SimulationEngine engine = new SimulationEngine();

    /**
     * Metoda start, inicjalizująca okno główne aplikacji.
     * @param primaryStage Główne okno aplikacji.
     */
    @Override
    public void start(Stage primaryStage) {
        // Konfiguracja głównego okna aplikacji
        configurePrimaryStage(primaryStage);
        primaryStage.show();// Wyświetlenie okna

        // Listener zamknięcia okna - zamyka aplikację
        primaryStage.setOnCloseRequest(event -> {
            engine.shutdown();
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * Metoda konfigurująca główne okno aplikacji.
     * @param stage Główne okno aplikacji.
     */
    private void configurePrimaryStage(Stage stage) {
        // Ustawienie ikony okna
        Image appIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icon.png")));
        stage.getIcons().add(appIcon);

        // Utworzenie głównego układu
        StackPane root = new StackPane();
        ImageView backgroundView = SimulationAppUIHelper.createBackgroundImageView(getClass(), "/app/background.png");
        backgroundView.fitWidthProperty().bind(root.widthProperty());
        backgroundView.fitHeightProperty().bind(root.heightProperty());
        root.getChildren().add(backgroundView);

        // Utworzenie menu
        VBox menuBox = createMenu();
        root.getChildren().add(menuBox);

        // Ustawienie sceny i parametrów okna
        Scene scene = new Scene(root, 896, 512);
        stage.setTitle("DarwinWorld – Menu Główne");
        stage.setScene(scene);
    }

    /**
     * Metoda tworząca menu główne aplikacji.
     * @return Vbox Menu główne aplikacji.
     */
    private VBox createMenu() {
        // Utworzenie pionowego układu
        VBox menuBox = new VBox(20);
        menuBox.setAlignment(javafx.geometry.Pos.CENTER);
        menuBox.setPadding(new javafx.geometry.Insets(20));

        // Utworzenie przycisków
        Button newSimButton = SimulationAppUIHelper.createTransparentImageButton(getClass(), "/app/start.png");
        Button loadConfigButton = SimulationAppUIHelper.createTransparentImageButton(getClass(), "/app/json.png");
        Button exitButton = SimulationAppUIHelper.createTransparentImageButton(getClass(), "/app/close.png");

        // Dodanie akcji do przycisków
        newSimButton.setOnAction(e -> openNewSimulationWindow());
        loadConfigButton.setOnAction(e -> onLoadSimulationFromJSON((Stage) exitButton.getScene().getWindow()));
        exitButton.setOnAction(e -> ((Stage) exitButton.getScene().getWindow()).close());

        // Dodanie przycisków do menu
        menuBox.getChildren().addAll(newSimButton, loadConfigButton, exitButton);

        // Zwrócenie menu
        return menuBox;
    }

    /**
     * Metoda otwierająca okno dialogowe do ustawienia parametrów nowej symulacji.
     */
    private void openNewSimulationWindow() {
        try {
            // Tworzymy nowy dialog, przekazując klasę, aby wewnątrz były dostępne zasoby (FXML, ikony)
            NewSimulationDialog dialog = new NewSimulationDialog(getClass());
            dialog.showAndWait();

            // Jeśli użytkownik kliknął przycisk start, pobieramy konfigurację i uruchamiamy symulację
            if (dialog.isStartClicked()) {
                SimulationConfig config = dialog.getSimulationConfig();
                startSimulation(config);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda wczytująca konfigurację symulacji z pliku JSON.
     * @param stage Okno, z którego wywołano metodę.
     */
    private void onLoadSimulationFromJSON(Stage stage) {
        // Tworzymy obiekt loadera i wczytujemy konfigurację z pliku JSON
        SimulationConfigLoader loader = new SimulationConfigLoader();
        try {
            // Wczytujemy konfigurację z pliku JSON dzięki
            SimulationConfig config = loader.loadConfigFromJSON(stage);
            if (config != null) {
                startSimulation(config); // Jeśli udało się wczytać konfigurację, uruchamiamy symulację
            }
        } catch (IOException e) {
            // W przypadku błędu wyświetlamy okno z informacją
            showErrorDialog("Błąd", "Nie udało się wczytać pliku JSON.");
            e.printStackTrace();
        }
    }

    /**
     * Metoda uruchamiająca symulację z podaną konfiguracją.
     * @param config Konfiguracja symulacji.
     */
    private void startSimulation(SimulationConfig config) {
        // Tworzymy mapę i symulację
        AbstractWorldMap worldMap = WorldMapFactory.createMap(config.getMapType(), config);
        Simulation simulation = new Simulation(config, worldMap, config.getCsvFilePath());
        // Tworzymy okno symulacji
        SimulationMapWindow simWindow = new SimulationMapWindow(simulation, config, engine);
        // Uruchamiamy symulację
        simWindow.showAndStart();
    }
    /**
     * Metoda wyświetlająca okno z błędem.
     * @param title Tytuł okna.
     * @param message Treść okna.
     */
    private void showErrorDialog(String title, String message) {
        // Wyświetlamy okno z błędem w wątku JavaFX
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Metoda main, uruchamiająca aplikację.
     */
    public static void main(String[] args) {
        // Uruchomienie aplikacji
        launch(args);
    }
}
