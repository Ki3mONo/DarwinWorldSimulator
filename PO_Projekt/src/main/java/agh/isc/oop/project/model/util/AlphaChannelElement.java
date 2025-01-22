package agh.isc.oop.project.model.util;

import agh.isc.oop.project.model.elements.WorldElement;

/**
 * Klasa reprezentująca "pusty" element z mapy symulacji.
 * Do celów pomocniczych wizualizacji.
 */
public class AlphaChannelElement implements WorldElement {

    @Override
    public Vector2d getPosition() {
        return null;
    }

    @Override
    public String getResourceName() {
        return "/world/alpha_channel.png";
    }
}
