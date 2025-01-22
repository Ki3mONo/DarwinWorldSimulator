package agh.isc.oop.project.app.helper;

import agh.isc.oop.project.model.util.WorldElementBox;
import agh.isc.oop.project.model.util.Vector2d;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Klasa pomocnicza do obsługi podświetlania elementów na mapie symulacji.
 * Zawiera metody pomocnicze do podświetlania elementów na mapie symulacji.
 */
public class SimulationMapWindowHighlightHelper {
    /**
     * Metoda podświetlająca elementy na mapie symulacji.
     * @param grid Siatka mapy.
     * @param filterCondition Warunek filtrujący elementy do podświetlenia.
     * @param highlightAction Akcja podświetlająca element.
     */
    public static void highlightElements(GridPane grid, Predicate<Vector2d> filterCondition,
                                         Consumer<WorldElementBox> highlightAction) {
        for (javafx.scene.Node node : grid.getChildren()) {
            if (node instanceof StackPane cell) {
                Vector2d pos = getGridPosition(cell);
                if (pos != null && filterCondition.test(pos)) {
                    for (javafx.scene.Node child : cell.getChildren()) {
                        if (child instanceof WorldElementBox box) {
                            highlightAction.accept(box);
                        }
                    }
                }
            }
        }
    }

    /** Metoda zwracająca pozycje komórki siatki.
     * @param cell Komórka siatki.
     * @return Pozycja komórki siatki.
     */
    private static Vector2d getGridPosition(StackPane cell) {
        Integer col = javafx.scene.layout.GridPane.getColumnIndex(cell);
        Integer row = javafx.scene.layout.GridPane.getRowIndex(cell);
        return (col != null && row != null) ? new Vector2d(col, row) : null;
    }
}
