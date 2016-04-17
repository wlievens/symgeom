package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class SignValue extends AbstractUnaryValue
{
    private SignValue(Value operand)
    {
        super(operand);
    }

    public Value old_simplify()
    {
        Value operand = getOperand().simplify();
        Sign sign = operand.getSign();
        if (sign.isNegative())
        {
            return number(-1);
        }
        if (sign.isPositive())
        {
            return number(+1);
        }
        if (sign.isZero())
        {
            return ZERO;
        }
        return create(operand);
    }

    public Tribool old_lt(Value value)
    {
        Value self = simplify();
        if (self.isInteger())
        {
            return self.lt(value);
        }
        return Tribool.UNKNOWN;
    }

    @Override
    public UnaryOperator getOperator()
    {
        return UnaryOperator.SIGN;
    }

    public static Value create(Value operand)
    {
        return new SignValue(operand);
    }
}
