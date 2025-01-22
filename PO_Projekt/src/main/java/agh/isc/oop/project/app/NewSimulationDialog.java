package agh.isc.oop.project.app;

import agh.isc.oop.project.simulation.SimulationConfig;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Klasa reprezentująca okno dialogowe do ustawiania parametrów nowej symulacji.
 * <p>
 * Okno dialogowe pozwala na ustawienie parametrów nowej symulacji.
 * Po wyświetleniu okna użytkownik może ustawić parametry, a następnie rozpocząć symulację.
 * </p>
 */
public class NewSimulationDialog {
    /**
     * Obiekt klasy Stage reprezentujący okno dialogowe.
     */
    private final Stage stage;

    /**
     * Obiekt klasy NewSimulationDialogController reprezentujący kontroler okna dialogowego.
     */
    private final NewSimulationDialogController controller;

    /**
     * Konstruktor klasy NewSimulationDialog.
     * <p>
     * Tworzy nowe okno dialogowe do ustawiania parametrów nowej symulacji.
     * </p>
     *
     * @param aClass Klasa, w której znajduje się plik FXML okna dialogowego
     * @throws IOException Wyjątek rzucany w przypadku błędu podczas ładowania pliku FXML
     */
    public NewSimulationDialog(Class<?> aClass) throws IOException {
        // Ładowanie pliku FXML okna dialogowego
        FXMLLoader loader = new FXMLLoader(aClass.getResource("/fxml/NewSimulationDialog.fxml"));
        Parent root = loader.load();

        // Pobranie kontrolera okna dialogowego
        controller = loader.getController();

        // Ustawienie tytułu okna dialogowego
        stage = new Stage();
        stage.setTitle("DarwinWorld – Ustaw Parametry Nowej Symulacji");

        // Ustawienie ikony aplikacji
        Image appIcon = new Image(Objects.requireNonNull(aClass.getResourceAsStream("/icons/icon.png")));
        stage.getIcons().add(appIcon);

        // Ustawienie sceny okna dialogowego
        stage.setScene(new Scene(root));

        // Ustawienie okna dialogowego jako modalnego (blokującego)
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    /**
     * Metoda wyświetlająca okno dialogowe.
     */
    public void showAndWait() {
        stage.showAndWait();
    }

    /**
     * Metoda zwracająca informację o tym, czy użytkownik kliknął przycisk "Start".
     *
     * @return Wartość logiczna informująca o tym, czy użytkownik kliknął przycisk "Start"
     */
    public boolean isStartClicked() {
        return controller.isStartClicked();
    }

    /**
     * Metoda zwracająca obiekt klasy SimulationConfig z ustawionymi parametrami symulacji.
     *
     * @return Obiekt klasy SimulationConfig z ustawionymi parametrami symulacji
     */
    public SimulationConfig getSimulationConfig() {
        return controller.getSimulationConfig();
    }
}