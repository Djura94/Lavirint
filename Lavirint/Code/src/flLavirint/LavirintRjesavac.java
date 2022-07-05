package flLavirint;

import java.util.List;

/**
 * Superklasa za sve algoritme za rjesavanje
 */
public abstract class LavirintRjesavac
{
    protected Lavirint lavirint;

    public LavirintRjesavac(Lavirint lavirint)
    {
        this.lavirint = lavirint;
    }

    /**
     * Rijesi lavirint i vrati rjesenje. Rjesenje predstavlja listu smjernica
     * koje vode od pocetka lavirinta do kraja. Ako ne postoji rjesenje,
     * null se vraca.

     * 
     * @return Lista smjernica koje bi vodile "misa" od pocetka lavirinta
     * do kraja.
     */
    public abstract List<Smjer> rjesi();
}
