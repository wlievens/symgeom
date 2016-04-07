package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class SineValue extends AbstractUnaryValue
{
    private SineValue(Value operand)
    {
        super(operand);
    }

    public Value simplify()
    {
        Value operand = getOperand().simplify();
        if (operand == PI || (operand.isInteger() && operand.asInteger() == 0))
        {
            return Value.ZERO;
        }
        return create(operand);
    }

    @Override
    public UnaryOperator getOperator()
    {
        return UnaryOperator.SINE;
    }

    public static Value create(Value operand)
    {
        return new SineValue(operand);
    }
}
