package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class AbsValue extends AbstractUnaryValue
{
    private AbsValue(Value operand)
    {
        super(operand);
    }

    public Value old_simplify()
    {
        Value operand = getOperand().simplify();
        if (operand.isInteger())
        {
            return Value.number(Math.abs(operand.asInteger()));
        }
        if (operand.isStrictlyPositive().isTrue())
        {
            return operand;
        }
        return create(operand);
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
