package agh.isc.oop.project.app;

import agh.isc.oop.project.model.elements.Animal;
import agh.isc.oop.project.model.elements.Grass;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static agh.isc.oop.project.model.util.WorldElementBox.DEFAULT_BACKGROUND;

public class SimulationMapWindowController implements MapChangeListener {

    @FXML private Label mostPopularGenotypeLabel;
    @FXML private Label avgChildrenLabel;

    @FXML private StackPane gridContainer;
    @FXML private GridPane mapGrid;
    @FXML private Label animalCountLabel;
    @FXML private Label grassCountLabel;
    @FXML private Label freeFieldsLabel;
    @FXML private Label avgEnergyLabel;
    @FXML private Label avgLifespanLabel;
    @FXML private Button pauseButton;
    @FXML private Button resumeButton;
    @FXML private Button closeButton;
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
    @FXML private Button stopTrackingButton;

    private Animal trackedAnimal;


    private Simulation simulation;
    private SimulationConfig config;
    private boolean isPaused = false;
    private boolean isHighlightingPreferredFields = false;
    private boolean isHighlightingDominantGenotypes = false;

    private Set<Vector2d> preferredFields = new HashSet<>();
    private Set<Animal> dominantGenotypeAnimals = new HashSet<>();
    private static final Map<String, Background> backgroundCache = new ConcurrentHashMap<>();

    private XYChart.Series<Number, Number> animalSeries;
    private XYChart.Series<Number, Number> grassSeries;

    private WorldElementBox trackedBox = null;

    public void initialize(Simulation simulation, SimulationConfig config) {
        this.simulation = simulation;
        this.config = config;

        animalSeries = new XYChart.Series<>();
        animalSeries.setName("Zwierzęta");
        animalChart.getData().add(animalSeries);
        animalChart.setCreateSymbols(false);

        grassSeries = new XYChart.Series<>();
        grassSeries.setName("Rośliny");
        grassChart.getData().add(grassSeries);
        grassChart.setCreateSymbols(false);

        simulation.getMap().addObserver(this);

        gridContainer.widthProperty().addListener((obs, oldVal, newVal) -> resizeGrid());
        gridContainer.heightProperty().addListener((obs, oldVal, newVal) -> resizeGrid());

        createGrid();
    }

    private void createGrid() {
        mapGrid.getChildren().clear();
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();

        double cellSize = calculateCellSize();

        for (int x = 0; x < config.getMapWidth(); x++) {
            ColumnConstraints column = new ColumnConstraints(cellSize);
            column.setHgrow(Priority.ALWAYS);
            column.setFillWidth(true);
            mapGrid.getColumnConstraints().add(column);
        }

        for (int y = 0; y < config.getMapHeight(); y++) {
            RowConstraints row = new RowConstraints(cellSize);
            row.setVgrow(Priority.ALWAYS);
            row.setFillHeight(true);
            mapGrid.getRowConstraints().add(row);
        }

        drawMap();
    }



    private void resizeGrid() {
        double cellSize = calculateCellSize();

        for (ColumnConstraints column : mapGrid.getColumnConstraints()) {
            column.setPrefWidth(cellSize);
        }

        for (RowConstraints row : mapGrid.getRowConstraints()) {
            row.setPrefHeight(cellSize);
        }

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


    private void drawMap() {
        Platform.runLater(() -> {
            mapGrid.getChildren().clear();
            AbstractWorldMap worldMap = simulation.getMap();
            double cellSquareSide = calculateCellSize();

            for (int x = 0; x < config.getMapWidth(); x++) {
                for (int y = 0; y < config.getMapHeight(); y++) {
                    Vector2d position = new Vector2d(x, y);

                    Optional<List<WorldElement>> elementsOpt = worldMap.objectAt(position);
                    int finalX = x;
                    int finalY = y;

                    elementsOpt.ifPresentOrElse(elements -> {
                        WorldElementBox box = createBoxForCell(worldMap, position, cellSquareSide, elements);
                        StackPane cell = createCell(box, cellSquareSide);
                        cell.setOnMouseClicked(event -> handleCellClickAnimalSelection(cell, elements));

                        mapGrid.add(cell, finalX, finalY);
                    }, () -> {
                        WorldElementBox box = createAlphaChannelBoxForCell(cellSquareSide);
                        StackPane cell = createCell(box, cellSquareSide);
                        mapGrid.add(cell, finalX, finalY);
                    });
                }
            }

            updateStatistics();
            updateTrackedAnimalInfo();
        });
    }

    private WorldElementBox createBoxForCell(AbstractWorldMap worldMap, Vector2d position, double cellSquareSide, List<WorldElement> elements) {
        if (!elements.isEmpty()) {
            List<Animal> animalsOnCell = worldMap.getAnimals().get(position);
            if (animalsOnCell == null) {
                animalsOnCell = new ArrayList<>();
            }

            long animalCount = animalsOnCell.size();

            if (animalCount > 1) {
                return createManyAnimalsBoxForCell(animalsOnCell, cellSquareSide, elements);
            } else if (animalCount == 1 && animalsOnCell != null && !animalsOnCell.isEmpty()) {
                Animal firstAnimal = animalsOnCell.get(0);
                if (firstAnimal != null && firstAnimal.isAlive()) {
                    return createSingleAnimalBoxForCell(firstAnimal, cellSquareSide, elements);
                }
            } else {
                return createGrassBoxForCell(position, cellSquareSide);
            }
        }

        return createAlphaChannelBoxForCell(cellSquareSide);
    }


    private WorldElementBox createManyAnimalsBoxForCell(List<Animal> animalsOnCell, double cellSquareSide, List<WorldElement> elements) {
        ManyAnimals manyAnimals = new ManyAnimals(animalsOnCell.size());
        WorldElementBox box = new WorldElementBox(manyAnimals, (int) cellSquareSide, (int) cellSquareSide);
        if (trackedAnimal != null && elements.contains(trackedAnimal)) {
            unhighlightTrackedBox(box, trackedAnimal);
            box.highlightYellow();
        }
        box.updateAnimalCountBar(animalsOnCell.size());
        return box;
    }

    private WorldElementBox createSingleAnimalBoxForCell(Animal animal, double cellSquareSide, List<WorldElement> elements) {
        WorldElementBox box = new WorldElementBox(animal, (int) cellSquareSide, (int) cellSquareSide);
        if (trackedAnimal != null && trackedAnimal.equals(animal)) {
            box.highlightYellow();
        }
        box.updateHealthBar(animal);
        return box;
    }

    private WorldElementBox createGrassBoxForCell(Vector2d position, double cellSquareSide) {
        return new WorldElementBox(new Grass(position), (int) cellSquareSide, (int) cellSquareSide);
    }

    private WorldElementBox createAlphaChannelBoxForCell(double cellSquareSide) {
        return new WorldElementBox(new AlphaChannelElement(), (int) cellSquareSide, (int) cellSquareSide);
    }


    private StackPane createCell(WorldElementBox box, double cellSquareSide) {
        StackPane cell = new StackPane();
        cell.setPrefSize(cellSquareSide, cellSquareSide);
        cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Zapewniamy, że pudełko zajmie całą dostępną przestrzeń
        VBox.setVgrow(box, Priority.ALWAYS);
        HBox.setHgrow(box, Priority.ALWAYS);
        box.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cell.getChildren().add(box);
        return cell;
    }

    private void handleCellClickAnimalSelection(StackPane cell, List<WorldElement> elements) {
        // Pobieramy najlepsze zwierzę do śledzenia w tej komórce
        Optional<Animal> selectedAnimalOpt = elements.stream()
                .filter(e -> e instanceof Animal)
                .map(e -> (Animal) e)
                .max(Comparator.comparingInt(Animal::getEnergy)
                        .thenComparingInt(Animal::getBirthDate)
                        .thenComparingInt(Animal::getChildrenCount));

        if (selectedAnimalOpt.isPresent()) {
            Animal selectedAnimal = selectedAnimalOpt.get();

            // Znajdujemy WorldElementBox w tej komórce
            if (!cell.getChildren().isEmpty() && cell.getChildren().get(0) instanceof WorldElementBox box) {
                unhighlightTrackedBox(box, selectedAnimal);
            }
        }
    }
    private void unhighlightTrackedBox(WorldElementBox newBox, Animal newAnimal) {
        // Jeśli mamy poprzednio śledzony box, resetujemy jego podświetlenie
        unhighlightTrackedBox();

        // Podświetlamy nowy box
        newBox.highlightYellow();
        trackedBox = newBox;  // Aktualizujemy śledzone pole
        trackAnimal(newAnimal);  // Aktualizujemy śledzone zwierzę
    }




    private double calculateCellSize() {
        double width = gridContainer.getWidth();
        double height = gridContainer.getHeight();

        double cellSize = Math.min(width / config.getMapWidth(), height / config.getMapHeight());

        return cellSize;
    }


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


    @Override
    public void mapChanged(AbstractWorldMap worldMap) {
        if (!isPaused) {
            drawMap();
        }
    }

    @FXML
    void pauseSimulation() {
        isPaused = true;
        simulation.pause();
        updateHighlightButtonsState(); // Aktualizuje przyciski
    }

    @FXML
    void resumeSimulation() {
        isPaused = false;
        simulation.resume();
        removeHighlighting();
        updateHighlightButtonsState(); // Aktualizuje przyciski
        drawMap();
    }

    @FXML
    void closeWindow() {
        simulation.stop();
        ((Stage) mapGrid.getScene().getWindow()).close();
    }

    @FXML
    void highlightDominantGenotype() {
        if (!isPaused) return;
        isHighlightingDominantGenotypes = true;
        highlightElements(this::isAnimalWithDominantGenotype, WorldElementBox::highlightDominantGenotype);
    }

    @FXML
    void highlightPreferredFields() {
        if (!isPaused) return;
        isHighlightingPreferredFields = true;
        highlightElements(this::isPreferredField, WorldElementBox::highlightPreferredField);
    }

    private void highlightElements(Predicate<Vector2d> filterCondition, Consumer<WorldElementBox> highlightAction) {
        Platform.runLater(() -> {
            for (javafx.scene.Node node : mapGrid.getChildren()) {
                if (node instanceof StackPane cell) {
                    Vector2d pos = getGridPosition(cell);
                    if (pos == null || !filterCondition.test(pos)) continue;

                    for (javafx.scene.Node child : cell.getChildren()) {
                        if (child instanceof WorldElementBox box) {
                            highlightAction.accept(box);
                        }
                    }
                }
            }
        });
    }

    private Vector2d getGridPosition(StackPane cell) {
        Integer columnIndex = GridPane.getColumnIndex(cell);
        Integer rowIndex = GridPane.getRowIndex(cell);
        return (columnIndex != null && rowIndex != null) ? new Vector2d(columnIndex, rowIndex) : null;
    }

    private boolean isAnimalWithDominantGenotype(Vector2d pos) {
        AbstractWorldMap worldMap = simulation.getMap();
        if (dominantGenotypeAnimals.isEmpty()) {
            dominantGenotypeAnimals.addAll(getAnimalsWithMostPopularGenes());
        }

        List<WorldElement> elementsAtPos = worldMap.getWorldElements().get(pos);
        return elementsAtPos != null && elementsAtPos.stream()
                .anyMatch(element -> element instanceof Animal animal && dominantGenotypeAnimals.contains(animal));
    }

    private boolean isPreferredField(Vector2d pos) {
        AbstractWorldMap worldMap = simulation.getMap();

        if (preferredFields.isEmpty()) {
            Map<Vector2d, Integer> preferredGrassMap = worldMap.getPreferredGrassFields();
            preferredFields.addAll(preferredGrassMap.keySet()); // Pobiera same klucze (pola)
        }

        return preferredFields.contains(pos);
    }


    private void removeHighlighting() {
        Platform.runLater(() -> {
            for (javafx.scene.Node node : mapGrid.getChildren()) {
                if (node instanceof StackPane cell) {
                    for (javafx.scene.Node child : cell.getChildren()) {
                        if (child instanceof WorldElementBox box) {
                            box.setBackground(WorldElementBox.DEFAULT_BACKGROUND);
                        }
                    }
                }
            }
            isHighlightingDominantGenotypes = false;
            isHighlightingPreferredFields = false;
        });
    }

    private void updateHighlightButtonsState() {
        boolean disable = !isPaused;
        highlightPreferredFieldsButton.setDisable(disable);
        highlightDominantGenotypeButton.setDisable(disable);
    }

    public void trackAnimal(Animal animal) {
        if (isPaused) {
            trackedAnimal = animal;
            updateTrackedAnimalInfo();
        }
    }
    private void updateTrackedAnimalInfo() {
        if (trackedAnimal != null) {
            trackedGenotypeLabel.setText("Genom: " + trackedAnimal.getGenome().toString());
            trackedActiveGeneLabel.setText("Aktywny gen: "+trackedAnimal.getGenome().getActiveGene());
            trackedEnergyLabel.setText("Energia: " + trackedAnimal.getEnergy());
            trackedGrassEatenLabel.setText("Zjedzone rośliny: " + trackedAnimal.getGrassEaten());
            trackedChildrenLabel.setText("Dzieci: " + trackedAnimal.getChildrenCount());
            trackedDescendantsLabel.setText("Potomkowie: " + trackedAnimal.getDescendantsCount());
            trackedAgeLabel.setText(trackedAnimal.isAlive() ? "Wiek: " + trackedAnimal.getAge(simulation.getCurrentDay()) : "Wiek: Nie żyje");
            trackedDeathDayLabel.setText(trackedAnimal.isAlive() ? "Dzień śmierci: Żyje" : "Dzień śmierci: " + trackedAnimal.getDeathDay());
        }
    }
    @FXML
    private void stopTracking() {
        trackedAnimal = null;
        unhighlightTrackedBox();
        trackedGenotypeLabel.setText("Genom: -");
        trackedActiveGeneLabel.setText("Aktywny gen: -");
        trackedEnergyLabel.setText("Energia: -");
        trackedGrassEatenLabel.setText("Zjedzone rośliny: -");
        trackedChildrenLabel.setText("Dzieci: -");
        trackedDescendantsLabel.setText("Potomkowie: -");
        trackedAgeLabel.setText("Wiek: -");
        trackedDeathDayLabel.setText("Dzień śmierci: -");
    }

    private void unhighlightTrackedBox() {
        if (trackedBox != null) {
            trackedBox.setBackground(DEFAULT_BACKGROUND);
            trackedBox = null;
        }
    }

    public List<Animal> getAnimalsWithMostPopularGenes() {
        List<Integer> popularGenes = simulation.getStatTracker().getMostPopularGenes();
        if (popularGenes == null || popularGenes.isEmpty()) return Collections.emptyList();

        return simulation.getAliveAnimals().stream()
                .filter(animal -> animal.getGenome() != null &&
                        differsByAtMostOneGene(animal.getGenome().getGeneList(), popularGenes))
                .collect(Collectors.toList());
    }


    private boolean differsByAtMostOneGene(List<Integer> genome1, List<Integer> genome2) {

        int differences = 0;
        for (int i = 0; i < config.getGenomeLength(); i++) {
            if (!Objects.equals(genome1.get(i), genome2.get(i))) {
                differences++;
                if (differences > (config.getGenomeLength() / 3.0)) {
                    return false;
                }
            }
        }

        return true;
    }


}
