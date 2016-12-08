package test.gui;


import gra.Kierunek;
import gra.Postać;
import gra.Pozycja;
import implementacja.MojaPostać;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.Map;

public class OknoSwingGUI extends JFrame implements RysownikPlanszy {

    private final static int SKALA = 2;
    private final static int POLE_WYMIAR = 32 / SKALA;
    private final static int PADDING = 8 / SKALA;
    private final static int ARROW_BASE = 10 / SKALA;

    private final static int DEFAULT_WINDOW_WIDTH = 600;
    private final static int DEFAULT_WINDOW_HEIGHT = 500;

    private JPanel drawer;
    private Rectangle hoverPionek = new Rectangle(0,0,1,1);
    protected GraGUI gra;
    private int rozmiarPola;
    private int odstęp;
    private int strzałka;

    public OknoSwingGUI() {
        this(POLE_WYMIAR, PADDING, ARROW_BASE);
    }
    
    public OknoSwingGUI(int rozmiarPola, int odstęp, int strzałka) {
        super("by Korczyn");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }

        this.rozmiarPola = rozmiarPola;
        this.odstęp = odstęp;
        this.strzałka = strzałka;

        createUIComponents();
        setSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private Graphics currentG = null;
    private int wiadomośćYOffset = 20;
    private Point myszStart=  new Point();
    private Point offset = new Point();
    private Point myszStartOffset = null;

    protected void createUIComponents() {
        drawer = new JPanel(true) {
            @Override
            public void paint(Graphics g) {
                currentG = g;
                wiadomośćYOffset = 20;
                gra.paintPlansza();
            }
        };

        drawer.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hoverPionek.x = (int)Math.floor((e.getX() - (int)offset.getX())/(rozmiarPola+1.0)) ;
                hoverPionek.y = (int)Math.floor((e.getY() - (int)offset.getY())/(rozmiarPola+1.0)) ;
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                offset.setLocation(myszStartOffset.getX() + e.getX() - myszStart.getX(), myszStartOffset.getY() + e.getY() - myszStart.getY());
                repaint();
            }
        });

        drawer.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                myszStartOffset = new Point(offset);
                myszStart.setLocation(e.getX(), e.getY());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                hoverPionek.x = (int)Math.floor((e.getX() - (int)offset.getX())/(rozmiarPola+1.0)) ;
                hoverPionek.y = (int)Math.floor((e.getY() - (int)offset.getY())/(rozmiarPola+1.0)) ;
                gra.stwórzWątekZPionkiem(new MojaPostać(hoverPionek.width, hoverPionek.height), hoverPionek.y, hoverPionek.x);
            }
        });

        drawer.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isShiftDown())
                    hoverPionek.width = Math.max(1, hoverPionek.width - e.getUnitsToScroll()/Math.abs(e.getUnitsToScroll()));
                else
                    hoverPionek.height = Math.max(1, hoverPionek.height - e.getUnitsToScroll()/Math.abs(e.getUnitsToScroll()));
                repaint();
            }
        });

        this.add(drawer);
    }


    private Rectangle relatywnyDoAbsolutnego(int x, int y, int szerokość, int wysokość)
    {
        Rectangle rectangle = new Rectangle();
        rectangle.width = (rozmiarPola+1) *szerokość-2*odstęp;
        rectangle.height = (rozmiarPola+1)*wysokość - 2*odstęp;
        rectangle.x =x * (rozmiarPola+1)+odstęp + (int)offset.getX();
        rectangle.y = y*(rozmiarPola+1)+odstęp + (int)offset.getY();
        return rectangle;
    }

    private Color kolorNieobecny = new Color(0, 0, 0, 20);

    @Override
    public void ustawGraGUI(GraGUI graGUI) {
        gra = graGUI;
        offset.setLocation(DEFAULT_WINDOW_WIDTH/2 - graGUI.dajPlansza().dajSzerokość()*(rozmiarPola+1)/2, DEFAULT_WINDOW_HEIGHT/2 - graGUI.dajPlansza().dajWysokość()*(rozmiarPola+1)/2);
    }

    @Override
    public void przerysujKiedyś() {
        repaint();
    }

    @Override
    public void rysujSiatkę() {
        currentG.clearRect(0, 0, currentG.getClipBounds().width, currentG.getClipBounds().height);
        currentG.setColor(Color.lightGray);

        int pionKoniec = (gra.dajPlansza().dajWysokość()) * (rozmiarPola+1) - 1+ offset.y;
        int poziomKoniec = (gra.dajPlansza().dajSzerokość()) * (rozmiarPola+1) - 1+ offset.x;

        for (int i = 0; i <= gra.dajPlansza().dajSzerokość(); ++i) {
            int x = i * (rozmiarPola+1) - 1 + (int)offset.getX();
            currentG.drawLine(x, offset.y, x, pionKoniec);
        }

        for (int j = 0; j <= gra.dajPlansza().dajWysokość(); j++) {
            int y = j * (rozmiarPola+1) - 1 + (int)offset.getY();
            currentG.drawLine(offset.x, y, poziomKoniec, y);
        }
    }

    @Override
    public void rysujPostać(Postać postać, boolean nieobecny, Map<Postać, Pozycja> pozycje, Map<Postać, Color> mapaKolorów, Map<Postać, Kierunek> chęćPrzesunięcia) {
        Pozycja pozycja = pozycje.get(postać);
        if (nieobecny)
            currentG.setColor(kolorNieobecny);
        else
            currentG.setColor(mapaKolorów.get(postać));
        Rectangle absolutne = relatywnyDoAbsolutnego(pozycja.getKolumna(), pozycja.getWiersz(), postać.dajSzerokość(), postać.dajWysokość());

        currentG.fillRect(absolutne.x, absolutne.y, absolutne.width, absolutne.height);

        if (chęćPrzesunięcia.containsKey(postać)) {
            Polygon poly = new Polygon();
            Kierunek k = chęćPrzesunięcia.get(postać);
            switch(k) {
                case GÓRA:
                    poly.addPoint(absolutne.x+absolutne.width/2 - strzałka/2, absolutne.y);
                    poly.addPoint(absolutne.x+absolutne.width/2 + strzałka/2, absolutne.y);
                    poly.addPoint(absolutne.x+absolutne.width/2, absolutne.y-odstęp);
                    break;
                case DÓŁ:
                    poly.addPoint(absolutne.x+absolutne.width/2 - strzałka/2, absolutne.y + absolutne.height - 1);
                    poly.addPoint(absolutne.x+absolutne.width/2 + strzałka/2, absolutne.y+ absolutne.height - 1);
                    poly.addPoint(absolutne.x+absolutne.width/2, absolutne.y+ absolutne.height+odstęp - 1);
                    break;
                case LEWO:
                    poly.addPoint(absolutne.x, absolutne.y + absolutne.height/2- strzałka/2);
                    poly.addPoint(absolutne.x, absolutne.y + absolutne.height/2+ strzałka/2);
                    poly.addPoint(absolutne.x-odstęp, absolutne.y+absolutne.height/2);
                    break;
                case PRAWO:
                    poly.addPoint(absolutne.x+absolutne.width-1, absolutne.y + absolutne.height/2- strzałka/2);
                    poly.addPoint(absolutne.x+absolutne.width-1, absolutne.y + absolutne.height/2+ strzałka/2);
                    poly.addPoint(absolutne.x+odstęp+absolutne.width-1, absolutne.y+absolutne.height/2);
                    break;
            }

            currentG.fillPolygon(poly);
        }
    }

    @Override
    public void poRysowaniu() {
        currentG.setColor(new Color(80, 80, 80, 127));
        Rectangle absolutne = relatywnyDoAbsolutnego(hoverPionek.x, hoverPionek.y, hoverPionek.width, hoverPionek.height);
        currentG.fillRect(absolutne.x, absolutne.y, absolutne.width, absolutne.height);
    }

    @Override
    public void rysujWiadomość(String wiadomość) {
        currentG.setFont(Font.decode("Monospaced"));
        currentG.setColor(Color.black);
        currentG.drawString(wiadomość, 10, wiadomośćYOffset);
        wiadomośćYOffset+= 15;
    }
}
