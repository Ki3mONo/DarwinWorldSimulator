package agh.isc.oop.project.simulation;

import agh.isc.oop.project.model.*;
import agh.isc.oop.project.model.elements.AgingAnimalFactory;
import agh.isc.oop.project.model.elements.Animal;
import agh.isc.oop.project.model.elements.AnimalFactory;
import agh.isc.oop.project.model.elements.Genome;
import agh.isc.oop.project.model.map.AbstractWorldMap;
import agh.isc.oop.project.model.util.GenomeGenerator;
import agh.isc.oop.project.model.util.SimulationCSVSaver;
import agh.isc.oop.project.model.util.SimulationStatTracker;
import agh.isc.oop.project.model.util.Vector2d;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa symulacji, która zarządza przebiegiem symulacji.
 * Zawiera w sobie mapę, konfigurację symulacji, listę żywych zwierząt, listę martwych zwierząt,
 * generator genomów, fabrykę zwierząt, statystyki symulacji oraz aktualny dzień symulacji.
 * Implementuje interfejs Runnable, aby móc uruchomić symulację w osobnym wątku.
 */
public class Simulation implements Runnable {

    private final AbstractWorldMap map;
    private final SimulationConfig config;
    private final List<Animal> aliveAnimals = new ArrayList<>();
    private final List<Animal> deadAnimals = new ArrayList<>();
    private final GenomeGenerator genomeGenerator;
    private boolean isRunning = true;
    private boolean isPaused = false;
    private final AnimalFactory animalFactory;
    private int currentDay = 0;

    private final SimulationStatTracker statTracker;

    /**
     * Konstruktor symulacji, który tworzy nową symulację na podstawie konfiguracji, mapy oraz ścieżki do pliku CSV.
     * @param config konfiguracja symulacji
     * @param map mapa świata
     * @param csvFilePath ścieżka do pliku CSV
     */
    public Simulation(SimulationConfig config, AbstractWorldMap map, String csvFilePath) {
        this.config = config;
        this.map = map;
        this.currentDay = 0;
        Genome.setConfig(config);
        Animal.setConfig(config);
        this.genomeGenerator = new GenomeGenerator(config.getGenomeLength());
        this.animalFactory = config.isAgingAnimalVariant() ? new AgingAnimalFactory() : new AnimalFactory();

        //Statystyki obserwują zmiany mapy
        this.statTracker = new SimulationStatTracker(this);
        this.map.addObserver(statTracker);

        if(csvFilePath != null){
            //CSV saver obserwuje zmiany statystyk
            statTracker.addObserver(new SimulationCSVSaver(csvFilePath));
        }
        //Generowanie zwierząt na mapie
        generateAnimalsOnMap(config, map);
    }

    /**
     * Metoda generująca zwierzęta na mapie na podstawie konfiguracji.
     * @param config konfiguracja symulacji
     * @param map mapa świata
     */
    private void generateAnimalsOnMap(SimulationConfig config, AbstractWorldMap map) {
        for (int i = 0; i < config.getStartAnimalCount(); i++) {
            Vector2d position = new Vector2d(
                    (int) (Math.random() * config.getMapWidth()),
                    (int) (Math.random() * config.getMapHeight())
            );
            //Tworzenie zwierzęcia z losowym genomem z użyciem fabryki i generatorem genomów
            Animal animal = animalFactory.createAnimal(position, genomeGenerator.generateGenome());
            try {
                map.place(animal);
                aliveAnimals.add(animal);
            } catch (IncorrectPositionException e) {
                System.err.println("Failed to place animal: " + e.getMessage());
            }
        }
    }

    /**
     * Metoda uruchamiająca symulację.
     * W pętli wykonywane są cykle symulacji, w których zwierzęta poruszają się, jedzą, rozmnażają się, a także umierają.
     * Cykl trwa tyle, ile wynosi długość dnia w konfiguracji symulacji.
     * Po zakończeniu dnia, zwiększany jest licznik dni symulacji.
     * W przypadku przerwania symulacji, wypisywany jest komunikat o przerwaniu.
     */
    @Override
    public void run() {
        while (isRunning) {
            try {
                synchronized (this) {
                    while (isPaused) {
                        wait(1000);
                    }
                }
                performDayCycle();
                currentDay++;
                Thread.sleep(config.getDayDurationMs());
            } catch (InterruptedException e) {
                System.err.println("Simulation interrupted.");
                isRunning = false;
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Metoda wykonująca cykl symulacji.
     * W cyklu symulacji zwierzęta poruszają się, jedzą, rozmnażają się, a także umierają.
     * Zwierzęta, które zginęły w trakcie cyklu, są usuwane z mapy oraz z listy żywych zwierząt.
     * Zwierzęta, które przetrwały, są przesuwane na mapie.
     * Następnie zwierzęta jedzą rośliny, a także rozmnażają się.
     * Na koniec dnia, rośliny rosną na mapie.
     */
    private void performDayCycle() {
        //Martwe zwierzęta
        List<Animal> diedThisCycle = new ArrayList<>();
        for (Animal animal : aliveAnimals) {
            if (animal.getEnergy() <= 0) {
                animal.die(currentDay);
                diedThisCycle.add(animal);
            }
        }
        map.removeAnimals(diedThisCycle);
        aliveAnimals.removeAll(diedThisCycle);
        deadAnimals.addAll(diedThisCycle);

        //Przesuwanie zwierząt
        for (Animal animal : aliveAnimals) {
            map.move(animal);
        }

        //Jedzenie
        map.handleEating();

        //Rozmnażanie
        List<Animal> bornAnimals = map.handleReproduction(currentDay);
        aliveAnimals.addAll(bornAnimals);

        //Rosnące trawy
        map.grassGrow(config.getDailyGrassGrowth());

        //Obserwatorzy zmiany mapy
        map.mapChanged();
    }
    /**
     * Metoda zatrzymująca symulację.
     */
    public void stop() {
        isRunning = false;
        resume();
    }
    /**
     * Metoda pauzująca symulację.
     */
    public synchronized void pause() {
        isPaused = true;
    }

    /**
     * Metoda wznawiająca symulację.
     */
    public synchronized void resume() {
        isPaused = false;
        notifyAll();
    }

    /**
     * Metoda zwracająca listę żywych zwierząt.
     * @return lista żywych zwierząt
     */
    public List<Animal> getAliveAnimals() {
        return aliveAnimals;
    }

    /**
     * Metoda zwracająca listę martwych zwierząt.
     * @return lista martwych zwierząt
     */
    public List<Animal> getDeadAnimals() {
        return deadAnimals;
    }

    /**
     * Metoda zwracająca mapę świata.
     * @return mapa świata
     */
    public AbstractWorldMap getMap() {
        return map;
    }

    /**
     * Metoda zwracająca aktualny dzień symulacji.
     * @return aktualny dzień symulacji
     */
    public int getCurrentDay() {
        return currentDay;
    }

    /**
     * Metoda zwracająca całą konfigurację symulacji.
     * @return konfiguracja symulacji
     */
    public SimulationConfig getConfig() {
        return config;
    }

    /**
     * Metoda zwracająca SimulationStatTracker symulacji.
     * @return SimulationStatTracker symulacji
     */
    public SimulationStatTracker getStatTracker() {
        return statTracker;
    }
}