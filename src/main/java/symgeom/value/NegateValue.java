package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class NegateValue extends AbstractUnaryValue
{
    private NegateValue(Value operand)
    {
        super(operand);
    }

    @Override
    public Tribool lt(Value value)
    {
        value = value.simplify();
        if (value.isZero().isTrue())
        {
            // -A < 0  ->  TRUE if A>0
            if (getOperand().isStrictlyPositive().isTrue())
            {
                return Tribool.TRUE;
            }
        }
        return super.lt(value);
    }

    @Override
    public UnaryOperator getOperator()
    {
        return UnaryOperator.NEGATE;
    }

    public static Value create(Value operand)
    {
        return new NegateValue(operand);
    }
}
