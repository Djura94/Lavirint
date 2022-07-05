package flLavirint;

/** Represents a compass direction. */
public enum Smjer
{
    NORTH, EAST, SOUTH, WEST;

    /**
     * Returns the opposite compass direction. So reverse(NORTH) returns SOUTH,
     * and reverse(EAST) returns WEST.
     */
    public Smjer reverse()
    {
        switch (this)
        {
            case NORTH:
                return SOUTH;
            case SOUTH:
                return NORTH;
            case EAST:
                return WEST;
            case WEST:
                return EAST;
        }

        return null; // unreachable
    }
}
