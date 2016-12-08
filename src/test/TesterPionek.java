package test;

import gra.Kierunek;
import gra.MojaPlansza;
import gra.Postać;
import gra.Pozycja;
import test.gui.MojaPlanszaGUI;

import java.util.Random;

public abstract class TesterPionek implements Runnable {
    protected final Random random = new Random();
    protected final MojaPlanszaGUI plansza;
    protected final Postać postać;
    protected Kierunek kierunek;
    protected int jakDlugo = 0;
    protected Pozycja pozycja;

    public TesterPionek(MojaPlanszaGUI plansza, Postać postać, int wiersz, int kolumna) {
        this.plansza = plansza;
        this.postać = postać;
        pozycja = new Pozycja(wiersz, kolumna);
    }

    protected Kierunek dajKierunekKtóryNieWyjdziePozaPlanszę() {
        Kierunek k;
        do
            k = losujKierunek();
        while (wychodziPoza(k));

        return k;
    }

    protected boolean wychodziPoza(Kierunek kierunek) {
        Pozycja p = new Pozycja(pozycja, kierunek);

        if (p.getKolumna() < 0 || p.getWiersz() < 0)
            return true;

        if (p.getKolumna() + postać.dajSzerokość() > plansza.dajSzerokość())
            return true;

        if (p.getWiersz() + postać.dajWysokość() > plansza.dajWysokość())
            return true;

        return false;
    }

    protected Kierunek losujKierunek() {
        switch (random.nextInt(4)) {
            case 0:
                return Kierunek.DÓŁ;
            case 1:
                return Kierunek.GÓRA;
            case 2:
                return Kierunek.LEWO;
            case 3:
                return Kierunek.PRAWO;
        }
        return Kierunek.DÓŁ;
    }


}
