package test;


import gra.*;
import implementacja.MojaPostać;
import test.gui.GraGUI;
import test.gui.MojaPlanszaGUI;
import test.gui.OknoSwingGUI;
import test.gui.PostaćGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;

public class SnakeTest implements KeyListener {

    WążRunnable głowa;
    Kierunek kierunek;
    
    public SnakeTest() {
        final MojaPlanszaGUI p = new MojaPlanszaGUI(40, 40, false);
        głowa = new WążRunnable(p, new MojaPostać(1, 1), 10, 20, 0, null, null, 5);
        final SnakeTest parent = this;
        GraGUI gui = new GraGUI(p, new OknoSwingGUI() {
            @Override
            protected void createUIComponents() {
                super.createUIComponents();
                this.addKeyListener(parent);
                JOptionPane.showMessageDialog(this, "Używaj strzałek do sterowania węzem. Snejk się buguje.");
            }
        });

        new Thread(głowa).start();

        new Thread(new JedzenieDlaWężaRunnable(p)).start();
    }

    public static void main(String[] args) {
        new SnakeTest();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_UP:
                ustawKierunek(Kierunek.GÓRA);
                break;
            case KeyEvent.VK_DOWN:
                ustawKierunek(Kierunek.DÓŁ);
                break;
            case KeyEvent.VK_LEFT:
                ustawKierunek(Kierunek.LEWO);
                break;
            case KeyEvent.VK_RIGHT:
                ustawKierunek(Kierunek.PRAWO);
                break;
        }
    }

    private void ustawKierunek(Kierunek kierunek) {
        głowa.ustawKierunek(kierunek);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private class WążRunnable extends TesterPionek {
        private ConcurrentLinkedDeque<Kierunek> kierunki = new ConcurrentLinkedDeque<>();
        private WążRunnable kolejnySegment = null;
        private Kierunek ostatniKierunek = null;
        private int ktorySegment;
        private Semaphore czekajNaPrzód;
        private Semaphore ja = new Semaphore(0);
        private boolean usuńSię;
        private int rosnijO;

        public WążRunnable(MojaPlanszaGUI plansza, Postać postać, int wiersz, int kolumna, int ktorySegment, Semaphore przod, ConcurrentLinkedDeque<Kierunek> kierunki, int rosnijO) {
            super(plansza, postać, wiersz, kolumna);
            this.ktorySegment = ktorySegment;
            this.czekajNaPrzód = przod;

            this.rosnijO = rosnijO;

            if (kierunki != null)
                this.kierunki = kierunki;
        }

        @Override
        public  void run() {
            try {
                plansza.postaw(postać, pozycja.getWiersz(), pozycja.getKolumna());

                while (true)
                {
                    try {
                        if (czekajNaPrzód != null)
                            czekajNaPrzód.acquireUninterruptibly();

                        if (usuwamSię())
                            break;

                        if (kierunki.size() > 0)
                        {
                            Kierunek k = kierunki.remove();
                            Pozycja nowa = new Pozycja(pozycja, k);
                            plansza.sprawdź(nowa.getWiersz(), nowa.getKolumna(), new Akcja() {
                                @Override
                                public void wykonaj(Postać postać) {
                                    if (wszedłemNa(postać))
                                        plansza.usuń(postać);
                                }
                            }, new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                            plansza.przesuń(postać, k);


                            if (kolejnySegment != null)
                                ja.release();

                            urośnijJeśliTrzeba(k);
                            pozycja.przesuń(k);

                            if (ktorySegment == 0)
                                ustawKierunek(ostatniKierunek);
                        }

                        if (ktorySegment == 0)
                            Thread.currentThread().sleep(100);

                    } catch (DeadlockException e) {
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void urośnijJeśliTrzeba(Kierunek ostatniKierunek) {
            if (rosnijO > 0)
            {
                Pozycja po = new Pozycja(pozycja);
                ConcurrentLinkedDeque<Kierunek> deq = new ConcurrentLinkedDeque<Kierunek>(kierunki);
                deq.offerFirst(ostatniKierunek);
                kolejnySegment = new WążRunnable(plansza, new MojaPostać(1, 1), po.getWiersz(), po.getKolumna(), ktorySegment +1, ja, deq, rosnijO-1);
                new Thread(kolejnySegment).start();
                rosnijO = 0;
            }
        }

        private boolean usuwamSię() {
            if (usuńSię)
                plansza.usuń(postać);
            return usuńSię;
        }

        private synchronized void ustawKierunek(Kierunek kierunek) {
            ostatniKierunek = kierunek;
            usunZbedne();

            this.kierunki.add(kierunek);

            if (kolejnySegment != null)
                kolejnySegment.ustawKierunek(kierunek);
        }

        private synchronized void usunZbedne() {
            while (this.kierunki.size() > ktorySegment)
                this.kierunki.removeLast();

            if (kolejnySegment != null)
                kolejnySegment.usunZbedne();
        }

        public synchronized boolean wszedłemNa(Postać postać) {
            if (kolejnySegment == null)
            {
                rosnijO = postać.dajWysokość() * postać.dajSzerokość();
                return true;
            }
            else if (postać == kolejnySegment.dajPostać())
            {
                kolejnySegment.zjedzSię();
                ja.release();
                kolejnySegment = null;
                return false;
            }
            else
                return kolejnySegment.wszedłemNa(postać);
        }

        private Postać dajPostać() {
            return postać;
        }

        private synchronized void zjedzSię() {
            if (kolejnySegment != null) {
                kolejnySegment.zjedzSię();
                ja.release();
                kolejnySegment = null;
            }
            usuńSię = true;
        }
    }

    private class JedzenieDlaWężaRunnable implements Runnable {
        private MojaPlanszaGUI plansza;
        private Random random = new Random();

        @Override
        public void run() {
            while (true) {
                final int x = random.nextInt(plansza.dajSzerokość()-1);
                final int y = random.nextInt(plansza.dajWysokość()-1);
                try {
                    plansza.sprawdź(y, x, new Akcja() {
                        @Override
                        public void wykonaj(Postać postać) {

                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                            try {
                                plansza.postaw(new PostaćGUI(1, 1, Color.black), y, x);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Thread.currentThread().sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public JedzenieDlaWężaRunnable(MojaPlanszaGUI plansza) {
            this.plansza = plansza;
        }
    }
}
