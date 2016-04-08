package symgeom.value;

public class EValue extends AbstractConstantValue
{
    protected static final Value INSTANCE = new EValue();

    private EValue()
    {
    }

    public double approximate()
    {
        return Math.E;
    }

    public String getName()
    {
        return "e";
    }
}
