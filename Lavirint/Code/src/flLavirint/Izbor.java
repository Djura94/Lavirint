package flLavirint;

import java.util.Deque;
import java.util.LinkedList;

/**Predstavlja tacku izbora u lavirintu. 
 * Takodje moze se koristi za prikazivanje corsokaka 
 * (to je prazan niz izbora putanja).
 * */
public class Izbor
{
    /** Pozicija tacke izbora */
    public final Pozicija at;
    /** Smjer iz kog smo dosli do ove tacke */
    public final Smjer from;
    /**
     * Dozvoljeni izbori za sljedeci potez (dozvoljeni potezi minus
     * {@link #from}).
     */
    public final Deque<Smjer> izbori;

    Izbor(Pozicija at, Smjer from, LinkedList<Smjer> izbori)
    {
        this.at = at;
        this.izbori = izbori;
        this.from = from;
    }

    /**
     * Vraca true ako je izbor corsokak.
     * 
     * @return
     */
    boolean isDeadend()
    {
        return izbori.isEmpty();
    }
}