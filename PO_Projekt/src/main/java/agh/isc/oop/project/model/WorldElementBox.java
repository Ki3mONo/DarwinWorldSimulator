package agh.isc.oop.project.model;

import agh.isc.oop.project.model.util.ManyAnimals;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WorldElementBox extends VBox {
    private final WorldElement element;
    private final int width;
    private final int height;
    private ProgressBar healthBar;
    private ProgressBar animalCountBar;

    private static final Map<String, Image> imageCache = new HashMap<>();
    private static final Background DEFAULT_BACKGROUND = loadBackground("/world/background.png");

    public WorldElementBox(WorldElement element, int width, int height) {
        this.element = element;
        this.width = width;
        this.height = height;

        this.setMaxWidth(Double.MAX_VALUE);
        this.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(this, Priority.ALWAYS);
        HBox.setHgrow(this, Priority.ALWAYS);

        initialize();
    }

    private void initialize() {
        this.setSpacing(2);
        this.setAlignment(javafx.geometry.Pos.CENTER);
        this.setBackground(DEFAULT_BACKGROUND);

        String resourcePath = element.getResourceName();
        Image image = imageCache.computeIfAbsent(resourcePath, key -> {
            try {
                return new Image(Objects.requireNonNull(getClass().getResourceAsStream(key)));
            } catch (Exception e) {
                System.err.println("Nie udało się załadować obrazu: " + key);
                return null;
            }
        });

        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height * 0.8);

        this.getChildren().add(imageView);

        if (element instanceof Animal animal) {
            healthBar = new ProgressBar();
            healthBar.setMinWidth(width);
            healthBar.setMaxWidth(width);
            healthBar.setMinHeight(5);
            healthBar.setPrefHeight(10);
            updateHealthBar(animal);
            this.getChildren().add(healthBar);
        }

        if (element instanceof ManyAnimals manyAnimals) {
            long animalCount = manyAnimals.getSize();

            animalCountBar = new ProgressBar();
            animalCountBar.setMinWidth(width);
            animalCountBar.setMaxWidth(width);
            animalCountBar.setPrefHeight(Math.max(height * 0.1, 5)); // 🔹 Minimalna wysokość 5 px
            animalCountBar.setMaxHeight(Double.MAX_VALUE); // 🔹 Maksymalna wysokość, jeśli dostępna
            animalCountBar.setProgress(1);
            animalCountBar.setStyle("-fx-accent: blue;");

            VBox.setVgrow(animalCountBar, Priority.ALWAYS); // 🔹 Pozwala rosnąć w pionie

            this.getChildren().add(animalCountBar);
            updateAnimalCountBar(animalCount);
        }
    }

    private double getHealthProgress(Animal animal) {
        int energy = animal.getEnergy();
        int maxEnergy = Math.max(animal.getMaxEnergy(), 1); // Zapobieganie dzieleniu przez 0
        return (double) energy / maxEnergy;
    }

    public void updateHealthBar(Animal animal) {
        if (healthBar != null) {
            double progress = getHealthProgress(animal);
            healthBar.setProgress(progress);
            healthBar.setStyle("-fx-background-color: white; -fx-accent: " + getHealthBarColor(animal.getEnergy(), animal.getMaxEnergy()) + ";");
        }
    }

    public void updateAnimalCountBar(long animalCount) {
        if (animalCountBar != null) {
            animalCountBar.setProgress(1);
            animalCountBar.setStyle("-fx-accent: blue;");
        } else {
            System.out.println("Błąd: animalCountBar jest null!");
        }
    }

    private String getHealthBarColor(int energy, int maxEnergy) {
        double healthPercent = (double) energy / Math.max(maxEnergy, 1);

        if (healthPercent > 0.7) {
            return "green";
        } else if (healthPercent > 0.3) {
            return "#ffea00";
        } else {
            return "red";
        }
    }

    public void highlightYellow() {
        BackgroundFill highlight = new BackgroundFill(Color.rgb(255, 255, 0, 0.3), CornerRadii.EMPTY, Insets.EMPTY);
        this.setBackground(new Background(highlight));
    }

    private static Background loadBackground(String path) {
        try {
            Image bgImage = new Image(Objects.requireNonNull(WorldElementBox.class.getResourceAsStream(path)));
            BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, true, false);
            BackgroundImage backgroundImage = new BackgroundImage(bgImage,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER, bgSize);
            return new Background(backgroundImage);
        } catch (Exception e) {
            System.err.println("Nie udało się załadować tła: " + path);
            return new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY));
        }
    }
}
