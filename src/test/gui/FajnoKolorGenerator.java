package test.gui;

import java.awt.*;

public class FajnoKolorGenerator {

    private float hue = 0;
    private final static float step = 24.0f/360;

    public Color dajKolejnyFajnyKolor() {
        Color c = dajKolor(hue);
        hue+= step;
        return c;
    }

    public static Color dajKolor(float hue) {
        return Color.getHSBColor(hue, 0.78f, 0.82f);
    }
}
