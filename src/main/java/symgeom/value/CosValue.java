package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class CosValue extends AbstractUnaryValue
{
    private CosValue(Value operand)
    {
        super(operand);
    }

    @Override
    public UnaryOperator getOperator()
    {
        return UnaryOperator.COSINE;
    }

    public static Value create(Value operand)
    {
        return new CosValue(operand);
    }
}
