package flLavirint;

import java.util.LinkedList;
import java.util.List;

/**
 * Jedno-tredovski algoritam za rjesavanje po sirini.
 */
public class AlgoritamSirina extends SkipLavirintRjesavac
{
    public class RjesenjeCvor
    {
        public RjesenjeCvor roditelj;
        public Izbor izbor;

        public RjesenjeCvor(RjesenjeCvor roditelj, Izbor izbor)
        {
            this.roditelj = roditelj;
            this.izbor = izbor;
        }
    }

    public AlgoritamSirina(Lavirint lavirint)
    {
        super(lavirint);
    }

    Smjer istrazivanje = null;

    /**
     * Prosiruje cvor u pretrazi stabla, tako sto vraca listu 'djece' cvorova.
     * 
     * @throws RjesenjeNadjeno
     */
    public List<RjesenjeCvor> expand(RjesenjeCvor cvor) throws RjesenjeNadjeno
    {
        LinkedList<RjesenjeCvor> result = new LinkedList<RjesenjeCvor>();
        if (lavirint.display != null) lavirint.postaviBoju(cvor.izbor.at, 0);
        for (Smjer dir : cvor.izbor.izbori)
        {
            istrazivanje = dir;
            Izbor noviIzbor = prati(cvor.izbor.at, dir);
            if (lavirint.display != null) lavirint.postaviBoju(noviIzbor.at, 2);
            result.add(new RjesenjeCvor(cvor, noviIzbor));
        }
        return result;
    }

    /**
     * Izvrsava pretragu lavirinta po sirini. Algoritam pravi stablo ciji je 
     * korijen u startnoj poziciji. Roditeljski pokazivaci se koriste da pokazu put nazad
     * do ulaza. Algoritam cuva listu listova u varijabli 'suma'.
     * Tokom svake iteracije, ovi listovi se sire i njihova djeca postaju nova 'suma'.
     * Ako cvor predstavlja corsokak, odbacuje se. Izvrsavanje prestaje kada je izlaz pronadjen,
     * kao sto je pokazano u izuzetku RjesenjeNadjeno.
     */
    public List<Smjer> rjesi()
    {
        RjesenjeCvor curr = null;
        LinkedList<RjesenjeCvor> suma = new LinkedList<RjesenjeCvor>();

        try
        {
            suma.push(new RjesenjeCvor(null, prviIzbor(lavirint.getStart())));
            while (!suma.isEmpty())
            {
                LinkedList<RjesenjeCvor> novaSuma = new LinkedList<RjesenjeCvor>();
                for (RjesenjeCvor cvor : suma)
                {
                    if (!cvor.izbor.isDeadend())
                    {
                        curr = cvor;
                        novaSuma.addAll(expand(cvor));
                    }
                    else if (lavirint.display != null)
                    {
                        lavirint.postaviBoju(cvor.izbor.at, 0);
                    }
                }
                suma = novaSuma;
                if (lavirint.display != null)
                {
                    lavirint.display.updateDisplay();
                    try
                    {
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e)
                    {
                    }
                    // Moze se koristiti i: lavirint.display.waitForMouse();
                    // ako zelimo da zaustavimo obavljanje sve dok ne kliknemo misem.
                }
            }
            // Rjesenje nije nadjeno.
            return null;
        }
        catch (RjesenjeNadjeno e)
        {
            if (curr == null)
            {
                // Ovo se desava samo kad postoji direktna putanja od pocetka do kraja

                return putDoPunePutanje(lavirint.dobijPoteze(lavirint.getStart()));
            }
            else
            {
                LinkedList<Smjer> soln = new LinkedList<Smjer>();
                // Prvo cuvamo u kom smjeru smo isli kada smo pronasli rjesenje
                soln.addFirst(istrazivanje);
                while (curr != null)
                {
                    try
                    {
                        Izbor vratiSeNazad = pratiOznaci(curr.izbor.at, curr.izbor.from, 1);
                        if (lavirint.display != null)
                        {
                            lavirint.display.updateDisplay();
                        }
                        soln.addFirst(vratiSeNazad.from);
                        curr = curr.roditelj;
                    }
                    catch (RjesenjeNadjeno e2)
                    {
                        // Ako postoji tacka izbora na pocetku onda treba zapamtiti koji pravac trebamo izabrati
                        if (lavirint.dobijPoteze(lavirint.getStart()).size() > 1) soln.addFirst(e2.from);
                        if (lavirint.display != null)
                        {
                            oznaciPutanju(soln, 1);
                            lavirint.display.updateDisplay();
                        }
                        return putDoPunePutanje(soln);
                    }
                }
                oznaciPutanju(soln, 1);
                return putDoPunePutanje(soln);
            }
        }
    }
}
