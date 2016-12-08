package test.gui;

import gra.Kierunek;
import gra.Postać;
import gra.Pozycja;

import java.awt.*;
import java.util.Map;

public interface RysownikPlanszy {
    void ustawGraGUI(GraGUI graGUI);

    void przerysujKiedyś();

    void rysujSiatkę();

    void rysujPostać(Postać postać, boolean nieobecny, Map<Postać, Pozycja> pozycje, Map<Postać, Color> mapaKolorów, Map<Postać, Kierunek> chęćPrzesunięcia);

    void poRysowaniu();

    void rysujWiadomość(String wiadomość);
}
