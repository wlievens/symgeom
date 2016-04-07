package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class AbsValue extends AbstractUnaryValue
{
    private AbsValue(Value operand)
    {
        super(operand);
    }

    public Value simplify()
    {
        Value operand = getOperand().simplify();
        if (operand.isInteger())
        {
            return Value.number(Math.abs(operand.asInteger()));
        }
        return create(operand);
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
