package test;

import gra.*;
import test.gui.MojaPlanszaGUI;

import java.util.concurrent.atomic.AtomicBoolean;

public class PionekLosowoChodzący extends TesterPionek {

    private AtomicBoolean stan;

    public PionekLosowoChodzący(MojaPlanszaGUI plansza, Postać postać, int wiersz, int kolumna) {
        this(plansza, postać, wiersz, kolumna, null);
    }

    public PionekLosowoChodzący(MojaPlanszaGUI plansza, Postać postać, int wiersz, int kolumna, AtomicBoolean stan) {
        super(plansza, postać, wiersz, kolumna);
        this.stan = stan;
    }

    @Override
    public void run() {
        try {
            plansza.postaw(postać, pozycja.getWiersz(), pozycja.getKolumna());
            kierunek = dajKierunekKtóryNieWyjdziePozaPlanszę();

            if (stan != null && !stan.get())
                Thread.currentThread().suspend();

            Thread.sleep(2000);
            while (true) {

                    if (wychodziPoza(kierunek) || czasZmienićKierunek())
                        kierunek = dajKierunekKtóryNieWyjdziePozaPlanszę();
                    try {
                        plansza.przesuń(postać, kierunek);
                        pozycja.przesuń(kierunek);
                    }
                    catch (DeadlockException e) {
                        kierunek = dajKierunekKtóryNieWyjdziePozaPlanszę();
                    }
                    Thread.currentThread().sleep(100);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean czasZmienićKierunek() {
        return random.nextInt(10) == 1;
    }
}
