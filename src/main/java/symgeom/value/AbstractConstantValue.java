package symgeom.value;

public abstract class AbstractConstantValue extends Value
{
    @Override
    public final int getDepth()
    {
        return 1;
    }

    public final Value old_simplify()
    {
        return this;
    }

    public final String toExpression(int precedence)
    {
        return getName();
    }

    @Override
    public String toPrefix()
    {
        return getName();
    }

    @Override
    public Tribool eqInternal(Value value)
    {
        if (this.equals(value.simplify()))
        {
            return Tribool.TRUE;
        }
        return Tribool.UNKNOWN;
    }

    @Override
    public Tribool lt(Value value)
    {
        return Tribool.UNKNOWN;
    }

    public abstract String getName();
}
