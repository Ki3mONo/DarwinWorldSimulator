package agh.isc.oop.project.app;

import agh.isc.oop.project.model.map.MapType;
import agh.isc.oop.project.simulation.SimulationConfig;
import agh.isc.oop.project.simulation.SimulationConfigBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * Kontroler dla okna dialogowego nowej symulacji.
 * <p>
 * Obsługuje pola tekstowe, przyciski i zdarzenia związane z oknem dialogowym.
 * Buduje konfigurację symulacji na podstawie wartości wprowadzonych w oknie dialogowym,
 * a także umożliwia zapis konfiguracji do pliku JSON oraz wybór pliku do zapisu wyników symulacji w formacie CSV.
 * </p>
 */
public class NewSimulationDialogController {


    // Pola tekstowe i przyciski z pliku FXML
    @FXML private Spinner<Integer> mapWidthSpinner;
    @FXML private Spinner<Integer> mapHeightSpinner;
    @FXML private Spinner<Integer> startGrassSpinner;
    @FXML private Spinner<Integer> grassEnergySpinner;
    @FXML private Spinner<Integer> dailyGrassGrowthSpinner;
    @FXML private Spinner<Integer> startAnimalCountSpinner;
    @FXML private Spinner<Integer> initialEnergySpinner;
    @FXML private Spinner<Integer> reproductionEnergySpinner;
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

    /**
     * Grupa przełączników dla wyboru typu mapy
     */
    private ToggleGroup mapTypeToggleGroup;
    /**
     * Obiekt konfiguracji symulacji
     */
    private SimulationConfig simulationConfig = null;
    /**
     * Flaga informująca, czy przycisk "Rozpocznij Symulację" został kliknięty
     */
    private boolean startClicked = false;


    /**
     * Inicjalizacja okna dialogowego
     */
    @FXML
    public void initialize() {
        initializeSpinners();
        setSpinnersAsEditable();
        initializeToggleGroup();
        initializeCSVCheckBox();
        initializeButtons();
    }
    /**
     * Ustawienie wartości początkowych spinnerów i pól tekstowych
     */
    private void initializeSpinners() {
        // Ustaw wartości początkowe i zakresy spinnerów
        mapWidthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 10));
        mapHeightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 10));
        startGrassSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 2500, 20));
        grassEnergySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 20));
        dailyGrassGrowthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 10));
        startAnimalCountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 10));
        initialEnergySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 100));
        reproductionEnergySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 500, 30));
        reproductionCostSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 500, 20));
        moveCostSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200, 5));
        minMutationsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 1));
        maxMutationsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 4));
        genomeLengthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 10));
        // Ustaw wartość początkową dla CheckBoxa
        agingAnimalCheckBox.setSelected(false);
        // Domyślna wartość czasu dnia
        dayDurationField.setText("500");
    }

    /**
     * Ustawienie spinnerów jako edytowalne
     */
    private void setSpinnersAsEditable() {
        // Ustaw spinnery jako edytowalne
        mapWidthSpinner.setEditable(true);
        mapHeightSpinner.setEditable(true);
        startGrassSpinner.setEditable(true);
        grassEnergySpinner.setEditable(true);
        dailyGrassGrowthSpinner.setEditable(true);
        startAnimalCountSpinner.setEditable(true);
        initialEnergySpinner.setEditable(true);
        reproductionEnergySpinner.setEditable(true);
        reproductionCostSpinner.setEditable(true);
        moveCostSpinner.setEditable(true);
        minMutationsSpinner.setEditable(true);
        maxMutationsSpinner.setEditable(true);
        genomeLengthSpinner.setEditable(true);
    }

    /**
     * Inicjalizacja grupy przełączników (ToggleGroup) dla wyboru typu mapy
     */
    private void initializeToggleGroup() {
        // Utwórz grupę przełączników i dodaj do niej przyciski
        mapTypeToggleGroup = new ToggleGroup();
        equatorMapRadio.setToggleGroup(mapTypeToggleGroup);
        crawlingJungleMapRadio.setToggleGroup(mapTypeToggleGroup);
        // Ustaw domyślnie zaznaczony przycisk jako "Equator"
        equatorMapRadio.setSelected(true);
    }

    /**
     * Inicjalizacja CheckBoxa i główna logika do zapisu do pliku CSV
     */
    private void initializeCSVCheckBox() {
        // Ustawienie domyślnych wartości dla pól tekstowych i przycisku do wyboru pliku
        fileExplorerButton.setDisable(true);
        filePathField.setDisable(true);
        csvSaveCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            // Ustawia dostępność pól tekstowych i przycisku do wyboru pliku w zależności od zaznaczenia CheckBoxa
            filePathField.setDisable(!newValue);
            // Ustawia dostępność przycisku do wyboru pliku w zależności od zaznaczenia CheckBoxa
            fileExplorerButton.setDisable(!newValue);
        });
        // Akcja po kliknięciu przycisku do wyboru pliku
        fileExplorerButton.setOnAction(e -> openFileExplorerForCSV());
    }

    /**
     * Inicjalizacja przycisków i ich akcji
     */
    private void initializeButtons() {
        startButton.setOnAction(e -> onStartClicked());
        cancelButton.setOnAction(e -> onCancelClicked());
        saveButton.setOnAction(e -> onSaveClicked());
    }
    /**
     * Akcja po kliknięciu przycisku "Rozpocznij Symulację"
     * Sprawdza, czy opcja zapisu do CSV jest zaznaczona
     *          i jeśli tak to, czy ścieżka pliku jest podana i nie jest pusta,
     * Jeśli tak, to buduje konfigurację symulacji i ustawia flagę startClicked na true i zamyka okno NewSimulationDialog
     * W przeciwnym razie wyświetla komunikat o błędzie
     */
    private void onStartClicked() {
        // Sprawdzenie, czy opcja zapisu do CSV jest zaznaczona i czy ścieżka pliku jest podana i nie jest pusta
        if (csvSaveCheckBox.isSelected() && (filePathField.getText() == null || filePathField.getText().isEmpty())) {
            // Wyświetlenie komunikatu o błędzie
            showAlert("Błąd", "Opcja zapisu do CSV jest zaznaczona, ale nie wybrano ścieżki pliku.");
            return; // Przerwanie metody, jeśli wystąpił błąd
        }

        // Budowanie konfiguracji symulacji i ustawienie flagi startClicked na true
        buildSimulationConfig();
        startClicked = true;

        // Zamknięcie okna dialogowego
        closeWindow();
    }
    /**
     * Sprawdzenie, czy przycisk "Rozpocznij Symulację" został kliknięty
     * @return true, jeśli przycisk "Rozpocznij Symulację" został kliknięty, w przeciwnym razie false
     */
    public boolean isStartClicked() {
        return startClicked;
    }

    /**
     * Akcja po kliknięciu przycisku "Zamknij Okno"
     * Ustawia flagę startClicked na false i zamyka okno
     * Ustawia obiekt simulationConfig na null
     */
    private void onCancelClicked() {
        // Ustawienie flagi startClicked na false i obiektu simulationConfig na null
        startClicked = false;
        simulationConfig = null;
        // Zamknięcie okna dialogowego
        closeWindow();
    }

    /**
     * Zamyka okno dialogowe
     */
    private void closeWindow() {
        // Pobranie obiektu Stage i zamknięcie okna
        Stage stage = (Stage) startButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Akcja po kliknięciu przycisku "Zapisz Konfigurację"
     * Zapisuje konfigurację do pliku JSON
     * Wyświetla komunikat o błędzie, jeśli wystąpił
     */
    private void onSaveClicked() {
        try {
            // Budujemy konfigurację symulacji
            buildSimulationConfig();
            if (simulationConfig == null) {
                // Jeśli obiekt SimulationConfig jest null, wyświetlamy komunikat o błędzie
                throw new IllegalStateException("Błąd konfiguracji: Obiekt SimulationConfig jest null.");
            }

            // Otwieramy okno dialogowe do wyboru pliku JSON
            File file = openFileExplorerForJSONConfig();
            if (file != null) {
                // Jeśli wybrano plik, ustawiamy ścieżkę pliku w polu tekstowym i zapisujemy konfigurację do pliku
                filePathField.setText(file.getAbsolutePath());
                saveConfigToFile(file);
            } else {
                // Jeśli nie wybrano pliku, wyświetlamy komunikat o błędzie
                throw new Exception("Nie wybrano ścieżki pliku. Konfiguracja nie została zapisana.");
            }
        } catch (Exception e) {
            // Wyświetlamy komunikat o błędzie w formie Alertu
            showAlert("Błąd zapisu", "Wystąpił błąd podczas zapisywania konfiguracji:\n" + e.getMessage());
        }
    }

    /**
     * Otwiera okno dialogowe do wyboru pliku, w którym ma zostać zapisana konfiguracja w formacie JSON
     * @return wybrany plik File lub null, jeśli nie wybrano pliku
     */
    private File openFileExplorerForJSONConfig(){
        // Otwiera okno dialogowe do wyboru pliku JSON
        FileChooser fileChooser = new FileChooser(); //Utworzenie obiektu FileChooser
        fileChooser.setTitle("Zapisz konfigurację jako JSON"); //Ustawia tytuł okna dialogowego
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Plik JSON", "*.json")); //Filtruje pliki do formatu JSON
        fileChooser.setInitialFileName("simulation_config.json"); //Ustawia domyślną nazwę pliku

        // Zwraca wybrany plik lub null, jeśli nie wybrano pliku
        return fileChooser.showSaveDialog(saveButton.getScene().getWindow());
    }

    /**
     * Zapisuje konfigurację do pliku JSON
     * @param file plik, do którego ma zostać zapisana konfiguracja,
     * Jeśli zapis się powiedzie, wyświetla komunikat o sukcesie
     *             w przeciwnym razie komunikat o błędzie
     */
    private void saveConfigToFile(File file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper(); //Utworzenie ObjectMapper do zapisu obiektu do pliku JSON

            objectMapper.writeValue(file, simulationConfig); //Zapisuje obiekt do pliku JSON

            showAlert("Sukces", "Konfiguracja została zapisana do: " + file.getAbsolutePath()); //Wyświetla komunikat o sukcesie

        } catch (IOException e) {
            showAlert("Błąd zapisu", "Nie udało się zapisać konfiguracji: " + e.getMessage()); //Wyświetla komunikat o błędzie
        }
    }

    /**
     * Otwiera okno dialogowe do wyboru pliku, w którym ma zostać zapisana symulacja w formacie CSV
     */
    private void openFileExplorerForCSV() {
        // Otwiera okno dialogowe do wyboru pliku CSV
        FileChooser fileChooser = new FileChooser();//Utworzenie obiektu FileChooser
        fileChooser.setTitle("Wybierz plik do zapisu");//Ustawia tytuł okna dialogowego
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Plik CSV", "*.csv"));//Filtruje pliki do formatu CSV
        fileChooser.setInitialFileName("simulation_stats.csv"); //Ustawia domyślną nazwę pliku

        // Wybiera plik i ustawia ścieżkę w polu tekstowym
        File selectedFile = fileChooser.showSaveDialog(fileExplorerButton.getScene().getWindow());
        if (selectedFile != null) {
            // Jeśli wybrano plik, ustawia ścieżkę w polu tekstowym
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Buduje konfigurację symulacji na podstawie wartości wprowadzonych w oknie dialogowym
     * Wykorzystuje SimulationConfigBuilder do budowy konfiguracji
     * Ustawia obiekt simulationConfig na zbudowaną konfigurację,
     * Jeśli wartość czasu dnia jest mniejsza od zera, wyświetla komunikat o błędzie i ustawia domyślną wartość 500ms,
     * Jeśli wartość czasu dnia nie jest liczbą, wyświetla komunikat o błędzie i ustawia domyślną wartość 500ms
     */
    private void buildSimulationConfig() {
        // Utworzenie obiektu SimulationConfigBuilder i ustawienie wartości z pól tekstowych
        SimulationConfigBuilder builder = new SimulationConfigBuilder();
        builder.setMapWidth(mapWidthSpinner.getValue());
        builder.setMapHeight(mapHeightSpinner.getValue());
        builder.setStartGrassCount(startGrassSpinner.getValue());
        builder.setGrassEnergy(grassEnergySpinner.getValue());
        builder.setDailyGrassGrowth(dailyGrassGrowthSpinner.getValue());
        builder.setStartAnimalCount(startAnimalCountSpinner.getValue());
        builder.setInitialEnergy(initialEnergySpinner.getValue());
        builder.setReproductionEnergy(reproductionEnergySpinner.getValue());
        builder.setReproductionCost(reproductionCostSpinner.getValue());
        builder.setMoveCost(moveCostSpinner.getValue());
        builder.setAgingAnimalVariant(agingAnimalCheckBox.isSelected());
        builder.setMinMutations(minMutationsSpinner.getValue());
        builder.setMaxMutations(maxMutationsSpinner.getValue());
        builder.setGenomeLength(genomeLengthSpinner.getValue());
        builder.setCsvFilePath(csvSaveCheckBox.isSelected() ? filePathField.getText() : null);

        // Sprawdzenie wartości czasu dnia == potencjalne błędy
        long dayDurationMs = 500; // Domyślna wartość-zapobiega potencjalnym błędom
        try {
            // Parsowanie wartości z pola tekstowego
            long parsedValue = Long.parseLong(dayDurationField.getText());
            if (parsedValue > 0) {
                dayDurationMs = parsedValue; // Aktualizacja wartości tylko, jeśli jest poprawna
            } else {
                // Wyświetlenie komunikatu o błędzie w przypadku wartości mniejszej od zera
                showAlert("Błąd", "Czas dnia musi być większy od zera! Ustawiono 500ms.");
            }
            // Wyświetlenie komunikatu o błędzie w przypadku nieprawidłowej wartości np. nie liczby
        } catch (NumberFormatException e) {
            showAlert("Błąd", "Nieprawidłowa wartość! Ustawiono 500ms.");
        }
        // Ustawienie wartości czasu dnia w obiekcie SimulationConfigBuilder (jeśli nie było błędów),
        //          jeśli były błędy, to zostanie ustawiona domyślna wartość 500ms
        builder.setDayDurationMs(dayDurationMs);

        // Ustawienie typu mapy na podstawie wybranego przełącznika
        builder.setMapType(getMapTypeEnum());

        // Utworzenie obiektu SimulationConfig na podstawie zbudowanego obiektu SimulationConfigBuilder
        simulationConfig = builder.build();
    }

    /**
     * Zwraca obiekt SimulationConfig zbudowany na podstawie wartości wprowadzonych w oknie dialogowym
     * @return obiekt SimulationConfig
     */
    public SimulationConfig getSimulationConfig() {
        return simulationConfig;
    }

    /**
     * Zwraca typ mapy na podstawie wybranego przełącznika zgodnie z ENUM MapType
     * @return typ mapy jako MapType
     */
    private MapType getMapTypeEnum() {
        RadioButton selected = (RadioButton) mapTypeToggleGroup.getSelectedToggle(); // Pobranie zaznaczonego przycisku
        return (selected != null && selected.getText().equals("Pełzająca Dżungla")) ? MapType.CRAWLING_JUNGLE : MapType.EQUATOR_FOREST;  // Zwraca odpowiedni typ mapy
    }

    /**
     * Wyświetla okno dialogowe Alertu z podanym tytułem i treścią
     * @param title tytuł okna
     * @param content treść okna
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}