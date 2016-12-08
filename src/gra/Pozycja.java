package gra;

public class Pozycja {
    private int wiersz;
    private int kolumna;

    public Pozycja(int wiersz, int kolumna) {
        this.wiersz = wiersz;
        this.kolumna = kolumna;
    }

    public Pozycja(Pozycja pozycja, Kierunek kierunek) {
        this(pozycja);
        przesuń(kierunek);
    }

    public Pozycja(Pozycja pozycja) {
        this.wiersz = pozycja.getWiersz();
        this.kolumna = pozycja.getKolumna();
    }

    public int getKolumna() {
        return kolumna;
    }

    public void setKolumna(int kolumna) {
        this.kolumna = kolumna;
    }

    public int getWiersz() {
        return wiersz;
    }

    public void setWiersz(int wiersz) {
        this.wiersz = wiersz;
    }

    public void przesuń(Kierunek kierunek) {
        int dx = (kierunek == Kierunek.LEWO ? -1 : (kierunek == Kierunek.PRAWO ? 1 : 0));
        int dy = (kierunek == Kierunek.GÓRA ? -1 : (kierunek == Kierunek.DÓŁ ? 1 : 0));
        wiersz += dy;
        kolumna += dx;
    }
}