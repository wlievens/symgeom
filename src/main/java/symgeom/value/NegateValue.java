package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class NegateValue extends AbstractUnaryValue
{
    private NegateValue(Value operand)
    {
        super(operand);
    }

    public Value simplify()
    {
        Value operand = getOperand().simplify();
        if (operand.isInteger())
        {
            int number = operand.asInteger();
            if (number != Integer.MIN_VALUE)
            {
                return Value.number(-number);
            }
        }
        return create(operand);
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
