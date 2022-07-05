package flLavirint;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;

/** Cuva matricu i obezbjedjuje funkcije za rad nad lavirintima */
public class Lavirint implements Serializable {
	/** <p>Lavirint predstavlja dvodimenzionalni niz (matricu) atomickih intedzera koji su kodirani kao AtomicIntegerArray.
	 * 	(Atomic (integer, i drugi osnovni tipovi) su varijable koje se dijele izmedju tredova.
		Inace varijablama se ne moze pristupiti iz razlicitih tredova, tj kad imas obicni int,
	 	on je dostupan samo u tom tredu, drugi tred ne moze vidjeti tu vrijednost.)
	 * 
	 * Gorni lijevi (sjevero-zapadni) cosak lavirinta je dat 
	 * pomocu lavirint.get(0). Donji desni (jugo-istocni) cosak lavirinta je dobijen pomocu
	 * lavirint.get(lavirint.length()-1).  Lavirint je cuvan na nacin "red prvo" (popunjavaju se redovi prvo).
	 *  Pa se element u redu n i koloni m dobija pomocu maze.get(n * width + m).</p>
	 * 
	 * <p>Ulaz u matricu je u redu: 0, koloni: sirina/2.
	 * Izlaz je u redu: visina-1, kolona: sirina/2.</p>
	 * 
	 * <p>Individualni bitovi svakog  bajta u nizu "lavirinta" se koriste da kodiraju
			informacije o tom polju lavirinta.  Samo se koriste 8-bitni bitovi nizeg reda.
	 * 		Naznaceni bitovi su:</p>
	 * <pre>  CCSSEEse</pre>
	 * <p>i imaju sljedece znacenje</p>
	 * <dl>  <dt><pre>CC</pre> <dd>oznacava dva bita koji se koriste da daju boju celiji (polju) u lavirintu.
			Svaka od 4 kombinacije bitova odgovara razlicitoj boji koja ce biti koristena u LavirintIzgled objektu
			kad se bude crtala ova celija. 
	 *   <dt><pre>SS</pre> <dd>oznacava dva bita koja se koriste da oboje juznu stranu ove celije
	 *   <dt><pre>EE</pre> <dd>oznacava dva bita koja se koriste da oboje istocnu stranu ove celije
	 *   <dt><pre>s</pre>  <dd>postavljen na 1 ako se nalazi zid na juznoj strani
	 *   <dt><pre>e</pre>  <dd>postavljen na 1 ako se nalazi zid na istocnoj strani
	 * 
	 * */
	public AtomicIntegerArray lavirint;
	public int sirina, visina;
	
	/**
	 * Referenca za graficki prikaz ovog lavirinta. 
	 * Bice null ako je graficki prikaz onemogucen.
	 */
	public transient LavirintIzgled display;

	private static final int EAST_COLOR_SHIFT = 2;
	private static final int SOUTH_COLOR_SHIFT = 4;
	private static final int CELL_COLOR_SHIFT = 6;
	private static final int CARVED_BIT = 1 << 8;
	private static final int EAST_COLOR_BITS = 3 << EAST_COLOR_SHIFT;
	private static final int SOUTH_COLOR_BITS = 3 << SOUTH_COLOR_SHIFT;
	private static final int CELL_COLOR_BITS = 3 << CELL_COLOR_SHIFT;
	private static final int SOUTH_BIT = 2;
	private static final int EAST_BIT = 1;
	
	// Varijable pozicija koje se koriste interno. Alocirane jednom ovde da bi se izbjegla pretjerana memorijska alokacija.
	private static final long serialVersionUID = 1L;

	//Vraca integer na dobijenu poziciju.
	public int dobijCeliju(Pozicija pos) {
		return lavirint.get(pos.kol + pos.red * sirina);
	}
	
	private void postaviCeliju(Pozicija pos, int val) {
		lavirint.set(pos.kol + pos.red * sirina, val);
	}
	
	boolean condPostaviCeliju(Pozicija pos, int oldVal, int newVal) {
		return lavirint.compareAndSet(pos.kol + pos.red * sirina, oldVal, newVal);
	}
	
	void postaviUrezano(Pozicija pos) {
		int newVal = dobijCeliju(pos) | CARVED_BIT;
		postaviCeliju(pos,newVal);
	}
	
	boolean uncarved(Pozicija pos) {
		if(pos.kol < 0 || pos.red < 0 || pos.kol > getWidth() - 1 || pos.red > getHeight() - 1)
			return false;
		else
			return (dobijCeliju(pos) & CARVED_BIT) == 0;
	}
	
	void ocistiIstok(Pozicija pos) {
		int newVal = dobijCeliju(pos) & ~EAST_BIT;
		postaviCeliju(pos,newVal);
	}
	void ocistiJug(Pozicija pos) {
		int newVal = dobijCeliju(pos) & ~SOUTH_BIT;
		postaviCeliju(pos,newVal);
	}
	void postaviIstok(Pozicija pos) {
		int newVal = dobijCeliju(pos) | EAST_BIT;
		postaviCeliju(pos,newVal);
	}	
	void postaviJug(Pozicija pos) {
		int newVal = dobijCeliju(pos) | SOUTH_BIT;
		postaviCeliju(pos,newVal);
	}

	/** Vraca true ako je moguce kretati se u pravcu @dir kada je na poziciji @pos. */
	public boolean mozeKretati(Pozicija pos, Smjer dir) {
		switch(dir) {
		case NORTH:
				if(pos.red == 0) return false;
				else return (dobijCeliju(pos.kreni(Smjer.NORTH)) & SOUTH_BIT) == 0;
		case SOUTH:
				return (dobijCeliju(pos) & SOUTH_BIT) == 0;
		case EAST:
				return (dobijCeliju(pos) & EAST_BIT) == 0;
		case WEST:
				if(pos.kol == 0) return false;
				else return (dobijCeliju(pos.kreni(Smjer.WEST)) & EAST_BIT) == 0;
		}
		
		return false; // nedostizno
	}
	
	/** Vraca listu otvorenih smjerova kretanja na ovoj poziciji.
		Smjer je otvoren ako nije blokiran zidom.
	 */
	public LinkedList<Smjer> dobijPoteze(Pozicija pos) {
		LinkedList<Smjer> result = new LinkedList<Smjer>();
		
		if(mozeKretati(pos,Smjer.SOUTH))
			result.add(Smjer.SOUTH);
		if(mozeKretati(pos,Smjer.EAST))
			result.add(Smjer.EAST);
		if(mozeKretati(pos,Smjer.WEST))
			result.add(Smjer.WEST);
		if(mozeKretati(pos,Smjer.NORTH))
			result.add(Smjer.NORTH);
		return result;
	}
	
	/** Vraca sirinu lavirinta
	 */
	public int getWidth() {
		return sirina;
	}
	
	/** Vraca visinu lavirinta.
	 */
	public int getHeight() {
		return visina;
	}
	
	/** Postavlja boju celije na poziciji 'pos' na odredjenu vrijednost,
		koja mora biti izmedju 0 i 3*/
	public void postaviBoju(Pozicija pos, int color) {
		int oldVal, newVal;
		
		color %= 4;
		color <<= CELL_COLOR_SHIFT;
		do {
			oldVal = dobijCeliju(pos);
			// pobrisi boju
			newVal = oldVal & ~CELL_COLOR_BITS;
			// postavi boju
			newVal = newVal | color;
		} while(!condPostaviCeliju(pos,oldVal,newVal));
	}
	
	/** Dobija boju celije na poziciji 'pos'.Vracena vrijednost
		mora biti izmedju 0 i 3 */
	public int dobijBoju(Pozicija pos) {
		return (dobijCeliju(pos) & CELL_COLOR_BITS) >> CELL_COLOR_SHIFT;
	}

	/** Postavlja boju coska u smjeru 'dir' na poziciji 'pos' na 
		odredjenu vrijednost (mora biti izmedju 0 i 3). Sjeverni zid
		najviseg reda celija i zapadni zid kolona na lijevoj strani ne smiju biti obojeni */
	public void postaviBoju(Pozicija pos, Smjer dir, int color) {
		switch(dir) {
		case NORTH:
			if(pos.red == 0)
				return;
			else
				postaviJuznuBoju(pos.kreni(Smjer.NORTH),color);
			break;
		case SOUTH:
			postaviJuznuBoju(pos,color);
			break;
		case EAST:
			postaviIstocnuBoju(pos,color);
			break;
		case WEST:
			if(pos.kol == 0)
				return;
			else
				postaviIstocnuBoju(pos.kreni(Smjer.WEST),color);
			break;
		}
	}

	/** Dobija boju coska u smjeru 'dir' na poziciji 'pos' 
		(mora biti izmedju 0 i 3). Sjeverni zid najviseg reda celija 
		i zapadni zid kolona na lijevoj strani ne mogu vratiti vrijednost. 
		 Boja 0 ce biti vracena u tom slucaju. */
	public int dobijBoju(Pozicija pos, Smjer dir) {
		switch(dir) {
		case NORTH:
			if(pos.red == 0)
				return 0;
			else
				return dobijJuznuBoju(pos.kreni(Smjer.NORTH));
		case SOUTH:
			return dobijJuznuBoju(pos);
		case EAST:
			return dobijIstocnuBoju(pos);
		case WEST:
			if(pos.kol == 0)
				return 0;
			else
				return dobijIstocnuBoju(pos.kreni(Smjer.WEST));
		}
		
		return 0; // nedostizno
	}

	/** Postavlja boju istocnog coska celije na poziciji 'pos' na odredjenu vrijednost (izmedju 0 i 3). */
	private void postaviIstocnuBoju(Pozicija pos, int color) {
		int oldVal, newVal;
		
		color %= 4;
		color <<= EAST_COLOR_SHIFT;
		do {
			oldVal = dobijCeliju(pos);
			// obrisi boju
			newVal = oldVal & ~EAST_COLOR_BITS;
			// postavi boju
			newVal = newVal | color;
		} while(!condPostaviCeliju(pos,oldVal,newVal));
	}

	/** Dobija boju istocnog coska celije na poziciji 'pos' (vrijednost izmedju 0 i 3). */
	private int dobijIstocnuBoju(Pozicija pos) {
		return (dobijCeliju(pos) & EAST_COLOR_BITS) >> EAST_COLOR_SHIFT;
	}

	/** Postavlja boju juznog coska celije na poziciji 'pos' na odredjenu vrijednost (izmedju 0 i 3). */
	private void postaviJuznuBoju(Pozicija pos, int color) {
		int oldVal, newVal;
		
		color %= 4;
		color <<= SOUTH_COLOR_SHIFT;
		do {
			oldVal = dobijCeliju(pos);
			// obrisi boju
			newVal = oldVal & ~SOUTH_COLOR_BITS;
			// postavi boju
			newVal = newVal | color;
		} while(!condPostaviCeliju(pos,oldVal,newVal));
	}

	/** Dobija boju juznog coska celije na poziciji 'pos' (vrijednost izmedju 0 i 3). */
	private int dobijJuznuBoju(Pozicija pos) {
		return (dobijCeliju(pos) & SOUTH_COLOR_BITS) >> SOUTH_COLOR_SHIFT;
	}

	/** Vraca poziciju koja odgovara ulazu u lavirint */
	public Pozicija getStart() {
		return new Pozicija(getWidth()/2, 0);
	}

	/** Vraca poziciju koja odgovara izlazu iz lavirinta */
	public Pozicija getEnd() {
		return new Pozicija(getWidth()/2, getHeight()-1);
	}
	
	/** Provjerava da li je rjesenje tacno.
	 * @return true ako je rjesenje tacno, false u suprotnom. */
	public final boolean provjeriRjesenje(List<Smjer> soln) {
		Pozicija at = getStart();
		Iterator<Smjer> iter = soln.iterator();
		while(iter.hasNext()) {
			Smjer dir = iter.next();
			if(!mozeKretati(at,dir))
				return false;
			at = at.kreni(dir);
		}
		return at.equals(getEnd());
	}
	//ispis lavirinta
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(sirina);
		out.writeInt(visina);
		Pozicija pos = new Pozicija(0,0);
		while(pos.red < visina) {
			pos = new Pozicija(0, pos.red + 1);
			while(pos.kol < sirina) {
				int bits = 0;
				int bit;
				for(bit = 0; bit < 16 && pos.kol < sirina; bit++, pos.kreni(Smjer.EAST)) {
					bits >>>= 2;
					bits |= mozeKretati(pos,Smjer.EAST) ? 0 : 0x40000000;
					bits |= mozeKretati(pos,Smjer.SOUTH) ? 0 : 0x80000000;
				}
				bits >>>= ((16 - bit) * 2);
				out.writeInt(bits);
			}
		}
	}
	//citanje lavirinta
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		sirina = in.readInt();
		visina = in.readInt();
		lavirint = new AtomicIntegerArray(sirina * visina);
		Pozicija pos = new Pozicija(0,0);
		while(pos.red < visina) {
			pos = new Pozicija(0, pos.red);
			while(pos.kol < sirina) {
				int bits = in.readInt();
				for(int bit = 0; bit < 16 && pos.kol < sirina; bit++, pos = pos.kreni(Smjer.EAST)) {
					if((bits & 1) == 1)
						postaviIstok(pos);
					if((bits & 2) == 2)
						postaviJug(pos);
					bits >>= 2;
				}
			}
			pos = pos.kreni(Smjer.SOUTH);
		}
	}
}
