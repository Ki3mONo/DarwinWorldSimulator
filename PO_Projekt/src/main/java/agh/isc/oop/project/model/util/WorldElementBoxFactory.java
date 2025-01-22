package agh.isc.oop.project.model.util;

import agh.isc.oop.project.model.elements.Animal;
import agh.isc.oop.project.model.elements.Grass;
import agh.isc.oop.project.model.elements.WorldElement;
import agh.isc.oop.project.model.map.AbstractWorldMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Fabryka obiektów WorldElementBox.
 * Tworzy obiekty WorldElementBox w zależności od zawartości komórki.
 */
public class WorldElementBoxFactory {

    /**
     * Tworzy obiekt WorldElementBox w zależności od zawartości komórki.
     *
     * @param worldMap      mapa, z której pobieramy dodatkowe informacje
     * @param position      pozycja komórki
     * @param cellSize      rozmiar komórki
     * @param elements      lista obiektów znajdujących się w komórce
     * @param trackedAnimal aktualnie śledzone zwierzę (może być null)
     * @return odpowiednio skonstruowany WorldElementBox
     */
    public static WorldElementBox createBox(AbstractWorldMap worldMap,
                                            Vector2d position,
                                            int cellSize,
                                            List<WorldElement> elements,
                                            Animal trackedAnimal) {
        if (!elements.isEmpty()) {
            // Pobieramy zwierzęta znajdujące się na danej pozycji – jeśli lista jest null,
            // tworzymy pustą listę.
            List<Animal> animalsOnCell = Optional.ofNullable(worldMap.getAnimals().get(position))
                    .orElse(new ArrayList<>());

            long animalCount = animalsOnCell.size();

            if (animalCount > 1) {
                return createManyAnimalsBox(animalsOnCell, cellSize, elements, trackedAnimal); // Wiele zwierząt
            } else if (animalCount == 1 && !animalsOnCell.isEmpty()) {
                Animal firstAnimal = animalsOnCell.get(0);
                if (firstAnimal != null && firstAnimal.isAlive()) {
                    return createSingleAnimalBox(firstAnimal, cellSize, elements, trackedAnimal); // Zwierzę
                }
            } else {
                return createGrassBox(position, cellSize); // Trawa
            }
        }
        return createBox(cellSize); // Puste pole
    }

    /**
     * Tworzy obiekt WorldElementBox w zależności od zawartości komórki.
     *
     * @param cellSize      rozmiar komórki
     * @return odpowiednio skonstruowany WorldElementBox
     */
    public static WorldElementBox createBox(int cellSize){
        return createAlphaChannelBox(cellSize);
    }

    /**
     * Tworzy obiekt WorldElementBox reprezentujący wiele zwierząt.
     * @param animals       lista zwierząt
     *                      (zakładamy, że lista nie jest pusta)
     * @param cellSize      rozmiar komórki
     * @param elements      lista obiektów znajdujących się w komórce
     *                      (zakładamy, że lista nie jest pusta)
     * @param trackedAnimal aktualnie śledzone zwierzę, o ile istnieje
     * */
    private static WorldElementBox createManyAnimalsBox(List<Animal> animals,
                                                        int cellSize,
                                                        List<WorldElement> elements,
                                                        Animal trackedAnimal) {
        // Tworzymy obiekt WorldElementBox reprezentujący wiele zwierząt.
        ManyAnimals manyAnimals = new ManyAnimals();

        // Tworzymy obiekt WorldElementBox reprezentujący wiele zwierząt.
        WorldElementBox box = new WorldElementBox(manyAnimals, cellSize, cellSize);

        // Jeśli któreś z zwierząt jest aktualnie śledzone, podświetlamy je na żółto.
        if (trackedAnimal != null && elements.contains(trackedAnimal)) {
            // Możesz dodatkowo obsłużyć odznaczenie poprzedniego boxu,
            // jeśli zajdzie taka potrzeba w kontrolerze.
            box.highlightTrackedAnimal();
        }
        // Zwracamy gotowy obiekt.
        return box;
    }

    /**
     * Tworzy obiekt WorldElementBox reprezentujący pojedyncze zwierzę.
     *
     * @param animal        zwierzę
     * @param cellSize      rozmiar komórki
     * @param elements      lista obiektów znajdujących się w komórce
     * @param trackedAnimal aktualnie śledzone zwierzę, o ile istnieje
     * @return obiekt WorldElementBox
     */
    private static WorldElementBox createSingleAnimalBox(Animal animal,
                                                         int cellSize,
                                                         List<WorldElement> elements,
                                                         Animal trackedAnimal) {
        // Tworzymy obiekt WorldElementBox reprezentujący pojedyncze zwierzę.
        WorldElementBox box = new WorldElementBox(animal, cellSize, cellSize);
        // Jeśli zwierzę jest aktualnie śledzone, podświetlamy je na żółto.
        if (trackedAnimal != null && trackedAnimal.equals(animal)) {
            box.highlightTrackedAnimal();
        }
        // Aktualizujemy pasek życia zwierzęcia.
        box.updateHealthBar(animal);
        // Zwracamy gotowy obiekt.
        return box;
    }

    /**
     * Tworzy obiekt WorldElementBox reprezentujący trawę.
     *
     * @param position pozycja trawy
     * @param cellSize rozmiar komórki
     * @return obiekt WorldElementBox
     */
    private static WorldElementBox createGrassBox(Vector2d position, int cellSize) {
        return new WorldElementBox(new Grass(position), cellSize, cellSize);
    }

    /**
     * Tworzy obiekt WorldElementBox reprezentujący "puste" pole.
     *
     * @param cellSize rozmiar komórki
     * @return obiekt WorldElementBox
     */
    private static WorldElementBox createAlphaChannelBox(int cellSize) {
        return new WorldElementBox(new AlphaChannelElement(), cellSize, cellSize);
    }
}
