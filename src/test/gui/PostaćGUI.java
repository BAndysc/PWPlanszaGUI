package test.gui;

import implementacja.MojaPostać;

import java.awt.*;

public class PostaćGUI extends MojaPostać {

    private final Color kolor;

    public PostaćGUI(int szerokość, int wysokość, Color kolorPostaci) {
        super(szerokość, wysokość);
        kolor = kolorPostaci;
    }

    public Color dajKolor() {
        return kolor;
    }
}
