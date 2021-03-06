package symgeom.value;

public enum Sign
{
    POSITIVE, ZERO, NEGATIVE, UNKNOWN;

    public boolean isPositive()
    {
        return this == POSITIVE;
    }

    public boolean isZero()
    {
        return this == ZERO;
    }

    public boolean isNegative()
    {
        return this == NEGATIVE;
    }

    public boolean isUnknown()
    {
        return this == UNKNOWN;
    }

    public boolean isKnown()
    {
        return !isUnknown();
    }

    public int toInteger()
    {
        switch (this)
        {
            case POSITIVE:
            {
                return +1;
            }
            case NEGATIVE:
            {
                return -1;
            }
            case ZERO:
            {
                return 0;
            }
            default:
            {
                throw new IllegalStateException();
            }
        }
    }
}
