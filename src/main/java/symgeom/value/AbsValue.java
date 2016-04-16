package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class AbsValue extends AbstractUnaryValue
{
    private AbsValue(Value operand)
    {
        super(operand);
    }

    @Override
    public Tribool lt(Value value)
    {
        // ABS(A) < B
        Value b = value.simplify();
        Sign bSign = b.getSign();
        if (bSign.isZero())
        {
            return Tribool.FALSE;
        }
        if (bSign.isNegative())
        {
            return Tribool.FALSE;
        }
        return super.lt(value);
    }

    @Override
    public UnaryOperator getOperator()
    {
        return UnaryOperator.ABS;
    }

    public static Value create(Value operand)
    {
        return new AbsValue(operand);
    }
}
