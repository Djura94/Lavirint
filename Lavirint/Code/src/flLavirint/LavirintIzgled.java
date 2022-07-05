package flLavirint;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;

import javax.swing.JPanel;

/**
 * Obezbjedjuje je graficki izgled lavirinta i funkcije potrebne za update izgleda
 */
public class LavirintIzgled extends JPanel
{
    Lavirint lavirint;
    private Object lock = new Object();
    private int numFound, numDrawn;
    Hashtable<Integer, Pozicija> cellTypes = new Hashtable<Integer, Pozicija>(100);

    private class MListener implements MouseListener
    {
        public void mouseClicked(MouseEvent e)
        {
            synchronized (lock)
            {
                lock.notifyAll();
            }
        }

        public void mousePressed(MouseEvent e)
        {
        }

        public void mouseReleased(MouseEvent e)
        {
        }

        public void mouseEntered(MouseEvent e)
        {
        }

        public void mouseExited(MouseEvent e)
        {
        }
    }

    public LavirintIzgled(Lavirint maze)
    {
        super();
        setIgnoreRepaint(true);
        addMouseListener(new MListener());
        requestFocusInWindow();
        this.lavirint = maze;
    }

    public void waitForMouse()
    {
        try
        {
            synchronized (lock)
            {
                lock.wait();
            }
        }
        catch (InterruptedException e)
        {
            System.out.println("Prekinuto dok se ceka na pritisak dugmeta.");
        }
    }

    private Color lookupColor(int color)
    {
        switch (color)
        {
            case 0:
                return Color.LIGHT_GRAY;
            case 1:
                return Color.red;
            case 2:
                return Color.green;
            case 3:
                return Color.cyan;
        }
        return Color.LIGHT_GRAY;
    }

    private void nacrtajCelijuLavirinta(Pozicija pos, int x, int y, int width, int height, Graphics g)
    {
        Pozicija celija = cellTypes.get(lavirint.dobijCeliju(pos));
        if (celija != null)
        {
            g.copyArea(celija.kol, celija.red, width, height, x - celija.kol, y - celija.red);
            numFound++;
        }
        else
        {
            g.setColor(lookupColor(lavirint.dobijBoju(pos)));
            g.fillRect(x, y, width - 1, height - 1);
            if (lavirint.mozeKretati(pos, Smjer.EAST))
            {
                g.setColor(lookupColor(lavirint.dobijBoju(pos, Smjer.EAST)));
                g.drawLine(x + width - 1, y, x + width - 1, y + height - 2);
            }
            if (lavirint.mozeKretati(pos, Smjer.SOUTH))
            {
                g.setColor(lookupColor(lavirint.dobijBoju(pos, Smjer.SOUTH)));
                g.drawLine(x, y + height - 1, x + width - 2, y + height - 1);
            }
            cellTypes.put(lavirint.dobijCeliju(pos), new Pozicija(x, y));
        }
        numDrawn++;
    }

    private void nacrtajLavirint(Graphics g)
    {
        /*
         *	Saznati koliko velika moze biti celija. Rezervisati jedan piksel za
		 *	zid oko granice. Rezervisati jedan red iznad i ispod ulaza i izlaza
         */
        cellTypes.clear();
        numFound = 0;
        numDrawn = 0;
        int celijaSirina = (getWidth() - 2) / lavirint.getWidth();
        int celijaVisina = (getHeight() - 2) / (lavirint.getHeight() + 2);
        celijaSirina = Math.min(celijaSirina, celijaVisina);
        celijaVisina = celijaSirina;

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        Pozicija curr = new Pozicija(0, 0);
        for (int draw_y = celijaVisina + 1; curr.red < lavirint.getHeight(); curr = curr.kreni(Smjer.SOUTH), draw_y += celijaVisina)
        {
            curr = new Pozicija(0, curr.red);
            for (int draw_x = 2; curr.kol < lavirint.getWidth(); curr = curr.kreni(Smjer.EAST), draw_x += celijaSirina)
            {
                nacrtajCelijuLavirinta(curr, draw_x, draw_y, celijaSirina, celijaVisina, g);
            }
        }

        // Nacrtaj ulaz i izlaz
        curr = new Pozicija(lavirint.getWidth() / 2, 0);
        g.setColor(lookupColor(lavirint.dobijBoju(curr)));
        g.fillRect(lavirint.getWidth() / 2 * celijaSirina + 1, 0, celijaSirina - 1, celijaVisina + 1);
        curr = new Pozicija(lavirint.getWidth() / 2, lavirint.getHeight() - 1);
        g.setColor(lookupColor(lavirint.dobijBoju(curr)));
        g.fillRect(lavirint.getWidth() / 2 * celijaSirina + 1, (lavirint.getHeight() + 1) * celijaVisina, celijaSirina - 1, celijaVisina + 1);

       
    }

    public void paint(Graphics g)
    {
        nacrtajLavirint(g);
    }

    /**
     *	Zahtjeva update izgleda. Stvarni izgled ce se promjeniti asinhrono u drugom tredu.
     */
    public void updateDisplay()
    {
        repaint();
    }
}
