package flLavirint;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * Klasa potrebna da deserijalizuje ,(Serijalizacija je proces pretvaranja objekata u niz bajtova, 
 * tj. stream, iz kojeg se moze ponovo rekonstruisati objekat
 * (i to je deserijalizacija - pretvaranje byte stream u objekat)), je proces legacy fajlove.
 */
public class LavirintInputStream extends ObjectInputStream {

	public LavirintInputStream(InputStream in) throws IOException {
		super(in);
	}
	
	@Override
	protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
	    ObjectStreamClass desc = super.readClassDescriptor();
	    if (desc.getName().equals("cmsc433_p4.Maze")) {
	        return ObjectStreamClass.lookup(Lavirint.class);
	    }
	    return desc;
	};

}
