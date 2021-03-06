package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class ModuloValue extends AbstractBinaryValue
{
    private ModuloValue(Value left, Value right)
    {
        super(left, right);
    }

    @Override
    public BinaryOperator getOperator()
    {
        return BinaryOperator.MODULO;
    }

    public static Value create(Value numerator, Value denominator)
    {
        return new ModuloValue(numerator, denominator);
    }
}
