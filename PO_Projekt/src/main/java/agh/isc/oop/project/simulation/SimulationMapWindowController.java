package agh.isc.oop.project.simulation;

import agh.isc.oop.project.model.*;
import agh.isc.oop.project.model.util.AlphaChannelElement;
import agh.isc.oop.project.model.util.ManyAnimals;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SimulationMapWindowController implements MapChangeListener {

    @FXML private Label mostPopularGenotypeLabel;
    @FXML private Label avgChildrenLabel;

    @FXML private StackPane gridContainer;
    @FXML private GridPane mapGrid;
    @FXML private Label animalCountLabel;
    @FXML private Label grassCountLabel;
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
    @FXML private Button stopTrackingButton;

    private Animal trackedAnimal;


    private Simulation simulation;
    private SimulationConfig config;
    private boolean isPaused = false;
    private int dayCounter = 0;
    private static final Map<String, Background> backgroundCache = new ConcurrentHashMap<>();

    private XYChart.Series<Number, Number> animalSeries;
    private XYChart.Series<Number, Number> grassSeries;

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

        // Dodanie listenera do zmiany rozmiaru okna
        gridContainer.widthProperty().addListener((obs, oldVal, newVal) -> resizeGrid());
        gridContainer.heightProperty().addListener((obs, oldVal, newVal) -> resizeGrid());

        createGrid();
    }

    private void createGrid() {
        mapGrid.getChildren().clear();
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();

        double cellSize = calculateCellSize(); // Nowy rozmiar dla równej szerokości i wysokości

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
        double cellSize = calculateCellSize(); // Obliczamy nowe rozmiary komórek

        for (ColumnConstraints column : mapGrid.getColumnConstraints()) {
            column.setPrefWidth(cellSize);
        }

        for (RowConstraints row : mapGrid.getRowConstraints()) {
            row.setPrefHeight(cellSize);
        }

        // 🔹 Przeskalowanie wszystkich elementów w komórkach
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

                    // Zamiast isPresent i orElse, używamy ifPresentOrElse dla bezpieczniejszego przetwarzania
                    int finalX = x;
                    int finalY = y;
                    elementsOpt.ifPresentOrElse(elements -> {
                        WorldElementBox box;
                        StackPane cell = new StackPane();
                        cell.setPrefSize(cellSquareSide, cellSquareSide);
                        cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                        if (!elements.isEmpty()) {
                            List<Animal> animalsOnCell = worldMap.getAnimals().get(position);
                            if (animalsOnCell == null) {
                                animalsOnCell = new ArrayList<>();
                            }
                            long animalCount = Math.max(0, animalsOnCell.size());

                            if (animalCount > 1) {
                                ManyAnimals manyAnimals = new ManyAnimals(animalCount);
                                box = new WorldElementBox(manyAnimals, (int) cellSquareSide, (int) cellSquareSide);
                                if (trackedAnimal != null && elements.contains(trackedAnimal)) {
                                    box.highlightYellow();
                                }
                                box.updateAnimalCountBar(animalCount);
                            } else if (animalCount == 1) {
                                Animal animal = animalsOnCell.get(0);
                                box = new WorldElementBox(animal, (int) cellSquareSide, (int) cellSquareSide);
                                if (trackedAnimal != null && trackedAnimal.equals(animal)) {
                                    box.highlightYellow();
                                }
                                box.updateHealthBar(animal);
                            } else {
                                box = new WorldElementBox(new Grass(position), (int) cellSquareSide, (int) cellSquareSide);
                            }
                        } else {
                            box = new WorldElementBox(new AlphaChannelElement(), (int) cellSquareSide, (int) cellSquareSide);
                        }
                        VBox.setVgrow(box, Priority.ALWAYS);
                        HBox.setHgrow(box, Priority.ALWAYS);
                        box.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                        cell.getChildren().add(box);

                        cell.setOnMouseClicked(event -> {
                            if (isPaused) {
                                elements.stream()
                                        .filter(e -> e instanceof Animal)
                                        .map(e -> (Animal) e)
                                        .max(Comparator.comparingInt(Animal::getEnergy)
                                                .thenComparingInt(Animal::getBirthDate)
                                                .thenComparingInt(Animal::getChildrenCount))
                                        .ifPresent(this::trackAnimal);
                            }
                        });

                        mapGrid.add(cell, finalX, finalY);
                    }, () -> {
                        WorldElementBox box;
                        StackPane cell = new StackPane();
                        cell.setPrefSize(cellSquareSide, cellSquareSide);
                        cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                        box = new WorldElementBox(new AlphaChannelElement(), (int) cellSquareSide, (int) cellSquareSide);
                        VBox.setVgrow(box, Priority.ALWAYS);
                        HBox.setHgrow(box, Priority.ALWAYS);
                        box.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                        cell.getChildren().add(box);

                        mapGrid.add(cell, finalX, finalY);
                    });
                }
            }
            updateStatistics();
            updateTrackedAnimalInfo();
        });
    }




    private Background getBackground(String resourcePath) {
        return backgroundCache.computeIfAbsent(resourcePath, path -> {
            try {
                Image bgImage = new Image(getClass().getResourceAsStream(path));
                BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, true, false);
                BackgroundImage backgroundImage = new BackgroundImage(bgImage,
                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER, bgSize);
                return new Background(backgroundImage);
            } catch (Exception e) {
                System.err.println("Nie udało się załadować tła: " + resourcePath);
                return new Background(new BackgroundFill(javafx.scene.paint.Color.LIGHTGREEN, CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY));
            }
        });
    }

    private double calculateCellSize() {
        double width = gridContainer.getWidth();
        double height = gridContainer.getHeight();

        double cellSize = Math.min(width / config.getMapWidth(), height / config.getMapHeight());

        return cellSize;
    }


    private void updateStatistics() {
        Platform.runLater(() -> {
            AbstractWorldMap worldMap = simulation.getMap();
            int animalCount = worldMap.getAnimalCount();
            long grassCount = worldMap.getGrassCount();
            List<Integer> mostPopularGenotype = worldMap.getMostPopularGenotype();
            double avgChildren = worldMap.getAverageChildrenCount();

            animalCountLabel.setText("Zwierzęta: " + animalCount);
            grassCountLabel.setText("Rośliny: " + grassCount);
            mostPopularGenotypeLabel.setText("Genotyp: " + mostPopularGenotype.toString());
            avgEnergyLabel.setText(String.format("Średnia energia: %.4f", worldMap.getAverageEnergy()));
            avgLifespanLabel.setText(String.format("Średnia długość życia: %.4f", simulation.getAverageLifespan()));
            avgChildrenLabel.setText(String.format("Średnia liczba dzieci: %.2f", avgChildren));

            animalSeries.getData().add(new XYChart.Data<>(dayCounter, animalCount));
            grassSeries.getData().add(new XYChart.Data<>(dayCounter, grassCount));

            if (!isPaused) {
                dayCounter++;
            }
        });
    }


    @Override
    public void mapChanged(AbstractWorldMap worldMap, String message) {
        if (!isPaused) {
            drawMap();
        }
    }

    @FXML
    void pauseSimulation() {
        isPaused = true;
        simulation.pause();
    }

    @FXML
    void resumeSimulation() {
        isPaused = false;
        simulation.resume();
        drawMap();
    }

    @FXML
    void closeWindow() {
        simulation.stop();
        ((Stage) mapGrid.getScene().getWindow()).close();
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
            trackedDescendantsLabel.setText("Potomkowie: " + trackedAnimal.getChildrenCount());
            trackedAgeLabel.setText(trackedAnimal.isAlive() ? "Wiek: " + trackedAnimal.getAge(simulation.getCurrentDay()) : "Wiek: Nie żyje");
            trackedDeathDayLabel.setText(trackedAnimal.isAlive() ? "Dzień śmierci: Żyje" : "Dzień śmierci: " + trackedAnimal.getDeathDay());
        }
    }
    @FXML
    private void stopTracking() {
        trackedAnimal = null;
        trackedGenotypeLabel.setText("Genom: -");
        trackedActiveGeneLabel.setText("Aktywny gen: -");
        trackedEnergyLabel.setText("Energia: -");
        trackedGrassEatenLabel.setText("Zjedzone rośliny: -");
        trackedChildrenLabel.setText("Dzieci: -");
        trackedDescendantsLabel.setText("Potomkowie: -");
        trackedAgeLabel.setText("Wiek: -");
        trackedDeathDayLabel.setText("Dzień śmierci: -");
    }



}
