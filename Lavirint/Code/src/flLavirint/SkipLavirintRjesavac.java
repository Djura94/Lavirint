package flLavirint;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Superklasa za rjesavace koji se granaju samo na tackama izbora.
 */
public abstract class SkipLavirintRjesavac extends LavirintRjesavac
{
    public class RjesenjeNadjeno extends Exception
    {
        public Pozicija pos;
        public Smjer from;

        public RjesenjeNadjeno(Pozicija pos, Smjer from)
        {
            this.pos = pos;
            this.from = from;
        }
    }

    public SkipLavirintRjesavac(Lavirint lavirint)
    {
        super(lavirint);
    }

    /** Vraca prvi izbor sa zadate pozicije */
    public Izbor prviIzbor(Pozicija pos) throws RjesenjeNadjeno
    {
        LinkedList<Smjer> potezi;

        potezi = lavirint.dobijPoteze(pos);
        if (potezi.size() == 1) return prati(pos, potezi.getFirst());
        else return new Izbor(pos, null, potezi);
    }

    /**
     * Prati putanju do tacke izbora. Vraca izbor na koji je naisao. /*
     * Ako je naisao na corsokak, vraca objekat tipa Izbor cije 'at' polje je
     * lokacija corsokaka i cija lista 'izbora' je prazna.
     * 
     * @param at
     *            Pozicija sa koje se krece.
     * @param dir
     *            Smjer u kom se ide.
     */
    public Izbor prati(Pozicija at, Smjer dir) throws RjesenjeNadjeno
    {
        LinkedList<Smjer> izbori;
        Smjer go_to = dir, came_from = dir.reverse();

        at = at.kreni(go_to);
        do
        {
            if (at.equals(lavirint.getEnd())) throw new RjesenjeNadjeno(at, go_to.reverse());
            if (at.equals(lavirint.getStart())) throw new RjesenjeNadjeno(at, go_to.reverse());
            izbori = lavirint.dobijPoteze(at);
            izbori.remove(came_from);

            if (izbori.size() == 1)
            {
                go_to = izbori.getFirst();
                at = at.kreni(go_to);
                came_from = go_to.reverse();
            }
        } while (izbori.size() == 1);

        // vraca novi Izbor(at,izbori);
        Izbor ret = new Izbor(at, came_from, izbori);
        return ret;
    }

    /**
     * Prati putanju do tacke izbora. Oznaca celije kroz koje prolazi
     * odredjenom bojom.
     * 
     * @param at
     *            Pozicija sa koje se krece.
     * @param dir
     *            Smjer u kom se ide.
     * @param color
     *            Boja kojom se oznacava.
     */
    public Izbor pratiOznaci(Pozicija at, Smjer dir, int color) throws RjesenjeNadjeno
    {
        LinkedList<Smjer> izbori;
        Smjer go_to = dir, came_from = dir.reverse();

        lavirint.postaviBoju(at, color);
        at = at.kreni(go_to);
        do
        {
            lavirint.postaviBoju(at, color);
            if (at.equals(lavirint.getEnd())) throw new RjesenjeNadjeno(at, go_to.reverse());
            if (at.equals(lavirint.getStart())) throw new RjesenjeNadjeno(at, go_to.reverse());
            izbori = lavirint.dobijPoteze(at);
            izbori.remove(came_from);

            if (izbori.size() == 1)
            {
                go_to = izbori.getFirst();
                at = at.kreni(go_to);
                came_from = go_to.reverse();
            }
        } while (izbori.size() == 1);

        return new Izbor(at, came_from, izbori);
    }

    /**
     * Oznacava putanju
     * 
     * @param path
     * @param color
     * @throws RjesenjeNadjeno
     */
    public void oznaciPutanju(List<Smjer> path, int color)
    {
        try
        {
            Izbor izbor = prviIzbor(lavirint.getStart());

            Pozicija at = izbor.at;
            Iterator<Smjer> iter = path.iterator();
            while (iter.hasNext())
            {
                izbor = pratiOznaci(at, iter.next(), color);
                at = izbor.at;
            }
        }
        catch (RjesenjeNadjeno e)
        {
        }
    }

    public List<Smjer> putDoPunePutanje(List<Smjer> path)
    {
        Iterator<Smjer> pathIter = path.iterator();
        LinkedList<Smjer> fullPath = new LinkedList<Smjer>();

        // Dobijanje rjesenja tj. putanje od pocetka do kraja.
        Pozicija curr = lavirint.getStart();
        Smjer go_to = null, came_from = null;
        while (!curr.equals(lavirint.getEnd()))
        {
            LinkedList<Smjer> moves = lavirint.dobijPoteze(curr);
            moves.remove(came_from);
            if (moves.size() == 1) go_to = moves.getFirst();
            else if (moves.size() > 1) go_to = pathIter.next();
            else if (moves.size() == 0)
            {
                System.out.println("Greska u rjesenju -- dovodi do corsokaka.");
                throw (new Error());
                // System.exit(-1);
            }
            fullPath.add(go_to);
            curr = curr.kreni(go_to);
            came_from = go_to.reverse();
        }

        return fullPath;
    }
}
