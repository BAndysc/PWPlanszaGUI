package test.gui.listeners;

import gra.Postać;

public interface ISprawdzanieListener {
    void sprawdzamIJestWolne(int wiersz, int kolumna);
    void sprawdziłemIByłoWolne(int wiersz, int kolumna);
    void sprawdzamIJestZajęte(int wiersz, int kolumna, Postać postać);
    void sprawdziłemIByłoZajęte(int wiersz, int kolumna, Postać postać);
}

