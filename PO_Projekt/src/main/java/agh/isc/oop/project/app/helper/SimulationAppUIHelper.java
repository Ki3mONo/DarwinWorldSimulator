package agh.isc.oop.project.app.helper;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

/**
 * Klasa pomocnicza do tworzenia elementów interfejsu użytkownika.
 * Zawiera metody pomocnicze do tworzenia przycisków i obrazów.
 */
public class SimulationAppUIHelper {
    /**
     * Metoda tworząca transparentny przycisk z obrazem.
     * @param aClass Klasa, w której znajduje się obraz.
     * @param imagePath Ścieżka do obrazu.
     * @return Przycisk z obrazem.
     */
    public static Button createTransparentImageButton(Class<?> aClass, String imagePath) {
        // Wczytanie obrazu
        Image image = new Image(Objects.requireNonNull(aClass.getResourceAsStream(imagePath)));
        ImageView imageView = new ImageView(image);
        // Ustawienie proporcji obrazu
        imageView.setPreserveRatio(true);

        // Utworzenie przycisku z obrazem
        Button button = new Button();
        // Ustawienie obrazu jako grafiki przycisku
        button.setGraphic(imageView);
        // Ustawienie przezroczystego tła przycisku
        button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        // Ustawienie minimalnych rozmiarów przycisku
        button.setMinSize(imageView.getFitWidth(), imageView.getFitHeight());

        // Zwrócenie przycisku
        return button;
    }

    /**
     * Metoda tworząca obraz tła.
     * @param aClass Klasa, w której znajduje się obraz.
     * @param imagePath Ścieżka do obrazu.
     * @return Obraz tła.
     */
    public static ImageView createBackgroundImageView(Class<?> aClass, String imagePath) {
        // Wczytanie obrazu
        Image backgroundImage = new Image(Objects.requireNonNull(aClass.getResourceAsStream(imagePath)));
        ImageView backgroundView = new ImageView(backgroundImage);

        // Ustawienie proporcji obrazu
        backgroundView.setPreserveRatio(true);
        backgroundView.setSmooth(true);

        // Zwrócenie obrazu tła
        return backgroundView;
    }
}
