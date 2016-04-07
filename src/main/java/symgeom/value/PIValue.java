package symgeom.value;

public class PIValue extends AbstractConstantValue
{
    protected static final Value INSTANCE = new PIValue();

    private PIValue()
    {
    }

    public double approximate()
    {
        return Math.PI;
    }

    public String getName()
    {
        return "pi";
    }
}
