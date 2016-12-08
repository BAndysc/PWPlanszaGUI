package test.gui;

import gra.*;
import test.gui.listeners.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MojaPlanszaGUI extends MojaPlansza {

    private Random random = new Random();
    private int wysokość;
    private int szerokość;
    private boolean opóźnienie;

    /* Listeners */
    private List<IPostaćPostawionaListener> postaćPostawionaListeners = new ArrayList<>();
    private List<IPostaćPrzeuniętaListener> postaćPrzeuniętaListeners = new ArrayList<>();
    private List<IChęćPrzesunięciaListener> chęćPrzeunięciaListeners = new ArrayList<>();
    private List<IPostaćChceSięPostawić> postaćChceSięPostawićListeners = new ArrayList<>();
    private List<IZakleszczenieListener> zakleszczenieListeners = new ArrayList<>();
    private List<IWątekInterruptedListener> wątekInterruptedListeners = new ArrayList<>();
    private List<IPostaćUsuniętaListener> postaćUsuniętaListeners = new ArrayList<>();
    private List<ISprawdzanieListener> sprawdzanieListeners = new ArrayList<>();

    public MojaPlanszaGUI(int wysokość, int szerokość) {
        this(wysokość, szerokość, true);
    }
    public MojaPlanszaGUI(int wysokość, int szerokość, boolean opóźnienie) {
        super(wysokość, szerokość);
        this.wysokość = wysokość;
        this.szerokość = szerokość;
        this.opóźnienie = opóźnienie;
    }

    @Override
    public void postaw(Postać postać, int wiersz, int kolumna)  throws InterruptedException {
        informPostaćChceSięPostawić(postać, wiersz, kolumna);
        try {
            super.postaw(postać, wiersz, kolumna);
        } catch (InterruptedException e) {
            informInterrupted(e);
            throw e;
        }
        informPostaćPostawiona(postać, wiersz, kolumna);
    }

    public int dajSzerokość() {
        return szerokość;
    }

    public int dajWysokość() {
        return wysokość;
    }

    @Override
    public void przesuń(Postać postać, Kierunek kierunek) throws InterruptedException, DeadlockException {
        informChęćPrzesunięcia(postać, kierunek);
        // symulowanie, że przesuwanie chwilę trwa
        if (opóźnienie)
        {
            /* Jeśli cała metoda synchronized: 
            long wait =  random.nextInt(100)+20;
            long wakeAt =  System.currentTimeMillis() +wait;
            while (System.currentTimeMillis() < wakeAt)
                this.wait(wait);
            wpp
            Thread.currentThread.sleep(random.nextInt(100)+20); */
        }

        try {
            super.przesuń(postać, kierunek);
        } catch (InterruptedException e) {
            informInterrupted(e);
            throw e;
        } catch (DeadlockException e) {
            informZakleszczenie();
            throw e;
        }

        informPostaćPrzesunięta(postać, kierunek);
    }

    @Override
    public void usuń(Postać postać) {
        super.usuń(postać);
        informPostaćUsunięta(postać);
    }

    @Override
    public void sprawdź(final int wiersz, final int kolumna, final Akcja jeśliZajęte, final Runnable jeśliWolne) {
        super.sprawdź(wiersz, kolumna, new Akcja() {
            @Override
            public void wykonaj(Postać postać) {
                informZaczynamSprawdzanieIZajęte(wiersz, kolumna, postać);
                jeśliZajęte.wykonaj(postać);
                informSprawdziłemIZajęte(wiersz, kolumna, postać);
            }
        }, new Runnable() {
            @Override
            public void run() {
                informZaczynamSprawdzanieIWolne(wiersz, kolumna);
                jeśliWolne.run();
                informSprawdziłemIWolne(wiersz, kolumna);
            }
        });
    }

    public void dodajPostaćPostawionaListener(IPostaćPostawionaListener listener) {
        postaćPostawionaListeners.add(listener);
    }

    public void dodajPostaćPrzesuniętaListener(IPostaćPrzeuniętaListener listener) {
        postaćPrzeuniętaListeners.add(listener);
    }

    public void dodaChęćPrzesunięciaListener(IChęćPrzesunięciaListener listener) {
        chęćPrzeunięciaListeners.add(listener);
    }

    public void dodajPostaćChceSięPostawićListener(IPostaćChceSięPostawić listener) {
        postaćChceSięPostawićListeners.add(listener);
    }

    public void dodajZakleszczenieListener(IZakleszczenieListener listener) {
        zakleszczenieListeners.add(listener);
    }

    public void dodajWątekInterruptedListener(IWątekInterruptedListener listener) {
        wątekInterruptedListeners.add(listener);
    }

    public void dodajPostaćUsuniętaListener(IPostaćUsuniętaListener listener) {
        postaćUsuniętaListeners.add(listener);
    }

    public void dodajSprawdzanieListener(ISprawdzanieListener listener) { sprawdzanieListeners.add(listener); }

    private void informPostaćPostawiona(Postać postać, int wiersz, int kolumna) {
        for (IPostaćPostawionaListener listener : postaćPostawionaListeners)
            listener.postaćPostawiona(postać, wiersz, kolumna);
    }

    private void informPostaćPrzesunięta(Postać postać, Kierunek kierunek) {
        for (IPostaćPrzeuniętaListener listener : postaćPrzeuniętaListeners)
            listener.postaćPrzesunięta(postać, kierunek);
    }

    private void informPostaćChceSięPostawić(Postać postać, int wiersz, int kolumna) {
        for (IPostaćChceSięPostawić listener : postaćChceSięPostawićListeners)
            listener.postaćChceSięPostawić(postać, wiersz, kolumna);
    }

    private void informChęćPrzesunięcia(Postać postać, Kierunek kierunek) {
        for (IChęćPrzesunięciaListener listener : chęćPrzeunięciaListeners)
            listener.postaćChceSięPrzesunąć(postać, kierunek);
    }

    private void informInterrupted(InterruptedException e) {
        for (IWątekInterruptedListener listener : wątekInterruptedListeners)
            listener.wątekPrzerwałDziałanie(e);
    }

    private void informZakleszczenie() {
        for (IZakleszczenieListener listener : zakleszczenieListeners)
            listener.wystąpiłoZakleszczenie();
    }

    private void informPostaćUsunięta(Postać postać) {
        for (IPostaćUsuniętaListener listener : postaćUsuniętaListeners)
            listener.postaćUsunięta(postać);
    }

    private void informZaczynamSprawdzanieIWolne(int wiersz, int kolumna)
    {
        for (ISprawdzanieListener listener : sprawdzanieListeners)
            listener.sprawdzamIJestWolne(wiersz, kolumna);
    }

    private void informSprawdziłemIWolne(int wiersz, int kolumna)
    {
        for (ISprawdzanieListener listener : sprawdzanieListeners)
            listener.sprawdziłemIByłoWolne(wiersz, kolumna);
    }

    private void informZaczynamSprawdzanieIZajęte(int wiersz, int kolumna, Postać postać)
    {
        for (ISprawdzanieListener listener : sprawdzanieListeners)
            listener.sprawdzamIJestZajęte(wiersz, kolumna, postać);
    }

    private void informSprawdziłemIZajęte(int wiersz, int kolumna, Postać postać)
    {
        for (ISprawdzanieListener listener : sprawdzanieListeners)
            listener.sprawdziłemIByłoZajęte(wiersz, kolumna, postać);
    }


}
