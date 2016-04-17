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
    public final boolean isAtom()
    {
        return true;
    }

    public abstract String getName();
}
