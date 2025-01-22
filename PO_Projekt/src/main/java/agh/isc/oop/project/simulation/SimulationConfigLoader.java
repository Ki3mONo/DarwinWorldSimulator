package agh.isc.oop.project.simulation;

import agh.isc.oop.project.model.map.MapType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * Klasa ładowania konfiguracji symulacji z pliku JSON.
 */
public class SimulationConfigLoader {

    /**
     * Metoda wczytująca konfigurację symulacji z pliku JSON.
     * @param stage scena, na której ma się pojawić okno wyboru pliku
     * @return wczytana konfiguracja symulacji lub null w przypadku błędu
     */
    public SimulationConfig loadConfigFromJSON(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik JSON z konfiguracją symulacji");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki JSON", "*.json"));

        File chosenFile = fileChooser.showOpenDialog(stage);
        if (chosenFile == null) {
            return null;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(chosenFile);

            // Lista wymaganych pól
            String[] requiredFields = {
                    "mapType", "mapWidth", "mapHeight", "startGrassCount", "grassEnergy", "dailyGrassGrowth",
                    "startAnimalCount", "initialEnergy", "reproductionEnergy", "reproductionCost", "moveCost",
                    "agingAnimalVariant", "minMutations", "maxMutations", "genomeLength", "dayDurationMs"
            };

            // Sprawdzenie obecności wszystkich wymaganych pól
            for (String field : requiredFields) {
                if (!jsonNode.has(field)) {
                    return null;
                }
            }

            SimulationConfigBuilder builder = new SimulationConfigBuilder();
            builder.setMapType(MapType.valueOf(jsonNode.get("mapType").asText()));
            builder.setMapWidth(jsonNode.get("mapWidth").asInt());
            builder.setMapHeight(jsonNode.get("mapHeight").asInt());
            builder.setStartGrassCount(jsonNode.get("startGrassCount").asInt());
            builder.setGrassEnergy(jsonNode.get("grassEnergy").asInt());
            builder.setDailyGrassGrowth(jsonNode.get("dailyGrassGrowth").asInt());
            builder.setStartAnimalCount(jsonNode.get("startAnimalCount").asInt());
            builder.setInitialEnergy(jsonNode.get("initialEnergy").asInt());
            builder.setReproductionEnergy(jsonNode.get("reproductionEnergy").asInt());
            builder.setReproductionCost(jsonNode.get("reproductionCost").asInt());
            builder.setMoveCost(jsonNode.get("moveCost").asInt());
            builder.setAgingAnimalVariant(jsonNode.get("agingAnimalVariant").asBoolean());
            builder.setMinMutations(jsonNode.get("minMutations").asInt());
            builder.setMaxMutations(jsonNode.get("maxMutations").asInt());
            builder.setGenomeLength(jsonNode.get("genomeLength").asInt());
            builder.setDayDurationMs(jsonNode.get("dayDurationMs").asLong());

            return builder.build();
        } catch (IOException | IllegalArgumentException e) {
            return null;
        }
    }
}
