package test.gui;

import gra.*;
import test.PionekLosowoChodzący;
import test.gui.listeners.*;

import java.awt.*;
import java.util.*;
import java.util.List;


public class GraGUI implements IPostaćPostawionaListener, IPostaćPrzeuniętaListener, IChęćPrzesunięciaListener, IPostaćChceSięPostawić, IZakleszczenieListener, IPostaćUsuniętaListener, IWątekInterruptedListener, ISprawdzanieListener {

    private RysownikPlanszy rysownik;
    private FajnoKolorGenerator generatorKolorów = new FajnoKolorGenerator();

    private Set<Postać> postacie = new HashSet<>();
    private Set<Postać> postacieCzekająceNaPostawienie = new HashSet<>();
    private Map<Postać, Pozycja> pozycje = new HashMap<>();
    private Map<Postać, Color> mapaKolorów = new HashMap<>();
    private Map<Postać, Kierunek> chęćPrzesunięcia = new HashMap<>();

    private static final int MAX_WIADOMOSCI = 5;
    private List<String> wiadomości = new LinkedList<>();

    private MojaPlanszaGUI plansza;


    public GraGUI(MojaPlanszaGUI plansza, RysownikPlanszy rysownik) {
        this.plansza = plansza;
        this.rysownik = rysownik;
        rysownik.ustawGraGUI(this);

        plansza.dodajPostaćPostawionaListener(this);
        plansza.dodajPostaćPrzesuniętaListener(this);
        plansza.dodaChęćPrzesunięciaListener(this);
        plansza.dodajPostaćChceSięPostawićListener(this);
        plansza.dodajZakleszczenieListener(this);
        plansza.dodajPostaćUsuniętaListener(this);
        plansza.dodajWątekInterruptedListener(this);
        plansza.dodajSprawdzanieListener(this);
    }

    public void paintPlansza () {
        rysownik.rysujSiatkę();

        Set<Postać> postacie;
        Set<Postać> postacieCzekająceNaPostawienie;
        Map<Postać, Pozycja> pozycje;
        Map<Postać, Color> mapaKolorów;
        Map<Postać, Kierunek> chęćPrzesunięcia;
        List<String> wiadomości;

        synchronized (this)
        {
            postacie = new HashSet<>(this.postacie);
            postacieCzekająceNaPostawienie = new HashSet<>(this.postacieCzekająceNaPostawienie);
            pozycje = new HashMap<>(this.pozycje);
            mapaKolorów = new HashMap<>(this.mapaKolorów);
            chęćPrzesunięcia = new HashMap<>(this.chęćPrzesunięcia);
            wiadomości = new LinkedList<>(this.wiadomości);
        }

        for (Postać postać : postacie)
            rysownik.rysujPostać(postać, false, pozycje, mapaKolorów, chęćPrzesunięcia);

        for (Postać postać : postacieCzekająceNaPostawienie)
            rysownik.rysujPostać( postać, true, pozycje, mapaKolorów, chęćPrzesunięcia);

        for (String wiadomość : wiadomości)
            rysownik.rysujWiadomość(wiadomość);

        rysownik.poRysowaniu();
    }

    @Override
    public void postaćPostawiona(Postać postać, int wiersz, int kolumna) {
        synchronized (this)
        {
            postacieCzekająceNaPostawienie.remove(postać);
            postacie.add(postać);
            pozycje.put(postać, new Pozycja(wiersz, kolumna));
            if (postać instanceof PostaćGUI)
                mapaKolorów.put(postać, ((PostaćGUI)postać).dajKolor());
            else
                mapaKolorów.put(postać, generatorKolorów.dajKolejnyFajnyKolor());
        }
        rysownik.przerysujKiedyś();
    }

    @Override
    public void postaćPrzesunięta(Postać postać, Kierunek kierunek) {
        synchronized (this)
        {
            chęćPrzesunięcia.remove(postać);
            pozycje.get(postać).przesuń(kierunek);
        }
        rysownik.przerysujKiedyś();
    }


    @Override
    public void postaćChceSięPrzesunąć(Postać postać, Kierunek kierunek) {
        synchronized (this)
        {
            chęćPrzesunięcia.put(postać, kierunek);
        }
        rysownik.przerysujKiedyś();
    }

    @Override
    public void postaćChceSięPostawić(Postać postać, int wiersz, int kolumna) {
        synchronized (this)
        {
            pozycje.put(postać, new Pozycja(wiersz, kolumna));
            postacieCzekająceNaPostawienie.add(postać);
        }
        rysownik.przerysujKiedyś();
    }

    @Override
    public void wystąpiłoZakleszczenie() {
        dodajWiadomość("Wątek "+Thread.currentThread().getName()+" się zakleszczył");
    }

    @Override
    public void postaćUsunięta(Postać postać) {
        synchronized (this)
        {
            pozycje.remove(postać);
            mapaKolorów.remove(postać);
            chęćPrzesunięcia.remove(postać);
            postacie.remove(postać);
            postacieCzekająceNaPostawienie.remove(postać);
        }
        rysownik.przerysujKiedyś();
    }

    public MojaPlanszaGUI dajPlansza() {
        return plansza;
    }

    @Override
    public void wątekPrzerwałDziałanie(InterruptedException e) {
        dodajWiadomość("Wątek "+Thread.currentThread().getName()+": "+e.getMessage());
    }

    private void dodajWiadomość(String s) {
        synchronized (this)
        {
            wiadomości.add(s);
            if (wiadomości.size() > MAX_WIADOMOSCI)
                wiadomości.remove(0);
        }
    }

    public void stwórzWątekZPionkiem(Postać mojaPostać, int y, int x) {
        new Thread(new PionekLosowoChodzący(plansza, mojaPostać, y, x)).start();
    }

    @Override
    public void sprawdzamIJestWolne(int wiersz, int kolumna) {
        dodajWiadomość(String.format("Wątek %s: sprawdza pole (%d, %d) i jest wolne", Thread.currentThread().getName(), kolumna, wiersz));
    }

    @Override
    public void sprawdziłemIByłoWolne(int wiersz, int kolumna) {
        dodajWiadomość(String.format("Wątek %s: sprawdził pole (%d, %d) i było wolne", Thread.currentThread().getName(), kolumna, wiersz));
    }

    @Override
    public void sprawdzamIJestZajęte(int wiersz, int kolumna, Postać postać) {
        dodajWiadomość(String.format("Wątek %s: sprawdza pole (%d, %d) i jest zajęte przez: %s", Thread.currentThread().getName(), kolumna, wiersz, postać.toString()));
    }

    @Override
    public void sprawdziłemIByłoZajęte(int wiersz, int kolumna, Postać postać) {
        dodajWiadomość(String.format("Wątek %s: sprawdził pole (%d, %d) i było zajęte przez: %s", Thread.currentThread().getName(), kolumna, wiersz, postać.toString()));
    }
}
