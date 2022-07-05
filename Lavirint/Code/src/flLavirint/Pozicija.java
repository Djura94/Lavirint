package flLavirint;

/**
 * Sacuvava poziciju u lavirintu.
 */
public class Pozicija
{
    /** Kolona za ovu poziciju */
    public final int kol;
    /** Red za ovu poziciju */
    public final int red;

    public Pozicija(int kol, int red)
    {
        this.kol = kol;
        this.red = red;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other instanceof Pozicija)
        {
            Pozicija o = (Pozicija) other;
            return (kol == o.kol) && (red == o.red);
        }
        else
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        return kol + "," + red;
    }

    /**
     * Generise novu poziciju koja je rezultat kretanja sa ove pozicije
     * u pravcu "dir."
     */
    public Pozicija kreni(Smjer dir)
    {
        switch (dir)
        {
            case NORTH:
                return new Pozicija(kol, red - 1);
            case SOUTH:
                return new Pozicija(kol, red + 1);
            case EAST:
                return new Pozicija(kol + 1, red);
            case WEST:
                return new Pozicija(kol - 1, red);
        }

        return null; // nedostizno
    }

    public int hashCode()
    {
        return (kol << 16) + red;
    }
}