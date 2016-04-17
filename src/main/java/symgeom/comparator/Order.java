package symgeom.comparator;

public enum Order
{
    UNKNOWN,
    LESSER,
    GREATER,
    EQUAL;

    public static Order of(int comparison)
    {
        if (comparison < 0)
        {
            return LESSER;
        }
        if (comparison > 0)
        {
            return GREATER;
        }
        return EQUAL;
    }

    public Order invert()
    {
        switch (this)
        {
            case LESSER:
            {
                return GREATER;
            }
            case GREATER:
            {
                return LESSER;
            }
            default:
            {
                return this;
            }
        }
    }
}
