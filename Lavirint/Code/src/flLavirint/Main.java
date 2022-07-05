package flLavirint;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;


public class Main
{
    private Lavirint lavirint;
    private boolean rjesiv;

    /**
     * Metoda koja poziva sve algoritme za rjesavanje.
     */
    public void rjesi()
    {
        LavirintRjesavac[] rjesavaci =
        {
                new AlgoritamDubina(lavirint),
                new AlgoritamSirina(lavirint),
                new AlgoritamMultiThread(lavirint),  
        };

        for (LavirintRjesavac rjesavac : rjesavaci)
        {
            long pocetnoVrijeme, zavrsnoVrijeme;
            float sec;

            System.out.println();
            System.out.println(className(rjesavac.getClass()) + ":");

            pocetnoVrijeme = System.currentTimeMillis();
            List<Smjer> soln = rjesavac.rjesi();
            zavrsnoVrijeme = System.currentTimeMillis();
            sec = (zavrsnoVrijeme - pocetnoVrijeme) / 1000F;

            if (soln == null)
            {
                if (!rjesiv) System.out.println("Nije pronadjeno rjesenje nakon " + sec + " sekundi.");
                else System.out.println("Ne postoji rjsenje.");
            }
            else
            {
                if (lavirint.provjeriRjesenje(soln)) System.out.println("Tacno rjesenje lavirinta pronadjeno za " + sec + " sekundi.");
                else System.out.println("Netacno rjesenje nadjeno!");
            }
           
            //Da se razdvoje algoritmi
            /*try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
        }
    }

    public static void main(String[] args)
    {
        Main m = new Main();

 
        //Linije sluze za pokretanje programa bez cmdPrompta
        String lokacijaLavirinta = "C:\\Users\\fond\\Desktop\\Lavirint\\Code\\lavirinti\\";
        String nazivLavirinta = "200x200.mz"; //naziv lavirinta koji se rjesava
        String[] replaceArgs = {lokacijaLavirinta+nazivLavirinta};
        args = replaceArgs;
        
        File file = new File(args[0]);
        if (!file.exists()) {
            System.out.println("Fajl " + file.getAbsolutePath() + " ne postoji.");
            System.exit(-2);
        }
        try {
            m.read(args[0]);
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException za vrijeme citanja lavirinta: " + args[0]);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException za vrijeme citanja lavirinta: " + args[0]);
            e.printStackTrace();
        }
        
        // Sluzi za prikazivanje ekrana lavirinta
        m.initDisplay();
        
        m.rjesi();
    }
    

    private void read(String filename) throws IOException, ClassNotFoundException
    {
        LavirintInputStream in =
                new LavirintInputStream(new BufferedInputStream(new FileInputStream(filename)));
        lavirint = (Lavirint) in.readObject();
        rjesiv = in.readBoolean();
        in.close();
    }

    private String className(Class<?> cl)
    {
        StringBuffer fullname = new StringBuffer(cl.getName());
        String name = fullname.substring(fullname.lastIndexOf(".") + 1);
        return name;
    }
    
    
    private void initDisplay()
    {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int lavirintSirina = lavirint.getWidth();
        int lavirintVisina = lavirint.getHeight() + 2;
        int celijaSirina = (dim.width / lavirintSirina);
        int celijaVisina = (dim.height / lavirintVisina);
        int celijaVelicina = Math.min(celijaSirina, celijaVisina);

        if (celijaVelicina >= 2)
        {
            JFrame frame = new JFrame("Rjesavac lavirinta");
            LavirintIzgled display = new LavirintIzgled(lavirint);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            lavirint.display = display;
            frame.setSize(lavirintSirina * celijaVelicina, lavirintVisina * celijaVelicina);
            frame.setVisible(true);
            Insets insets = frame.getInsets();
            frame.setSize(lavirintSirina * celijaVelicina + insets.left + insets.right + 3,
                    lavirintVisina * celijaVelicina + insets.top + insets.bottom + 2);
            System.out.println(frame.getSize());
            frame.getContentPane().add(display);
        }
        else
        {
            System.out.println("Lavirint je preveliki i ne moze se prikazati na ekranu");
        }
    }
}
