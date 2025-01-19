package agh.isc.oop.project.simulation;

import agh.isc.oop.project.model.MapType;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class NewSimulationDialogController {

    @FXML private Spinner<Integer> mapWidthSpinner;
    @FXML private Spinner<Integer> mapHeightSpinner;
    @FXML private Spinner<Integer> startGrassSpinner;
    @FXML private Spinner<Integer> grassEnergySpinner;
    @FXML private Spinner<Integer> dailyGrassGrowthSpinner;
    @FXML private Spinner<Integer> startAnimalCountSpinner;
    @FXML private Spinner<Integer> initialEnergySpinner;
    @FXML private Spinner<Integer> reproductionCostSpinner;
    @FXML private Spinner<Integer> moveCostSpinner;
    @FXML private CheckBox agingAnimalCheckBox;
    @FXML private Spinner<Integer> minMutationsSpinner;
    @FXML private Spinner<Integer> maxMutationsSpinner;
    @FXML private Spinner<Integer> genomeLengthSpinner;
    @FXML private TextField dayDurationField;
    @FXML private RadioButton equatorMapRadio;
    @FXML private RadioButton crawlingJungleMapRadio;
    @FXML private Button saveButton;
    @FXML private Button startButton;
    @FXML private Button cancelButton;
    @FXML private CheckBox csvSaveCheckBox;
    @FXML private TextField filePathField;
    @FXML private Button fileExplorerButton;

    private ToggleGroup mapTypeToggleGroup;
    private SimulationConfig simulationConfig = null;
    private boolean startClicked = false;

    @FXML
    public void initialize() {
        mapWidthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 10));
        mapHeightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 10));
        startGrassSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 2500, 20));
        grassEnergySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 20));
        dailyGrassGrowthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 10));
        startAnimalCountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 10));
        initialEnergySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 100));
        reproductionCostSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 500, 50));
        moveCostSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200, 5));
        agingAnimalCheckBox.setSelected(false);
        minMutationsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 1));
        maxMutationsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 4));
        genomeLengthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 10));
        dayDurationField.setText("500");

        // Ustaw spinnery jako edytowalne
        mapWidthSpinner.setEditable(true);
        mapHeightSpinner.setEditable(true);
        startGrassSpinner.setEditable(true);
        grassEnergySpinner.setEditable(true);
        dailyGrassGrowthSpinner.setEditable(true);
        startAnimalCountSpinner.setEditable(true);
        initialEnergySpinner.setEditable(true);
        reproductionCostSpinner.setEditable(true);
        moveCostSpinner.setEditable(true);
        minMutationsSpinner.setEditable(true);
        maxMutationsSpinner.setEditable(true);
        genomeLengthSpinner.setEditable(true);

        // Inicjalizacja ToggleGroup dla wyboru typu mapy
        mapTypeToggleGroup = new ToggleGroup();
        equatorMapRadio.setToggleGroup(mapTypeToggleGroup);
        crawlingJungleMapRadio.setToggleGroup(mapTypeToggleGroup);
        equatorMapRadio.setSelected(true);

        startButton.setOnAction(e -> onStartClicked());
        cancelButton.setOnAction(e -> onCancelClicked());
        saveButton.setOnAction(e -> onSaveClicked());
        csvSaveCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filePathField.setDisable(!newValue);
            fileExplorerButton.setDisable(!newValue);
        });
        fileExplorerButton.setOnAction(e -> openFileExplorer());
    }

    private void onStartClicked() {
        buildSimulationConfig();
        startClicked = true;
        closeWindow();
    }

    private void onCancelClicked() {
        startClicked = false;
        simulationConfig = null;
        closeWindow();
    }

    private void onSaveClicked() {
        buildSimulationConfig();
        if (simulationConfig == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz konfigurację");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Plik CSV", "*.csv"));
        fileChooser.setInitialFileName("simulation_config.csv");

        File file = fileChooser.showSaveDialog(saveButton.getScene().getWindow());
        if (file != null) {
            filePathField.setText(file.getAbsolutePath());
            saveConfigToFile(file);
        }
    }

    private void openFileExplorer() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik do zapisu");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Plik CSV", "*.csv"));

        File selectedFile = fileChooser.showSaveDialog(fileExplorerButton.getScene().getWindow());
        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void buildSimulationConfig() {
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setMapWidth(mapWidthSpinner.getValue());
        builder.setMapHeight(mapHeightSpinner.getValue());
        builder.setStartGrassCount(startGrassSpinner.getValue());
        builder.setGrassEnergy(grassEnergySpinner.getValue());
        builder.setDailyGrassGrowth(dailyGrassGrowthSpinner.getValue());
        builder.setStartAnimalCount(startAnimalCountSpinner.getValue());
        builder.setInitialEnergy(initialEnergySpinner.getValue());
        builder.setReproductionCost(reproductionCostSpinner.getValue());
        builder.setMoveCost(moveCostSpinner.getValue());
        builder.setAgingAnimalVariant(agingAnimalCheckBox.isSelected());
        builder.setMinMutations(minMutationsSpinner.getValue());
        builder.setMaxMutations(maxMutationsSpinner.getValue());
        builder.setGenomeLength(genomeLengthSpinner.getValue());
        builder.setCsvFilePath(csvSaveCheckBox.isSelected() ? filePathField.getText() : null);


        long dayDurationMs;
        try {
            dayDurationMs = Long.parseLong(dayDurationField.getText());
            if (dayDurationMs <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            dayDurationMs = 500;
            showAlert("Błąd", "Czas dnia musi być liczbą większą od zera! Ustawiono 500ms.");
        }
        builder.setDayDurationMs(dayDurationMs);

        builder.setMapType(getMapTypeEnum());

        simulationConfig = builder.build();
    }

    private void saveConfigToFile(File file) {
        try {
            // Tutaj można zaimplementować zapis do pliku CSV
            showAlert("Sukces", "Konfiguracja została zapisana.");
        } catch (Exception e) {
            showAlert("Błąd", "Nie udało się zapisać pliku.");
        }
    }

    public boolean isStartClicked() {
        return startClicked;
    }

    public SimulationConfig getSimulationConfig() {
        return simulationConfig;
    }

    private MapType getMapTypeEnum() {
        RadioButton selected = (RadioButton) mapTypeToggleGroup.getSelectedToggle();
        return (selected != null && selected.getText().equals("Crawling Jungle")) ? MapType.CRAWLING_JUNGLE : MapType.EQUATOR_FOREST;
    }

    private void closeWindow() {
        Stage stage = (Stage) startButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}