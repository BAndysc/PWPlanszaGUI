package test;


import gra.Postać;
import implementacja.MojaPostać;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

public class WłasnyTest {

    final List<Thread> wątki =  new ArrayList<Thread>();
    final Semaphore muteks = new Semaphore(1);
    final BooleanProperty stan = new SimpleBooleanProperty(true);

    public WłasnyTest() {

        MojaPlanszaGUI p = new MojaPlanszaGUI(30, 30);
        final GraGUI gui = new GraGUI(p, new OknoSwingGUI(24, 6, 6){
            private Random random = new Random();
            @Override
            protected void createUIComponents() {
                super.createUIComponents();
                JToolBar toolbar = new JToolBar();

                final JButton button = new JButton("Stop");
                stan.addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        button.setText(newValue?"Stop":"Start");
                    }
                });
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (stan.getValue())
                            zatrzymajWątki();
                        else
                            startujWątki();
                        stan.set(!stan.get());
                    }
                });

                final JTextField ileWątków = new JTextField("1");
                final JTextField maksSzer = new JTextField("1");
                final JTextField maksWys = new JTextField("1");

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
        		// przekazywanie tam stanu to proszenie się o błędy :v
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
