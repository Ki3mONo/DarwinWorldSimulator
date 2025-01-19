package agh.isc.oop.project.simulation;

import agh.isc.oop.project.model.AbstractWorldMap;
import agh.isc.oop.project.model.Vector2d;
import agh.isc.oop.project.model.WorldMapFactory;
import agh.isc.oop.project.model.MapType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SimulationApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Set application icon
        Image appIcon = new Image(getClass().getResourceAsStream("/icons/icon.png"));
        primaryStage.getIcons().add(appIcon);

        // Load background image
        Image backgroundImage = new Image(getClass().getResourceAsStream("/app/background.png"));
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setPreserveRatio(true);
        backgroundView.setSmooth(true);

        // Create layout
        StackPane root = new StackPane();
        backgroundView.fitWidthProperty().bind(root.widthProperty());
        backgroundView.fitHeightProperty().bind(root.heightProperty());
        root.getChildren().add(backgroundView);

        // Create menu
        VBox menuBox = new VBox(20);
        menuBox.setAlignment(javafx.geometry.Pos.CENTER);
        menuBox.setPadding(new javafx.geometry.Insets(20));

        // Define buttons
        Button newSimButton = createTransparentImageButton("/app/start.png");
        Button loadConfigButton = createTransparentImageButton("/app/json.png");
        Button loadSimulationButton = createTransparentImageButton("/app/csv.png");
        Button exitButton = createTransparentImageButton("/app/close.png");

        // Style buttons
        String buttonStyle = "-fx-font-size: 16px; -fx-min-width: 200px;";
        newSimButton.setStyle(buttonStyle);
        loadConfigButton.setStyle(buttonStyle);
        loadSimulationButton.setStyle(buttonStyle);
        exitButton.setStyle(buttonStyle);

        // Set button actions
        newSimButton.setOnAction(e -> openNewSimulationWindow());
        loadConfigButton.setOnAction(e -> onLoadSimulationFromJSON(primaryStage));
        loadSimulationButton.setOnAction(e -> onLoadSimulationFromCSV(primaryStage));
        exitButton.setOnAction(e -> primaryStage.close());

        // Add buttons to the menu
        menuBox.getChildren().addAll(newSimButton, loadConfigButton, loadSimulationButton, exitButton);
        root.getChildren().add(menuBox);

        // Create scene
        Scene scene = new Scene(root, 896, 512);
        primaryStage.setTitle("DarwinWorld – Menu Główne");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    private void openNewSimulationWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NewSimulationDialog.fxml"));
            Parent root = loader.load();

            NewSimulationDialogController controller = loader.getController();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("DarwinWorld – Ustaw Parametry Nowej Symulacji");
            Image appIcon = new Image(getClass().getResourceAsStream("/icons/icon.png"));
            dialogStage.getIcons().add(appIcon);
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.showAndWait();

            if (controller.isStartClicked()) {
                SimulationConfig config = controller.getSimulationConfig();
                startSimulation(config);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onLoadSimulationFromJSON(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik JSON z konfiguracją symulacji");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki JSON", "*.json"));

        File chosenFile = fileChooser.showOpenDialog(stage);
        if (chosenFile != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(chosenFile);

                SimulationConfigBuilder builder = new SimulationConfigBuilder();
                builder.setMapType(MapType.valueOf(jsonNode.get("mapType").asText()));
                builder.setMapWidth(jsonNode.get("mapWidth").asInt());
                builder.setMapHeight(jsonNode.get("mapHeight").asInt());
                builder.setStartGrassCount(jsonNode.get("startGrassCount").asInt());
                builder.setGrassEnergy(jsonNode.get("grassEnergy").asInt());
                builder.setDailyGrassGrowth(jsonNode.get("dailyGrassGrowth").asInt());
                builder.setStartAnimalCount(jsonNode.get("startAnimalCount").asInt());
                builder.setInitialEnergy(jsonNode.get("initialEnergy").asInt());
                builder.setReproductionCost(jsonNode.get("reproductionCost").asInt());
                builder.setMoveCost(jsonNode.get("moveCost").asInt());
                builder.setAgingAnimalVariant(jsonNode.get("agingAnimalVariant").asBoolean());
                builder.setMinMutations(jsonNode.get("minMutations").asInt());
                builder.setMaxMutations(jsonNode.get("maxMutations").asInt());
                builder.setGenomeLength(jsonNode.get("genomeLength").asInt());
                builder.setDayDurationMs(jsonNode.get("dayDurationMs").asLong());

                SimulationConfig config = builder.build();
                startSimulation(config);

            } catch (IOException e) {
                showErrorDialog("Błąd", "Nie udało się wczytać pliku JSON.");
                e.printStackTrace();
            }
        }
    }

    private void onLoadSimulationFromCSV(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik CSV z danymi symulacji");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki CSV", "*.csv"));

        File chosenFile = fileChooser.showOpenDialog(stage);
        if (chosenFile != null) {
            try {
                CSVParser csvParser = CSVParser.parse(Files.newBufferedReader(Paths.get(chosenFile.toURI())),
                        CSVFormat.DEFAULT.withHeader());

                SimulationConfigBuilder builder = new SimulationConfigBuilder();

                for (CSVRecord record : csvParser) {
                    builder.setMapWidth(Integer.parseInt(record.get("mapWidth")));
                    builder.setMapHeight(Integer.parseInt(record.get("mapHeight")));
                    builder.setStartGrassCount(Integer.parseInt(record.get("startGrassCount")));
                    builder.setGrassEnergy(Integer.parseInt(record.get("grassEnergy")));
                    builder.setDailyGrassGrowth(Integer.parseInt(record.get("dailyGrassGrowth")));
                    builder.setStartAnimalCount(Integer.parseInt(record.get("startAnimalCount")));
                    builder.setInitialEnergy(Integer.parseInt(record.get("initialEnergy")));
                    builder.setReproductionCost(Integer.parseInt(record.get("reproductionCost")));
                    builder.setMoveCost(Integer.parseInt(record.get("moveCost")));
                    builder.setAgingAnimalVariant(Boolean.parseBoolean(record.get("agingAnimalVariant")));
                    builder.setMinMutations(Integer.parseInt(record.get("minMutations")));
                    builder.setMaxMutations(Integer.parseInt(record.get("maxMutations")));
                    builder.setGenomeLength(Integer.parseInt(record.get("genomeLength")));
                    builder.setDayDurationMs(Long.parseLong(record.get("dayDurationMs")));
                    builder.setMapType(MapType.valueOf(record.get("mapType")));
                    break;
                }
                csvParser.close();

                SimulationConfig config = builder.build();
                startSimulation(config);

            } catch (IOException | NumberFormatException e) {
                showErrorDialog("Błąd", "Nie udało się poprawnie wczytać pliku CSV.");
                e.printStackTrace();
            }
        }
    }

    private void startSimulation(SimulationConfig config) {
        printConfigSetup(config);

        Vector2d mapSize = new Vector2d(config.getMapWidth(), config.getMapHeight());
        AbstractWorldMap worldMap = WorldMapFactory.createMap(config.getMapType(), mapSize, config.getStartGrassCount());

        Simulation simulation = new Simulation(config, worldMap, config.csvFilePath);
        SimulationEngine engine = new SimulationEngine();
        engine.addSimulation(simulation);
        engine.runAsync();

        SimulationMapWindow simWindow = new SimulationMapWindow(simulation, config, engine);
        simWindow.showAndStart();
    }

    private static void printConfigSetup(SimulationConfig config) {
        System.out.println("Config: " + config);
    }

    private void showErrorDialog(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }


    private Button createTransparentImageButton(String imagePath) {
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);

        Button button = new Button();
        button.setGraphic(imageView);
        button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        button.setMinSize(imageView.getFitWidth(), imageView.getFitHeight()); // Ensure button size matches image

        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
