package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class DivideValue extends AbstractBinaryValue
{
    private DivideValue(Value left, Value right)
    {
        super(left, right);
    }

    @Override
    public Tribool lt(Value value)
    {
        // A/B < C -> A <?> BC
        Value a = getLeft().simplify();
        Value b = getRight().simplify();
        Value c = value;
        Value bc = b.multiply(c).simplify();
        Sign sign = b.getSign();
        if (sign.isPositive())
        {
            return a.lt(bc);
        }
        if (sign.isNegative())
        {
            return bc.lt(a);
        }
        return super.lt(value);
    }

    @Override
    public BinaryOperator getOperator()
    {
        return BinaryOperator.DIVIDE;
    }

    public static Value create(Value numerator, Value denominator)
    {
        return new DivideValue(numerator, denominator);
    }

}
