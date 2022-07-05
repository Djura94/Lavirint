package flLavirint;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Jedno-tredovski algoritam za rjesavanje po dubini.
 */
public class AlgoritamDubina extends SkipLavirintRjesavac
{
    public AlgoritamDubina(Lavirint lavirint)
    {
        super(lavirint);
    }

    /**
     * Vrsi se pretraga po dubini da bi se nasao izlaz. Algoritam radi na principu
     * odrzavanja steka izbora. Tokom svake iteracije, izbor koji je na 
     * vrhu steka se izucava. Ako je izbor.isEmpty() true, onda smo mi dosli 
     * do corsokaka i moramo se vratiti tako sto cemo raditi pop steka. Ako izbor nije 
     * prazan, onda nastavljamo dalje dole u prvu putanju na listi izbora.
     * Ako pronadjemo izlaz, onda RjesenjeNadjeno se baci i mi generisemo
     * putanju rjesenja, koju vracamo. U bilo kom trenutku dok traje izvrsavanje,
     * lista prvih izbora se odnosi na trenutnu putanju, tj. ako je izbor na steku:
     *
     * <pre>
     * [[E W S] [E W] [S N] [N]]
     * </pre>
     *
     * Onda je trenutna putanja data sljedecom listom:
     *
     * <pre>
     * [E E S N]
     * </pre>
     */
    public List<Smjer> rjesi()
    {
        LinkedList<Izbor> izborStek = new LinkedList<Izbor>();
        Izbor izb;

        try
        {
            izborStek.push(prviIzbor(lavirint.getStart()));
            while (!izborStek.isEmpty())
            {
                izb = izborStek.peek();
                if (izb.isDeadend())
                {
                    // vrati se nazad.
                    izborStek.pop();
                    if (!izborStek.isEmpty()) izborStek.peek().izbori.pop();
                    continue;
                }
                izborStek.push(prati(izb.at, izb.izbori.peek()));
            }
            // Rjesenje nije nadjeno.
            return null;
        }
        catch (RjesenjeNadjeno e)
        {
            Iterator<Izbor> iter = izborStek.iterator();
            LinkedList<Smjer> solutionPath = new LinkedList<Smjer>();

            while (iter.hasNext())
            {
            	izb = iter.next();
                solutionPath.push(izb.izbori.peek());
            }

            if (lavirint.display != null) lavirint.display.updateDisplay();
            return putDoPunePutanje(solutionPath);
        }
    }
}
