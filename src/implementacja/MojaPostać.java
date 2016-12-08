package implementacja;

import gra.Postać;

public class MojaPostać implements Postać{

    private final int szerokość;
    private final int wysokość;

    public MojaPostać(int szerokość, int wysokość) {
        this.szerokość = szerokość;
        this.wysokość = wysokość;
    }

    @Override
    public int dajWysokość() {
        return wysokość;
    }

    @Override
    public int dajSzerokość() {
        return szerokość;
    }

    @Override
    public String toString() {
        return "MojaPostać{" +
                "szerokość=" + szerokość +
                ", wysokość=" + wysokość +
                '}';
    }
}
