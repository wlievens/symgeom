package symgeom.value;

public enum Tribool
{
    TRUE, FALSE, UNKNOWN;

    public boolean isTrue()
    {
        return this == TRUE;
    }

    public boolean isFalse()
    {
        return this == FALSE;
    }

    public boolean isUnknown()
    {
        return this == UNKNOWN;
    }

    public boolean isKnown()
    {
        return this != UNKNOWN;
    }

    public Tribool invert()
    {
        switch (this)
        {
            case TRUE:
            {
                return FALSE;
            }
            case FALSE:
            {
                return TRUE;
            }
            case UNKNOWN:
            {
                return UNKNOWN;
            }
            default:
            {
                throw new IllegalStateException();
            }
        }
    }

    public static Tribool of(boolean b)
    {
        return b ? TRUE : FALSE;
    }
}
