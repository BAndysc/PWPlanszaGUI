package test;

import implementacja.MojaPostać;
import test.gui.GraGUI;
import test.gui.MojaPlanszaGUI;
import test.gui.OknoSwingGUI;

import java.util.Random;

public class Test2 {

    private static  final int SZEROKOSC = 120;
    private static  final int WYSOKOSC = 80;

    public static void main(String[] args) {

        MojaPlanszaGUI p = new MojaPlanszaGUI(WYSOKOSC, SZEROKOSC);
        GraGUI gui = new GraGUI(p, new OknoSwingGUI());

        Random random = new Random();

        for (int i = 0; i < WYSOKOSC/2-1; i++) {
            for (int j = 0; j < SZEROKOSC/2-1; ++j)
            {
                new Thread(new PionekLosowoChodzący(p, new MojaPostać(1, 1), i*2+j%2, j)).start();
                // int szer = random.nextInt(3)+1;
                // int wys = random.nextInt(3)+1;
                // new Thread(new PionekLosowoChodzący(p, new MojaPostać(szer, wys), random.nextInt(WYSOKOSC-wys-1), random.nextInt(SZEROKOSC-szer-1))).start();
            }
       }
    }

}
