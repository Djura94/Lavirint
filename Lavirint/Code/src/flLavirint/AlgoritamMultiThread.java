package flLavirint;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class AlgoritamMultiThread extends SkipLavirintRjesavac
{
    public AlgoritamMultiThread(Lavirint lavirint)
    {
        super(lavirint);
    }
    public static int count=0;

    public List<Smjer> rjesi()
    {
        List<Smjer> putanjaRjesenja = null;
        ExecutorService executor;
        LinkedList<PretragaMLThread> tasks = new LinkedList<PretragaMLThread>();
        List<Future<List<Smjer>>> buduci = new LinkedList<Future<List<Smjer>>>();

        // dobijanje runtime objekta koji je povezan sa trenutnom JVM (java virtuelnom masinom).
        Runtime runtime = Runtime.getRuntime();
        /** dobijanje broja procesora koji su slobodni za koristenje u ovoj JVM 
         * (kada zelimo sekvencijalno da radi algoritam, ovaj broj postavimo na 1)
         * */
        int numberOfProcessors = runtime.availableProcessors();

        //Inicijalizacija thread pool-a sa brojem tredova = broj trenutnih procesorskih jezgara u JVM
        executor = Executors.newFixedThreadPool(numberOfProcessors);

        try{
            Izbor start = prviIzbor(lavirint.getStart());

            int size = start.izbori.size();
            for(int index = 0; index < size; index++){
                Izbor curr = prati(start.at, start.izbori.peek());
                tasks.add(new PretragaMLThread(curr, start.izbori.pop())); // For petlja za dodavanje taskova u linked listu
            }

            buduci = executor.invokeAll(tasks); // Za svaki task smjesti u listu ugradjeni 'buduci' objekat koji sadrzi listu putanja
        } catch (RjesenjeNadjeno e1) {
            System.out.println("Nadjeno rjesenje; Pronadjen izuzetak");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();

        for(Future<List<Smjer>> result : buduci){
            try {
                if(result.get() != null){
                    putanjaRjesenja = result.get();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Ukupan broj obidjenih cvorova je: "+count);
        return putanjaRjesenja;

    }

    private class PretragaMLThread implements Callable<List<Smjer>>{
        Izbor current;
        Smjer followDir;
        public PretragaMLThread(Izbor current, Smjer followDir){
            this.current = current;
            this.followDir = followDir;

        }

        @Override
        public List<Smjer> call() {

            LinkedList<Izbor> choiceStack = new LinkedList<Izbor>();
            Izbor izb;

            try
            {
                choiceStack.push(this.current);
                count=count+1;
                
                /*Za sve moguce putanje dodaje u stek 
                 * moguce polje ce se pomjeriti iz te putanje i 
                 * skida sa steka ako je deadend*/
                
                while (!choiceStack.isEmpty()) 
                {
                    izb = choiceStack.peek();
                    if (izb.isDeadend())
                    {
                        // vrati se unazad.
                        choiceStack.pop();
                        if (!choiceStack.isEmpty()) choiceStack.peek().izbori.pop();
//                        if (maze.display != null)
//                        {
//                            maze.setColor(ch.at, 0);
//                        }
                        continue;
                    }
                    choiceStack.push(prati(izb.at, izb.izbori.peek()));
                    count++;
                }
                //Rjesenje nije nadjeno.
                return null;
            }
            catch (RjesenjeNadjeno e)
            {
                Iterator<Izbor> iter = choiceStack.iterator(); //Ako je solution found uzme sve sa steka i to predstavlja putanju do kraja
                LinkedList<Smjer> solutionPath = new LinkedList<Smjer>();
                while (iter.hasNext())
                {
                    izb = iter.next();
                    solutionPath.push(izb.izbori.peek());
                }
                solutionPath.push(followDir);

                if (lavirint.display != null) lavirint.display.updateDisplay();
                oznaciPutanju(solutionPath, 1);
                return putDoPunePutanje(solutionPath);
            }

        }

    }
}
