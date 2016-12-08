package test;

import gra.DeadlockException;
import gra.Kierunek;
import test.gui.*;

public class Test1 {
    public static void main(String[] args) {
        MojaPlanszaGUI p = new MojaPlanszaGUI(4, 4);
        GraGUI gui = new GraGUI(p, new OknoSwingGUI(64, 16, 20));

        int[][] zielone = {{0, 0, 0}, {2, 0, 0}, {3, 1, 1}, {3, 3, 2}, {1, 3, 2}, {0, 2, 3}};

        for (final int[] zielony : zielone)
            new Thread(new TesterPionek(p, new PostaćGUI(1, 1, FajnoKolorGenerator.dajKolor(0.5f)), zielony[0], zielony[1]) {
                @Override
                public void run() {
                    try {
                        plansza.postaw(postać, pozycja.getWiersz(), pozycja.getKolumna());

                        switch (zielony[2]){
                            case 0:
                                kierunek = Kierunek.DÓŁ;
                                break;
                            case 1:
                                kierunek = Kierunek.PRAWO;
                                break;
                            case 2:
                                kierunek = Kierunek.GÓRA;
                                break;
                            case 3:
                                kierunek = Kierunek.LEWO;
                        }

                        while (true)
                        {
                            if (wychodziPoza(kierunek))
                                kierunek = następnyKierunek();
                            try {
                                plansza.przesuń(postać, kierunek);
                                pozycja.przesuń(kierunek);
                            } catch (DeadlockException e) {
                               // e.printStackTrace();
                            }
                            Thread.sleep(200);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                private Kierunek następnyKierunek() {
                    switch (kierunek)
                    {
                        case GÓRA:
                            return Kierunek.LEWO;
                        case DÓŁ:
                            return Kierunek.PRAWO;
                        case LEWO:
                            return Kierunek.DÓŁ;
                        case PRAWO:
                            return Kierunek.GÓRA;
                    }
                    return Kierunek.DÓŁ;
                }
            }).start();

        new Thread(new PionekLosowoChodzący(p, new PostaćGUI(2, 2, FajnoKolorGenerator.dajKolor(1f)), 1, 1)).start();
    }
}
