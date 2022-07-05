package flLavirint;

/**Pomocna klasa za dobijanje informacija o potezu*/

public class Potez
{
    public Pozicija from;
    public Smjer to;
    public Potez previous;
    
    public Potez() { } 
    
    public Potez(Pozicija from, Smjer to, Potez previous)
    {
        this.from = from;
        this.to = to;
        this.previous = previous;
    }
}
