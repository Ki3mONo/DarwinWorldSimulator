package agh.isc.oop.project.model.util;

/**
 * Interfejs dla obserwatorów statystyk,
 * realizacja wzorca obserwator.
 */
public interface StatsChangeListener {
    /**
     * Metoda, która jest reakcją na powiadomienie o zmianie statystyk
     * @param stats obserwowane statystyki
     * @param currentDay obecny dzień
     */
    void statsChanged(SimulationStatTracker stats, int currentDay);
}
