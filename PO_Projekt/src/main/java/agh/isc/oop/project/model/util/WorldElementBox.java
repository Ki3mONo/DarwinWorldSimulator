package agh.isc.oop.project.model.util;

import agh.isc.oop.project.model.elements.Animal;
import agh.isc.oop.project.model.elements.WorldElement;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Klasa reprezentująca element z mapy symulacji.
 * Do celów pomocniczych wizualizacji.
 * Rozszerza VBox.
 * Zawiera obrazek reprezentujący element, pasek energii dla zwierząt oraz,
 * w przypadku elementów ManyAnimals, dodatkowy pasek, który zawsze jest pełny i niebieski.
 */
public class WorldElementBox extends VBox {
    private final WorldElement element;
    private final int width;
    private final int height;
    private ProgressBar healthBar;
    private ProgressBar manyAnimalsBar; // Pasek dla ManyAnimals: zawsze pełny i niebieski
    private static final Map<String, Image> imageCache = new HashMap<>();

    // Tło dla elementu na mapie
    public static final Background DEFAULT_BACKGROUND = loadBackground("/world/background.png");

    // Stałe dla konfiguracji progress barów
    private static final double MIN_BAR_HEIGHT = 5;
    private static final double HEALTH_BAR_PREF_HEIGHT = 10;

    /**
     * Konstruktor klasy WorldElementBox.
     *
     * @param element element z mapy symulacji
     * @param width   szerokość elementu
     * @param height  wysokość elementu
     */
    public WorldElementBox(WorldElement element, int width, int height) {
        this.element = element;
        this.width = width;
        this.height = height;

        // Ustawienia rozmiaru
        this.setMaxWidth(Double.MAX_VALUE);
        this.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(this, Priority.ALWAYS);
        HBox.setHgrow(this, Priority.ALWAYS);

        // Inicjalizacja elementu
        initialize();
    }

    /**
     * Metoda inicjalizująca element.
     */
    private void initialize() {
        // Ustawienia VBox
        this.setSpacing(2);
        this.setAlignment(javafx.geometry.Pos.CENTER);
        this.setBackground(DEFAULT_BACKGROUND);

        // Wczytanie obrazu za pomocą metody pomocniczej
        Image image = loadImage(element.getResourceName());

        // Ustawienia obrazu
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height * 0.8);
        this.getChildren().add(imageView);

        // Konfiguracja paska energii dla obiektu Animal
        if (element instanceof Animal animal) {
            configureHealthBar(animal);
        }

        // W przypadku ManyAnimals dodajemy niebieski pasek
        if (element instanceof ManyAnimals) {
            configureManyAnimalCountBar();
        }
    }

    /**
     * Metoda pomocnicza ładująca obraz z cache.
     *
     * @param resourcePath ścieżka do zasobu
     * @return załadowany obraz
     */
    private static Image loadImage(String resourcePath) {
        return imageCache.computeIfAbsent(resourcePath, key -> {
            try {
                return new Image(Objects.requireNonNull(WorldElementBox.class.getResourceAsStream(key)));
            } catch (Exception e) {
                System.err.println("Nie udało się załadować obrazu: " + key);
                return null;
            }
        });
    }

    /**
     * Konfiguracja paska energii dla obiektu Animal.
     * @param animal obiekt Animal
     */
    private void configureHealthBar(Animal animal) {
        healthBar = new ProgressBar();
        healthBar.setMinWidth(width);
        healthBar.setMaxWidth(width);
        healthBar.setMinHeight(10);
        healthBar.setPrefHeight(12);
        updateHealthBar(animal);
        this.getChildren().add(healthBar);
    }

    /**
     * Konfiguracja niebieskiego paska dla obiektów ManyAnimals.
     * Pasek będzie zawsze pełny (progress = 1) i z niebieskim akcentem.
     */
    private void configureManyAnimalCountBar() {
        manyAnimalsBar = new ProgressBar();
        manyAnimalsBar.setMinWidth(width);
        manyAnimalsBar.setMaxWidth(width);
        manyAnimalsBar.setMinHeight(10);
        manyAnimalsBar.setPrefHeight(12);
        manyAnimalsBar.setProgress(1); // zawsze pełny
        manyAnimalsBar.setStyle("-fx-accent: blue;"); // niebieski akcent
        this.getChildren().add(manyAnimalsBar);
    }

    /**
     * Oblicza postęp paska energii na podstawie energii zwierzęcia.
     *
     * @param animal obiekt Animal
     * @return wartość postępu w zakresie od 0.0 do 1.0
     */
    private double getHealthProgress(Animal animal) {
        int energy = animal.getEnergy();
        int maxEnergy = Math.max(animal.getMaxEnergy(), 1); // maxEnergy to config.getInitialEnergy()*2 też dobrane arbitralnie
        return (double) energy / maxEnergy;
    }

    /**
     * Aktualizuje healthBar dla obiektu Animal.
     *
     * @param animal obiekt Animal
     */
    public void updateHealthBar(Animal animal) {
        if (healthBar != null) {
            double progress = getHealthProgress(animal);
            healthBar.setProgress(progress);
            healthBar.setStyle("-fx-background-color: white; -fx-accent: " +
                    getHealthBarColor(animal.getEnergy(), animal.getMaxEnergy()) + ";");
        }
    }

    /**
     * Zwraca kolor paska energii w zależności od procentowej wartości energii.
     *
     * @param energy    aktualna energia
     * @param maxEnergy maksymalna energia
     * @return kolor w formacie akceptowanym przez CSS
     */
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

    /**
     * Ustawia tło z podświetleniem.
     *
     * @param color   kolor podświetlenia
     * @param opacity przezroczystość podświetlenia
     */
    private void setHighlightBackground(Color color, double opacity) {
        BackgroundFill highlight = new BackgroundFill(
                Color.rgb((int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255), opacity),
                CornerRadii.EMPTY,
                Insets.EMPTY
        );
        this.setBackground(new Background(highlight));
    }

    /**
     * Podświetla zwierzę śledzone na mapie.
     */
    public void highlightTrackedAnimal() {
        setHighlightBackground(Color.YELLOW, 0.3);
    }

    /**
     * Podświetla dominujący genotyp.
     */
    public void highlightDominantGenotype() {
        setHighlightBackground(Color.CYAN, 0.2);
    }

    /**
     * Podświetla pola preferowane przez trawę.
     */
    public void highlightPreferredField() {
        setHighlightBackground(Color.RED, 0.1);
    }

    /**
     * Ładuje tło z podanego zasobu.
     *
     * @param path ścieżka do zasobu tła
     * @return obiekt Background
     */
    private static Background loadBackground(String path) {
        try {
            Image bgImage = new Image(Objects.requireNonNull(WorldElementBox.class.getResourceAsStream(path)));
            BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, true, false);
            BackgroundImage backgroundImage = new BackgroundImage(bgImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    bgSize);
            return new Background(backgroundImage);
        } catch (Exception e) {
            System.err.println("Nie udało się załadować tła: " + path);
            return new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY));
        }
    }
}
