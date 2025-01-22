package agh.isc.oop.project.app;

import agh.isc.oop.project.app.helper.SimulationMapWindowGridHelper;
import agh.isc.oop.project.app.helper.SimulationMapWindowHighlightHelper;
import agh.isc.oop.project.app.helper.TrackedAnimalInfoHelper;
import agh.isc.oop.project.model.elements.Animal;
import agh.isc.oop.project.model.elements.WorldElement;
import agh.isc.oop.project.model.map.AbstractWorldMap;
import agh.isc.oop.project.model.util.*;
import agh.isc.oop.project.simulation.Simulation;
import agh.isc.oop.project.model.util.SimulationStatTracker;
import agh.isc.oop.project.simulation.SimulationConfig;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;

import static agh.isc.oop.project.model.util.WorldElementBox.DEFAULT_BACKGROUND;

/**
 * Klasa kontrolera okna pojedynczej symulacji.
 * <p>
 * Kontroler obsługuje wyświetlanie mapy symulacji oraz statystyk symulacji.
 * Implementuje interfejs MapChangeListener, aby reagować na zmiany mapy symulacji.
 * </p>
 */
public class SimulationMapWindowController implements MapChangeListener {

    // Wstrzykiwanie elementów z pliku FXML
    @FXML private Label mostPopularGenotypeLabel;
    @FXML private Label avgChildrenLabel;
    @FXML private StackPane gridContainer;
    @FXML private GridPane mapGrid;
    @FXML private Label animalCountLabel;
    @FXML private Label grassCountLabel;
    @FXML private Label freeFieldsLabel;
    @FXML private Label avgEnergyLabel;
    @FXML private Label avgLifespanLabel;
    @FXML private LineChart<Number, Number> animalChart;
    @FXML private LineChart<Number, Number> grassChart;
    @FXML private Label trackedGenotypeLabel;
    @FXML private Label trackedActiveGeneLabel;
    @FXML private Label trackedEnergyLabel;
    @FXML private Label trackedGrassEatenLabel;
    @FXML private Label trackedChildrenLabel;
    @FXML private Label trackedDescendantsLabel;
    @FXML private Label trackedAgeLabel;
    @FXML private Label trackedDeathDayLabel;
    @FXML private Button highlightPreferredFieldsButton;
    @FXML private Button highlightDominantGenotypeButton;
    @FXML private Button resumeButton;
    @FXML private Button pauseButton;
    @FXML private Button stopTrackingButton;

    private Animal trackedAnimal;
    private Simulation simulation;
    private AbstractWorldMap worldMap;
    private SimulationConfig config;
    private boolean isPaused = false;
    // Wykresy
    private XYChart.Series<Number, Number> animalSeries;
    private XYChart.Series<Number, Number> grassSeries;

    // Śledzenie zaznaczonego elementu
    private WorldElementBox trackedBox = null;

    /**
     * Inicjalizacja kontrolera.
     * <p>
     * Metoda wywoływana po załadowaniu pliku FXML.
     * </p>
     *
     * @param simulation Symulacja, do której przypisany jest kontroler
     * @param config     Konfiguracja symulacji
     */
    public void initialize(Simulation simulation, SimulationConfig config) {
        // Przypisanie symulacji i konfiguracji
        this.simulation = simulation;
        this.config = config;
        worldMap = simulation.getMap();

        // Inicjalizacja wykresów
        animalSeries = new XYChart.Series<>();
        animalSeries.setName("Zwierzęta");
        animalChart.getData().add(animalSeries);
        animalChart.setCreateSymbols(false);

        grassSeries = new XYChart.Series<>();
        grassSeries.setName("Rośliny");
        grassChart.getData().add(grassSeries);
        grassChart.setCreateSymbols(false);

        simulation.getMap().addObserver(this);

        // Listener zmiany rozmiaru kontenera
        gridContainer.widthProperty().addListener((obs, oldVal, newVal) -> resizeGrid());
        gridContainer.heightProperty().addListener((obs, oldVal, newVal) -> resizeGrid());

        // Inicjalizacja gridu
        createGrid();
    }

    /**
     * Tworzy siatkę mapy na podstawie konfiguracji symulacji.
     * Używa helpera do obliczenia rozmiaru komórki.
     */
    private void createGrid() {
        // Czyszczenie siatki
        mapGrid.getChildren().clear();
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();

        // Użycie GridHelper do obliczenia rozmiaru komórki
        double cellSize = SimulationMapWindowGridHelper.calculateCellSize(gridContainer, config.getMapWidth(), config.getMapHeight());

        // Ustawienie ograniczeń kolumn i wierszy przy użyciu helpera
        mapGrid.getColumnConstraints().addAll(SimulationMapWindowGridHelper.createColumnConstraints(cellSize, config.getMapWidth()));
        mapGrid.getRowConstraints().addAll(SimulationMapWindowGridHelper.createRowConstraints(cellSize, config.getMapHeight()));

        // Wypełnienie siatki komórkami
        drawMap();
    }

    /**
     * Zmienia rozmiar komórek siatki mapy.
     */
    private void resizeGrid() {
        // Obliczenie nowego rozmiaru komórki
        double cellSize = SimulationMapWindowGridHelper.calculateCellSize(gridContainer, config.getMapWidth(), config.getMapHeight());

        // Ustawienie nowych rozmiarów komórek
        mapGrid.getColumnConstraints().forEach(column -> column.setPrefWidth(cellSize));
        mapGrid.getRowConstraints().forEach(row -> row.setPrefHeight(cellSize));

        // Ustawienie nowych rozmiarów elementów w komórkach
        for (javafx.scene.Node node : mapGrid.getChildren()) {
            if (node instanceof StackPane cell) {
                cell.setPrefSize(cellSize, cellSize);
                for (javafx.scene.Node child : cell.getChildren()) {
                    if (child instanceof ImageView imageView) {
                        imageView.setFitWidth(cellSize);
                        imageView.setFitHeight(cellSize);
                    }
                    if (child instanceof WorldElementBox box) {
                        box.setPrefSize(cellSize, cellSize);
                    }
                }
            }
        }
    }

    /**
     * Rysuje mapę symulacji na siatce.
     */
    private void drawMap() {
        Platform.runLater(() -> {
            // Czyszczenie siatki
            mapGrid.getChildren().clear();

            // Obliczenie rozmiaru komórki
            double cellSquareSide = SimulationMapWindowGridHelper.calculateCellSize(gridContainer, config.getMapWidth(), config.getMapHeight());

            // Wypełnienie siatki komórkami
            for (int x = 0; x < config.getMapWidth(); x++) {
                for (int y = 0; y < config.getMapHeight(); y++) {
                    // Utworzenie pozycji komórki
                    Vector2d position = new Vector2d(x, y);
                    // Pobranie elementów znajdujących się na danej pozycji
                    Optional<List<WorldElement>> elementsOpt = worldMap.objectAt(position);
                    // Finalne zmienne dla lambdy
                    int finalX = x;
                    int finalY = y;

                    elementsOpt.ifPresentOrElse(elements -> {
                        // Utworzenie komórki na podstawie elementów
                        WorldElementBox box = createBoxForCell(worldMap, position, cellSquareSide, elements);
                        // Utworzenie komórki na podstawie boxa
                        StackPane cell = createCell(box, cellSquareSide);
                        // Obsługa kliknięcia na komórkę do zaznaczenia zwierzęcia
                        cell.setOnMouseClicked(event -> handleCellClickAnimalSelection(cell, elements));
                        // Dodanie komórki do siatki
                        mapGrid.add(cell, finalX, finalY);
                    }, () -> {
                        // Utworzenie komórki z przezroczystym elementem
                        WorldElementBox box = WorldElementBoxFactory.createBox((int) cellSquareSide);
                        // Utworzenie komórki na podstawie boxa
                        StackPane cell = createCell(box, cellSquareSide);
                        // Dodanie komórki do siatki
                        mapGrid.add(cell, finalX, finalY);
                    });
                }
            }

            updateStatistics();
            updateTrackedAnimalInfo();
        });
    }

    /**
     * Tworzy komórkę na podstawie elementów znajdujących się na danej pozycji.
     * Wykorzystuje fabrykę WorldElementBoxFactory do tworzenia komórek.
     *
     * @param worldMap       mapa świata
     * @param position       pozycja komórki
     * @param cellSquareSide rozmiar komórki
     * @param elements       lista elementów znajdujących się na danej pozycji
     * @return komórka reprezentująca elementy na danej pozycji
     */
    private WorldElementBox createBoxForCell(AbstractWorldMap worldMap, Vector2d position, double cellSquareSide, List<WorldElement> elements) {
        return agh.isc.oop.project.model.util.WorldElementBoxFactory.createBox(
                worldMap,
                position,
                (int) cellSquareSide,
                elements,
                trackedAnimal
        );
    }

    /**
     * Tworzy komórkę na podstawie boxa.
     *
     * @param box            box reprezentujący elementy na danej pozycji
     * @param cellSquareSide rozmiar komórki
     * @return komórka reprezentująca elementy na danej pozycji
     */
    private StackPane createCell(WorldElementBox box, double cellSquareSide) {
        // Utworzenie komórki
        StackPane cell = new StackPane();
        // Ustawienie rozmiaru komórki
        cell.setPrefSize(cellSquareSide, cellSquareSide);
        cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        // Dodanie boxa do komórki
        VBox.setVgrow(box, Priority.ALWAYS);
        HBox.setHgrow(box, Priority.ALWAYS);
        // Ustawienie paddingu
        box.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cell.getChildren().add(box);
        // Zwrócenie komórki
        return cell;
    }

    /**
     * Obsługuje kliknięcie na komórkę w celu zaznaczenia zwierzęcia.
     *
     * @param cell     komórka, na którą kliknięto
     * @param elements lista elementów znajdujących się na danej pozycji
     */
    private void handleCellClickAnimalSelection(StackPane cell, List<WorldElement> elements) {
        Optional<Animal> selectedAnimalOpt = elements.stream()
                .filter(e -> e instanceof Animal)
                .map(e -> (Animal) e)
                .max(Comparator.comparingInt(Animal::getEnergy)
                        .thenComparingInt(Animal::getBirthDate)
                        .thenComparingInt(Animal::getChildrenCount));

        // Jeśli znaleziono zwierzę, zaznacz je
        if (selectedAnimalOpt.isPresent()) {
            // Zaznaczenie komórki
            Animal selectedAnimal = selectedAnimalOpt.get();

            // Jeśli komórka jest już zaznaczona, zaznacz nową komórkę
            if (!cell.getChildren().isEmpty() && cell.getChildren().getFirst() instanceof WorldElementBox box) {
                highlightTrackedBox(box, selectedAnimal);
            }
        }
    }

    /**
     * Zaznacza komórkę z nowo wybranym zwierzęciem.
     *
     * @param newBox    nowa komórka do zaznaczenia
     * @param newAnimal nowe zwierzę do śledzenia
     */
    private void highlightTrackedBox(WorldElementBox newBox, Animal newAnimal) {
        unhighlightTrackedBox(); // Odznaczenie poprzedniej komórki
        newBox.highlightTrackedAnimal(); // Zaznaczenie nowej komórki
        trackedBox = newBox; // Ustawienie nowej komórki
        trackAnimal(newAnimal); // Śledzenie nowego zwierzęcia
    }

    /**
     * Odznacza zaznaczoną komórkę.
     */
    private void unhighlightTrackedBox() {
        if (trackedBox != null) {
            trackedBox.setBackground(DEFAULT_BACKGROUND);
            trackedBox = null;
            // Aby wprowadzić zmiany na GUI, należy wywołać drawMap()
            drawMap();
        }
    }

    /**
     * Aktualizuje statystyki symulacji.
     */
    private void updateStatistics() {
        Platform.runLater(() -> {
            SimulationStatTracker stats = simulation.getStatTracker();

            animalCountLabel.setText("Zwierzęta: " + stats.getAnimalCount());
            grassCountLabel.setText("Rośliny: " + stats.getGrassCount());
            freeFieldsLabel.setText("Wolne pola: " + stats.getFreeFields());
            mostPopularGenotypeLabel.setText("Genotyp: " + stats.getMostPopularGenes().toString());
            avgEnergyLabel.setText(String.format("Średnia energia: %.4f", stats.getAverageEnergy()));
            avgLifespanLabel.setText(String.format("Średnia długość życia: %.4f", stats.getAverageLifespan()));
            avgChildrenLabel.setText(String.format("Średnia liczba dzieci: %.2f", stats.getAverageChildren()));

            animalSeries.getData().add(new XYChart.Data<>(simulation.getCurrentDay(), stats.getAnimalCount()));
            grassSeries.getData().add(new XYChart.Data<>(simulation.getCurrentDay(), stats.getGrassCount()));
        });
    }

    /**
     * Pauzuje symulację.
     */
    @FXML
    void pauseSimulation() {
        isPaused = true;
        simulation.pause();
        updatePauseButtonState();
        updateResumeButtonState();
        updateHighlightButtonsState();
    }

    /**
     * Aktualzuje stan przycisku wznawiania symulacji.
     */
    @FXML
    private void updateResumeButtonState() {
        resumeButton.setDisable(!isPaused);
    }

    /**
     * Wznawia symulację.
     */
    @FXML
    void resumeSimulation() {
        isPaused = false;
        simulation.resume();
        removeHighlighting();
        updatePauseButtonState();
        updateResumeButtonState();
        updateHighlightButtonsState();
        drawMap();
    }

    /**
     * Aktualizuje stan przycisku pauzy.
     */
    @FXML
    private void updatePauseButtonState() {
        pauseButton.setDisable(isPaused);
    }

    /**
     * Zatrzymuje symulację i zamyka okno.
     */
    @FXML
    void closeWindow() {
        simulation.stop();
        ((Stage) mapGrid.getScene().getWindow()).close();
    }

    /**
     * Zaznacza zwierzęta z dominującym genotypem. Używa helpera do zaznaczania elementów.
     */
    @FXML
    void highlightDominantGenotype() {
        if (!isPaused) return;
        SimulationMapWindowHighlightHelper.highlightElements(mapGrid, this::isAnimalWithDominantGenotype, WorldElementBox::highlightDominantGenotype);
    }

    /**
     * Zaznacza preferowane pola. Używa helpera do zaznaczania elementów.
     */
    @FXML
    void highlightPreferredFields() {
        if (!isPaused) return;
        SimulationMapWindowHighlightHelper.highlightElements(mapGrid, this::isPreferredField, WorldElementBox::highlightPreferredField);
    }

    /**
     * Sprawdza, czy na danej pozycji znajduje się zwierzę z dominującym genotypem.
     *
     * @param pos pozycja
     * @return true, jeśli na danej pozycji znajduje się zwierzę z dominującym genotypem
     */
    private boolean isAnimalWithDominantGenotype(Vector2d pos) {
        // Pobranie elementów znajdujących się na danej pozycji
        AbstractWorldMap worldMap = simulation.getMap();
        List<WorldElement> elementsAtPos = worldMap.objectAt(pos).orElse(Collections.emptyList());
        // Pobranie zwierząt z najpopularniejszymi genami
        List<Animal> animalsWithPopularGenes = getAnimalsWithMostPopularGenes();

        // Sprawdzenie, czy na danej pozycji znajduje się zwierzę z popularnym genotypem
        return elementsAtPos.stream()
                .filter(e -> e instanceof Animal)
                .map(e -> (Animal) e)
                .anyMatch(animalsWithPopularGenes::contains);
    }

    /**
     * Sprawdza, czy dane pole jest preferowane przez trawę.
     *
     * @param pos pozycja
     * @return true, jeśli na danej pozycji znajduje się preferowane pole
     */
    private boolean isPreferredField(Vector2d pos) {
        AbstractWorldMap worldMap = simulation.getMap();
        Map<Vector2d, Integer> preferredGrassMap = worldMap.getPreferredGrassFields();
        return preferredGrassMap.containsKey(pos);
    }

    /**
     * Usuwa zaznaczenie zaznaczonych elementów.
     */
    private void removeHighlighting() {
        Platform.runLater(() -> {
            for (javafx.scene.Node node : mapGrid.getChildren()) {
                if (node instanceof StackPane cell) {
                    for (javafx.scene.Node child : cell.getChildren()) {
                        if (child instanceof WorldElementBox box) {
                            box.setBackground(DEFAULT_BACKGROUND);
                        }
                    }
                }
            }
        });
    }

    /**
     * Aktualizuje stan przycisków zaznaczania.
     */
    private void updateHighlightButtonsState() {
        boolean switcher = !isPaused;
        highlightPreferredFieldsButton.setDisable(switcher);
        highlightDominantGenotypeButton.setDisable(switcher);
    }

    /**
     * Śledzi zwierzę. Wybiera je tylko w trybie pauzy.
     *
     * @param animal zwierzę do śledzenia
     */
    public void trackAnimal(Animal animal) {
        if (isPaused) {
            trackedAnimal = animal;
            updateTrackedAnimalInfo();
        }
    }

    /**
     * Aktualizuje informacje o śledzonym zwierzęciu.
     */
    private void updateTrackedAnimalInfo() {
        if (trackedAnimal != null) {
            int currentDay = simulation.getCurrentDay();
            TrackedAnimalInfoHelper.updateTrackedAnimalInfo(
                    trackedAnimal,
                    trackedGenotypeLabel,
                    trackedActiveGeneLabel,
                    trackedEnergyLabel,
                    trackedGrassEatenLabel,
                    trackedChildrenLabel,
                    trackedDescendantsLabel,
                    trackedAgeLabel,
                    trackedDeathDayLabel,
                    currentDay
            );
        }
    }


    /**
     * Zatrzymuje śledzenie zwierzęcia.
     */
    @FXML
    private void stopTracking() {
        if (trackedAnimal != null) {
            Platform.runLater(this::unhighlightTrackedBox);
        }
        trackedAnimal = null;
        TrackedAnimalInfoHelper.resetTrackedAnimalInfo(
                trackedGenotypeLabel,
                trackedActiveGeneLabel,
                trackedEnergyLabel,
                trackedGrassEatenLabel,
                trackedChildrenLabel,
                trackedDescendantsLabel,
                trackedAgeLabel,
                trackedDeathDayLabel
        );
    }

    /**
     * Zwraca listę zwierząt z najpopularniejszymi genami.
     *
     * @return lista zwierząt z najpopularniejszymi genami
     */
    public List<Animal> getAnimalsWithMostPopularGenes() {
        // Pobranie najpopularniejszych genów
        List<Integer> popularGenes = simulation.getStatTracker().getMostPopularGenes();
        if (popularGenes == null || popularGenes.isEmpty())
            return Collections.emptyList();

        // Zwrócenie zwierząt z najpopularniejszymi genami
        return simulation.getAliveAnimals().stream()
                .filter(animal -> (animal.getGenome().getGeneList().equals(popularGenes)))
                .collect(Collectors.toList());
    }

    /**
     * Tworzy nowe okno z wykresem.
     */
    @Override
    public void mapChanged(AbstractWorldMap worldMap) {
        if (!isPaused) {
            drawMap();
        }
    }
}
