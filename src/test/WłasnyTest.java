package test;


import gra.Postać;
import implementacja.MojaPostać;
import test.gui.GraGUI;
import test.gui.MojaPlanszaGUI;
import test.gui.OknoSwingGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class WłasnyTest {

    final List<Thread> wątki =  new ArrayList<Thread>();
    final Semaphore muteks = new Semaphore(1);
    final AtomicBoolean stan = new AtomicBoolean(true);

    public WłasnyTest() {

        MojaPlanszaGUI p = new MojaPlanszaGUI(30, 30);
        final GraGUI gui = new GraGUI(p, new OknoSwingGUI(24, 6, 6){
            private Random random = new Random();
            @Override
            protected void createUIComponents() {
                super.createUIComponents();
                JToolBar toolbar = new JToolBar();

                final JButton button = new JButton("Stop");
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        boolean s = stan.get();
                        if (s)
                            zatrzymajWątki();
                        else
                            startujWątki();
                        stan.set(!s);
                    }
                });

                final JTextField ileWątków = new JTextField("1");
                ileWątków.setPreferredSize(new Dimension(200, 30));
                final JTextField maksSzer = new JTextField("1");
                maksSzer.setPreferredSize(new Dimension(200, 30));
                final JTextField maksWys = new JTextField("1");
                maksWys.setPreferredSize(new Dimension(200, 30));

                JButton button2 = new JButton("Stwórz tyle wątków");
                button2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int ile = Integer.parseInt(ileWątków.getText());
                        int maksSz = Integer.parseInt(maksSzer.getText());
                        int maksWs = Integer.parseInt(maksWys.getText());
                        for (; ile >= 0; --ile) {
                            int szerokosc = random.nextInt(maksSz)+1;
                            int wysokosc = random.nextInt(maksWs)+1;
                            gra.stwórzWątekZPionkiem(new MojaPostać(szerokosc, wysokosc), random.nextInt(gra.dajPlansza().dajSzerokość()-szerokosc), random.nextInt(gra.dajPlansza().dajWysokość()-wysokosc));
                        }
                    }
                });
                toolbar.add(button);
                toolbar.add(new JSeparator());
                toolbar.add(new JLabel("Maksymalna szerokość: "));
                toolbar.add(maksSzer);
                toolbar.add(new JLabel("Maksymalna wysokość: "));
                toolbar.add(maksWys);
                toolbar.add(new JLabel("Ile wątków "));
                toolbar.add(ileWątków);
                toolbar.add(button2);
                getContentPane().add(toolbar, BorderLayout.NORTH);
            }
        }) {
            @Override
            public void stwórzWątekZPionkiem(Postać postać, int y, int x) {
                Thread wątek = new Thread(new PionekLosowoChodzący(dajPlansza(), postać, y, x, stan));
                wątek.start();
                muteks.acquireUninterruptibly();
                wątki.add(wątek);
                muteks.release();
            }
        };
    }

    private void zatrzymajWątki() {
        muteks.acquireUninterruptibly();
        // @TODO: suspend i resume są deprecated @FIXME
        for (Thread t : wątki)
            t.suspend();
        muteks.release();
    }

    private void startujWątki() {
        muteks.acquireUninterruptibly();
        // @TODO: suspend i resume są deprecated @FIXME
        for (Thread t : wątki)
            t.resume();
        muteks.release();
    }

    public static void main(String[] args) {
        new WłasnyTest();
    }
}
