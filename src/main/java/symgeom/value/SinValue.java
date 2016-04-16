package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class SinValue extends AbstractUnaryValue
{
    private SinValue(Value operand)
    {
        super(operand);
    }

    @Override
    public UnaryOperator getOperator()
    {
        return UnaryOperator.SINE;
    }

    public static Value create(Value operand)
    {
        return new SinValue(operand);
    }
}
